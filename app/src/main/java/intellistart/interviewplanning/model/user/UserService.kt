package intellistart.interviewplanning.model.user

import intellistart.interviewplanning.exceptions.UserException
import intellistart.interviewplanning.exceptions.UserException.UserExceptionProfile
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Service
@Suppress("LongParameterList")
class UserService(
    private val userRepository: UserRepository
) {

    fun getUserByEmail(email: String): Mono<User> = userRepository.findByEmail(email)

    fun grantRoleByEmail(email: String, roleOfUser: Role): Mono<User> = userRepository.findByEmail(email)
        .flatMap { existingUser ->
            if (existingUser.role == roleOfUser) {
                Mono.error(UserException(UserExceptionProfile.USER_ALREADY_HAS_ROLE))
            } else {
                userRepository.updateRoleOfUser(email, roleOfUser)
            }
        }
        .switchIfEmpty(
            userRepository.save(
                when (roleOfUser) {
                    Role.COORDINATOR -> Coordinator()
                    Role.INTERVIEWER -> Interviewer()
                    Role.CANDIDATE -> Candidate()
                }.apply { this.email = email }
            )
        )

    fun getUsersByRole(role: Role): Flux<User> = userRepository.findByRole(role)

    fun deleteInterviewer(id: ObjectId): Mono<User> = userRepository.findById(id)
        .switchIfEmpty { UserException(UserExceptionProfile.USER_NOT_FOUND).toMono() }
        .flatMap { user ->
            if (user.role != Role.INTERVIEWER) {
                Mono.error(UserException(UserExceptionProfile.NOT_INTERVIEWER))
            } else {
                userRepository.delete(user)
            }
        }

    @Suppress("ThrowsCount")
    fun deleteCoordinator(id: ObjectId, currentEmailCoordinator: String): Mono<User> = userRepository.findById(id)
        .switchIfEmpty { UserException(UserExceptionProfile.USER_NOT_FOUND).toMono() }
        .flatMap { user ->
            userRepository.findByEmail(currentEmailCoordinator)
                .switchIfEmpty { UserException(UserExceptionProfile.USER_NOT_FOUND).toMono() }
                .flatMap { currentUser ->
                    if (user.role != Role.COORDINATOR) {
                        Mono.error(UserException(UserExceptionProfile.NOT_COORDINATOR))
                    } else if (user.id == currentUser.id) {
                        Mono.error(UserException(UserExceptionProfile.SELF_REVOKING))
                    } else {
                        userRepository.delete(user)
                    }
                }
        }

    fun save(user: User): Mono<User> = userRepository.save(user)

    fun getDashboard(): Flux<User> = userRepository.getDashboard()
}
