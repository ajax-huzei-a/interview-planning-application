package com.intellistart.interviewplanning.model.jwt

import com.intellistart.interviewplanning.cache.CacheService
import com.intellistart.interviewplanning.controllers.dto.JwtRequest
import com.intellistart.interviewplanning.exceptions.SecurityException
import com.intellistart.interviewplanning.security.JwtUserDetails
import com.intellistart.interviewplanning.security.JwtUserDetailsService
import com.intellistart.interviewplanning.utils.FacebookUtil
import com.intellistart.interviewplanning.utils.JwtUtil
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class JwtService(
    private val cacheService: CacheService,
    private val facebookUtil: FacebookUtil,
    private val userDetailsService: JwtUserDetailsService,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: ReactiveAuthenticationManager
) {

    fun getJwtToken(jwtRequest: JwtRequest): Mono<String> {
        return cacheService.getFromCache(jwtRequest.facebookToken)
            .switchIfEmpty {
                generateAndCacheJwt(jwtRequest)
            }
    }

    private fun generateAndCacheJwt(jwtRequest: JwtRequest): Mono<String> =
        Mono.fromCallable {
            facebookUtil.getScope(jwtRequest.facebookToken)
        }.onErrorMap {
            SecurityException(SecurityException.SecurityExceptionProfile.BAD_FACEBOOK_TOKEN)
        }
            .map { scopes ->
                scopes[FacebookUtil.FacebookScopes.EMAIL] to scopes[FacebookUtil.FacebookScopes.NAME]
            }
            .doOnNextMono { (email, _) -> authenticate(email) }
            .flatMap { (email, name) ->
                userDetailsService.loadUserByEmailAndName(email, name)
            }
            .map { userDetails ->
                jwtUtil.generateToken(userDetails as JwtUserDetails)
            }
            .doOnNextMono { jwt ->
                cacheService.setInCache(jwtRequest.facebookToken, jwt)
            }

    private fun authenticate(username: String?): Mono<Authentication> {
        return Mono.just(UsernamePasswordAuthenticationToken(username, username))
            .flatMap(authenticationManager::authenticate)
            .onErrorMap {
                throw SecurityException(SecurityException.SecurityExceptionProfile.BAD_CREDENTIALS)
            }
    }

    fun <T> Mono<T>.doOnNextMono(mapper: (T) -> Mono<*>): Mono<T> = flatMap(mapper).then(this)
}
