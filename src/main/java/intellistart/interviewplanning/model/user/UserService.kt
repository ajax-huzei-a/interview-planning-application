package intellistart.interviewplanning.model.user

import intellistart.interviewplanning.controllers.dto.JwtRequest
import intellistart.interviewplanning.exceptions.SecurityException
import intellistart.interviewplanning.exceptions.UserException
import intellistart.interviewplanning.exceptions.UserException.UserExceptionProfile
import intellistart.interviewplanning.security.JwtUserDetails
import intellistart.interviewplanning.security.JwtUserDetailsService
import intellistart.interviewplanning.utils.FacebookUtil
import intellistart.interviewplanning.utils.FacebookUtil.FacebookScopes
import intellistart.interviewplanning.utils.JwtUtil
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import redis.clients.jedis.JedisPooled

@Service
@Suppress("LongParameterList")
class UserService(
    private val userRepository: UserRepository,
    private val jedis: JedisPooled,
    private val facebookUtil: FacebookUtil,
    private val userDetailsService: JwtUserDetailsService,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
) {

    @Value("\${jwt.caching}")
    private var jwtValidity: Long = 0L

    fun getUserByEmail(email: String): User? = userRepository.findByEmail(email)

    fun grantRoleByEmail(email: String, roleOfUser: Role): User {
        val existingUser = getUserByEmail(email)
        if ((existingUser != null) && (existingUser.role == roleOfUser)) {
            throw UserException(UserExceptionProfile.USER_ALREADY_HAS_ROLE)
        }

        return if (existingUser != null) {
            userRepository.updateRoleOfUser(email, roleOfUser)
        } else {
            userRepository.save(
                when (roleOfUser) {
                    Role.COORDINATOR -> Coordinator().apply { this.email = email }
                    Role.INTERVIEWER -> Interviewer().apply { this.email = email }
                    Role.CANDIDATE -> Candidate().apply { this.email = email }
                }
            )
        }
    }

    fun getUsersByRole(role: Role): List<User> = userRepository.findByRole(role)

    fun deleteInterviewer(id: ObjectId): User {
        val user = userRepository.findById(id)
            ?: throw UserException(UserExceptionProfile.USER_NOT_FOUND)

        if (user.role != Role.INTERVIEWER) {
            throw UserException(UserExceptionProfile.NOT_INTERVIEWER)
        }

        userRepository.delete(user)

        return user
    }

    @Suppress("ThrowsCount")
    fun deleteCoordinator(id: ObjectId, currentEmailCoordinator: String): User {
        val user = userRepository.findById(id)
            ?: throw UserException(UserExceptionProfile.USER_NOT_FOUND)

        val currentUser = userRepository.findByEmail(currentEmailCoordinator)
            ?: throw UserException(UserExceptionProfile.USER_NOT_FOUND)

        if (user.role != Role.COORDINATOR) {
            throw UserException(UserExceptionProfile.NOT_COORDINATOR)
        }

        if (user.id == currentUser.id) {
            throw UserException(UserExceptionProfile.SELF_REVOKING)
        }

        userRepository.delete(user)
        return user
    }

    fun save(user: User) = userRepository.save(user)

    fun getDashboard() = userRepository.getDashboard()

    fun getJwtToken(jwtRequest: JwtRequest): String {
        val fbCached = jedis.get(jwtRequest.facebookToken)

        if (fbCached != null) {
            return fbCached
        }

        val userScopes: Map<FacebookScopes, String> =
            runCatching {
                facebookUtil.getScope(jwtRequest.facebookToken)
            }.getOrElse {
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
        runCatching {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(username, username)
            )
        }.getOrElse {
            throw SecurityException(SecurityException.SecurityExceptionProfile.BAD_CREDENTIALS)
        }
    }
}
