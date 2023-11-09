package intellistart.interviewplanning.model.user

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class UserRepository(private val reactiveMongoTemplate: ReactiveMongoTemplate) {

    fun findByEmail(email: String): Mono<User> {
        val query = Query().addCriteria(Criteria.where("email").`is`(email))
        return reactiveMongoTemplate.findOne(query, User::class.java)
    }

    fun save(user: User): Mono<User> {
        return reactiveMongoTemplate.save(user)
    }

    fun findByRole(role: Role): Flux<User> {
        val query = Query().addCriteria(Criteria.where("role").`is`(role))
        return reactiveMongoTemplate.find(query, User::class.java)
    }

    fun findById(id: ObjectId): Mono<User> {
        val query = Query().addCriteria(Criteria.where("_id").`is`(id))
        return reactiveMongoTemplate.findOne(query, User::class.java)
    }

    fun delete(user: User): Mono<User> {
        val query = Query().addCriteria(Criteria.where("_id").`is`(user.id))
        return reactiveMongoTemplate.remove(query, User::class.java)
            .thenReturn(user)
    }

    fun updateRoleOfUser(email: String, roleOfUser: Role): Mono<User> =
        findByEmail(email)
            .flatMap { existingUser ->
                delete(existingUser)
            }
            .then(
                Mono.defer {
                    val user: User = when (roleOfUser) {
                        Role.COORDINATOR -> Coordinator()
                        Role.INTERVIEWER -> Interviewer()
                        Role.CANDIDATE -> Candidate()
                    }
                    user.email = email
                    save(user)
                }
            )

    fun getDashboard(): Flux<User> = reactiveMongoTemplate.find(Query(), User::class.java)
}
