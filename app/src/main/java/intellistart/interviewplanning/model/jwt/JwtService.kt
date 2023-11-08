package intellistart.interviewplanning.model.jwt

import intellistart.interviewplanning.cache.CacheService
import intellistart.interviewplanning.controllers.dto.JwtRequest
import intellistart.interviewplanning.exceptions.SecurityException
import intellistart.interviewplanning.security.JwtUserDetails
import intellistart.interviewplanning.security.JwtUserDetailsService
import intellistart.interviewplanning.utils.FacebookUtil
import intellistart.interviewplanning.utils.JwtUtil
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
                val userScopes = Mono.fromCallable {
                    facebookUtil.getScope(jwtRequest.facebookToken)
                }.onErrorMap {
                    SecurityException(SecurityException.SecurityExceptionProfile.BAD_FACEBOOK_TOKEN)
                }

                userScopes.flatMap { scopes ->
                    val email = scopes[FacebookUtil.FacebookScopes.EMAIL]
                    val name = scopes[FacebookUtil.FacebookScopes.NAME]

                    authenticate(email)
                        .flatMap {
                            userDetailsService.loadUserByEmailAndName(email, name)
                        }
                        .map { userDetails ->
                            jwtUtil.generateToken(userDetails as JwtUserDetails)
                        }
                        .flatMap { jwt ->
                            cacheService.setInCache(jwtRequest.facebookToken, jwt)
                                .thenReturn(jwt)
                        }
                }
            }
    }

    private fun authenticate(username: String?): Mono<Authentication> {
        return Mono.just(UsernamePasswordAuthenticationToken(username, username))
            .flatMap(authenticationManager::authenticate)
            .onErrorMap {
                throw SecurityException(SecurityException.SecurityExceptionProfile.BAD_CREDENTIALS)
            }
    }
}
