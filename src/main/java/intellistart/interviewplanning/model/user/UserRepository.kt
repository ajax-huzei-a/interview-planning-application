package intellistart.interviewplanning.model.user

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val mongoTemplate: MongoTemplate) {
    fun findByEmail(email: String): User? {
        val query = Query().addCriteria(Criteria.where("email").`is`(email))
        return mongoTemplate.findOne(query, User::class.java)
    }

    fun save(user: User): User {
        mongoTemplate.save(user)
        return user
    }

    fun findByRole(role: Role): List<User> {
        val query = Query().addCriteria(Criteria.where("role").`is`(role))
        return mongoTemplate.find(query, User::class.java)
    }

    fun findById(id: ObjectId): User? {
        val query = Query().addCriteria(Criteria.where("_id").`is`(id))
        return mongoTemplate.findOne(query, User::class.java)
    }

    fun delete(user: User) {
        val query = Query().addCriteria(Criteria.where("_id").`is`(user.id))
        mongoTemplate.remove(query, User::class.java)
    }

    fun updateRoleOfUser(email: String, roleOfUser: Role): User {
        delete(findByEmail(email)!!)
        return when (roleOfUser) {
            Role.COORDINATOR -> save(
                Coordinator().apply {
                    this.email = email
                }
            )
            Role.INTERVIEWER -> save(
                Interviewer().apply {
                    this.email = email
                }
            )
            Role.CANDIDATE -> save(
                Candidate().apply {
                    this.email = email
                }
            )
        }
    }

    fun getDashboard(): List<User> {
        return mongoTemplate.find(Query(), User::class.java)
    }
}
