package intellistart.interviewplanning.controllers

import intellistart.interviewplanning.controllers.dto.CandidateDto
import intellistart.interviewplanning.controllers.dto.FacebookOauthInfoDto
import intellistart.interviewplanning.controllers.dto.JwtRequest
import intellistart.interviewplanning.controllers.dto.JwtResponse
import intellistart.interviewplanning.exceptions.SecurityException
import intellistart.interviewplanning.exceptions.SecurityException.SecurityExceptionProfile
import intellistart.interviewplanning.model.user.UserService
import intellistart.interviewplanning.security.JwtUserDetails
import intellistart.interviewplanning.security.JwtUserDetailsService
import intellistart.interviewplanning.utils.FacebookUtil
import intellistart.interviewplanning.utils.FacebookUtil.FacebookScopes
import intellistart.interviewplanning.utils.JwtUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.client.RestClientException
import redis.clients.jedis.JedisPooled

/**
 * Controller for authentication and authenticated requests.
 */
@RestController
@CrossOrigin
class JwtAuthenticationController(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil,
    private val userDetailsService: JwtUserDetailsService,
    private val facebookUtil: FacebookUtil,
    private val userService: UserService,
    private val jedis: JedisPooled
) {
    @Value("\${jwt.caching}")
    private val jwtValidity: Long = 0

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
        val fbCached = jedis[jwtRequest.facebookToken]
        if (fbCached != null) {
            return ResponseEntity.ok(JwtResponse(fbCached))
        }
        val userScopes: Map<FacebookScopes, String> = try {
            facebookUtil.getScope(jwtRequest.facebookToken)
        } catch (e: RestClientException) {
            throw SecurityException(SecurityExceptionProfile.BAD_FACEBOOK_TOKEN)
        }
        val email = userScopes[FacebookScopes.EMAIL]
        val name = userScopes[FacebookScopes.NAME]
        authenticate(email)
        val userDetails = userDetailsService
            .loadUserByEmailAndName(email, name) as JwtUserDetails
        val jwt = jwtUtil.generateToken(userDetails)
        jedis.setex(jwtRequest.facebookToken, jwtValidity, jwt)
        return ResponseEntity.ok(JwtResponse(jwt))
    }

    private fun authenticate(username: String?) {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(username, username)
            )
        } catch (e: BadCredentialsException) {
            throw SecurityException(SecurityExceptionProfile.BAD_CREDENTIALS)
        }
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
        val user = userService.getUserByEmail(jwtUserDetails.email)
            ?: return ResponseEntity.ok(CandidateDto(jwtUserDetails.email))
        return ResponseEntity.ok(user)
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
            facebookClientId, redirectUri
        )
        return FacebookOauthInfoDto(facebookClientId, redirectUri, requestUrl)
    }
}