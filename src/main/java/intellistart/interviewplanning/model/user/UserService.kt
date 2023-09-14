package intellistart.interviewplanning.model.user

import intellistart.interviewplanning.controllers.dto.JwtRequest
import intellistart.interviewplanning.exceptions.SecurityException
import intellistart.interviewplanning.exceptions.UserException
import intellistart.interviewplanning.exceptions.UserException.UserExceptionProfile
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlotService
import intellistart.interviewplanning.security.JwtUserDetails
import intellistart.interviewplanning.security.JwtUserDetailsService
import intellistart.interviewplanning.utils.FacebookUtil
import intellistart.interviewplanning.utils.FacebookUtil.FacebookScopes
import intellistart.interviewplanning.utils.JwtUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientException
import redis.clients.jedis.JedisPooled

/**
 * Service for User entity.
 */
@Service
@Suppress("Many fields ain the constructor")
class UserService(
    private val userRepository: UserRepository,
    private val interviewerSlotService: InterviewerSlotService,
    private val jedis: JedisPooled,
    private val facebookUtil: FacebookUtil,
    private val userDetailsService: JwtUserDetailsService,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
) {

    @Value("\${jwt.caching}")
    private var jwtValidity: Long = 0L

    /**
     * Method for gaining Optional User by id.
     *
     * @return Optional User by id.
     */
    fun getUserById(id: Long): User = userRepository.findById(id)
        .orElseThrow { UserException(UserExceptionProfile.INVALID_INTERVIEWER) }

    /**
     * Returned the current user by given email.
     *
     * @param email - email on which the database will be searched.
     * @return User - user object with current info.
     */
    fun getUserByEmail(email: String): User? = userRepository.findByEmail(email)

    /**
     * Method for grant the user a role by email.
     *
     * @param email      - email address of the user to whom we will give the role.
     * @param roleOfUser - the role to grant the user.
     * @return User - user to whom we granted the role.
     * @throws UserException - when user already has role.
     */
    fun grantRoleByEmail(email: String, roleOfUser: Role): User {
        val existingUser = getUserByEmail(email)
        if (existingUser != null) {
            throw UserException(UserExceptionProfile.USER_ALREADY_HAS_ROLE)
        }

        return userRepository.save(User().apply {
            this.email = email
            this.role = roleOfUser
        })
    }

    /**
     * Method returned the list of users by given role from DB.
     *
     * @param role - role on which the database will be searched.
     * @return List of users by given role.
     */
    fun obtainUsersByRole(role: Role): List<User> = userRepository.findByRole(role)

    /**
     * Method will return the interviewer whom we will delete.
     * Before deleting, the method checks if the submitted id is really the interviewer.
     * The method also deletes all the interviewer's bookings and slots before deleting.
     *
     * @param id - the interviewer's id to delete.
     * @return User - the deleted user.
     * @throws UserException -
     *                       when the user has not interviewer role or not found by given id.
     */
    fun deleteInterviewer(id: Long): User {
        val user = userRepository.findById(id)
            .orElseThrow { UserException(UserExceptionProfile.USER_NOT_FOUND) }

        if (user.role != Role.INTERVIEWER) {
            throw UserException(UserExceptionProfile.NOT_INTERVIEWER)
        }

        interviewerSlotService.deleteSlotsByUser(user)

        userRepository.delete(user)
        return user
    }

    /**
     * Method will return the coordinator whom we will delete.
     * Before deleting, the method checks that the coordinator to be deleted is not himself.
     *
     * @param id                      - the coordinator's id to delete.
     * @param currentEmailCoordinator - email of current user.
     * @return User - the deleted user.
     * @throws UserException - when the coordinator removes himself,
     *     not found by given id or the user has not interviewer role.
     */
    fun deleteCoordinator(id: Long, currentEmailCoordinator: String): User {
        val user = userRepository.findById(id)
            .orElseThrow { UserException(UserExceptionProfile.USER_NOT_FOUND) }
        val currentUser = userRepository.findByEmail(currentEmailCoordinator)

        if (user.role != Role.COORDINATOR) {
            throw UserException(UserExceptionProfile.NOT_COORDINATOR)
        }

        if (user.id == currentUser?.id) {
            throw UserException(UserExceptionProfile.SELF_REVOKING)
        }

        userRepository.delete(user)
        return user
    }

    /**
     * Returns JWT by facebook token and caches it if it has not been cached before.
     *
     * @param jwtRequest - contains facebook token
     * @return jwt
     */
    fun getJwtToken(jwtRequest: JwtRequest): String {
        val fbCached = jedis.get(jwtRequest.facebookToken)

        if (fbCached != null) {
            return fbCached
        }

        val userScopes: Map<FacebookScopes, String> = try {
            facebookUtil.getScope(jwtRequest.facebookToken)
        } catch (e: RestClientException) {
            logger.warn("Failed to obtain user scope", e)
            throw SecurityException(SecurityException.SecurityExceptionProfile.BAD_FACEBOOK_TOKEN)
        }

        val email = userScopes[FacebookScopes.EMAIL]
        val name = userScopes[FacebookScopes.NAME]

        authenticate(email)

        val userDetails = userDetailsService.loadUserByEmailAndName(email, name) as JwtUserDetails

        val jwt = jwtUtil.generateToken(userDetails)

        jedis.setex(jwtRequest.facebookToken, jwtValidity, jwt)

        return jwt
    }

    private fun authenticate(username: String?) {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(username, username)
            )
        } catch (e: BadCredentialsException) {
            logger.warn("Failed to authenticate user {}", username, e)
            throw SecurityException(SecurityException.SecurityExceptionProfile.BAD_CREDENTIALS)
        }
    }

    companion object {
        private val logger: Logger = LogManager.getLogger(UserService::class.java)
    }
}
