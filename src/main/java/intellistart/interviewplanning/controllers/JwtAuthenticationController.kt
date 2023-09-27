package intellistart.interviewplanning.controllers

import intellistart.interviewplanning.controllers.dto.CandidateDto
import intellistart.interviewplanning.controllers.dto.FacebookOauthInfoDto
import intellistart.interviewplanning.controllers.dto.JwtRequest
import intellistart.interviewplanning.controllers.dto.JwtResponse
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

/**
 * Controller for authentication and authenticated requests.
 */
@RestController
@CrossOrigin
class JwtAuthenticationController(
    private val userService: UserService,
) {
    /**
     * Method that mappings the authentication request through generating
     * JWT by Facebook Token.
     *
     * @param jwtRequest object with facebookToken field - gained by user oauth2 token
     * @return JWT
     */
    @RequestMapping(value = ["/authenticate"], method = [RequestMethod.POST])
    fun createAuthenticationToken(
        @RequestBody jwtRequest: JwtRequest
    ): ResponseEntity<*> {
        return ResponseEntity.ok(JwtResponse(userService.getJwtToken(jwtRequest)))
    }

    /**
     * GET request for getting info about current User.
     *
     * @param authentication - Spring security auth object.
     *
     * @return User - user object with current info.
     */
    @GetMapping("/me")
    fun getMyself(authentication: Authentication): ResponseEntity<*> {
        val jwtUserDetails = authentication.principal as JwtUserDetails
        return userService.getUserByEmail(jwtUserDetails.email)
            ?.let { user -> ResponseEntity.ok(user) }
            ?: ResponseEntity.ok(CandidateDto(jwtUserDetails.email))
    }

    /**
     * GET request for getting application facebook client id.
     *
     * @param facebookClientId auto-injected from environmental variables facebook client id.
     * @return DTO with simple string.
     */
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
