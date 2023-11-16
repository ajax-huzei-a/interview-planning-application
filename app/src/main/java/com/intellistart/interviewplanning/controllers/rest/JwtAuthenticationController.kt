package com.intellistart.interviewplanning.controllers.rest

import com.intellistart.interviewplanning.controllers.dto.FacebookOauthInfoDto
import com.intellistart.interviewplanning.controllers.dto.JwtRequest
import com.intellistart.interviewplanning.controllers.dto.JwtResponse
import com.intellistart.interviewplanning.model.jwt.JwtService
import com.intellistart.interviewplanning.model.user.Candidate
import com.intellistart.interviewplanning.model.user.User
import com.intellistart.interviewplanning.model.user.UserService
import com.intellistart.interviewplanning.security.JwtUserDetails
import com.intellistart.interviewplanning.utils.FacebookUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@RestController
@CrossOrigin
class JwtAuthenticationController(
    private val userService: UserService,
    private val jwtService: JwtService,
) {

    @RequestMapping(value = ["/authenticate"], method = [RequestMethod.POST])
    fun createAuthenticationToken(
        @RequestBody jwtRequest: JwtRequest
    ): Mono<JwtResponse> {
        return jwtService.getJwtToken(jwtRequest)
            .map { JwtResponse(it) }
    }

    @GetMapping("/me")
    fun getMyself(authentication: Authentication): Mono<User> {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        return userService.getUserByEmail(jwtUserDetails.email)
            .switchIfEmpty {
                userService.save(
                    Candidate().apply {
                        this.email = jwtUserDetails.email
                    }
                )
            }
    }

    @GetMapping("/oauth2/facebook/v15.0")
    fun getFacebookClientId(
        @Value("\${spring.security.oauth2.client.registration.facebook.clientId}") facebookClientId: String,
        @Value("\${spring.security.oauth2.client.registration.facebook.redirectUri}") redirectUri: String
    ): FacebookOauthInfoDto {
        val requestUrl = String.format(
            FacebookUtil.userFacebookTokenUrlV15,
            facebookClientId,
            redirectUri
        )
        return FacebookOauthInfoDto(facebookClientId, redirectUri, requestUrl)
    }
}
