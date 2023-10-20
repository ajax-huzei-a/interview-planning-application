package intellistart.interviewplanning.controllers

import intellistart.interviewplanning.controllers.dto.FacebookOauthInfoDto
import intellistart.interviewplanning.controllers.dto.JwtRequest
import intellistart.interviewplanning.controllers.dto.JwtResponse
import intellistart.interviewplanning.model.user.Candidate
import intellistart.interviewplanning.model.user.UserService
import intellistart.interviewplanning.security.JwtUserDetails
import intellistart.interviewplanning.utils.FacebookUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class JwtAuthenticationController(
    private val userService: UserService,
) {

    @RequestMapping(value = ["/authenticate"], method = [RequestMethod.POST])
    fun createAuthenticationToken(
        @RequestBody jwtRequest: JwtRequest
    ): ResponseEntity<*> {
        return ResponseEntity.ok(JwtResponse(userService.getJwtToken(jwtRequest)))
    }

    @GetMapping("/me")
    fun getMyself(authentication: Authentication): ResponseEntity<*> {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        return userService.getUserByEmail(jwtUserDetails.email)
            ?.let { user -> ResponseEntity.ok(user) }
            ?: ResponseEntity.ok(
                userService.save(
                    Candidate().apply {
                        this.email = jwtUserDetails.email
                    }
                )
            )
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
