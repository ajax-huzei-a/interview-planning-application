package intellistart.interviewplanning.model.user

import org.springframework.stereotype.Repository

@Repository
class UserRepository {
    fun findByEmail(email: String): User? {
        TODO("Not yet implemented")
    }

    fun save(apply: User): User {
        TODO("Not yet implemented")
    }

    fun findByRole(role: Role): List<User> {
        TODO("Not yet implemented")
    }

    fun findById(id: Long): User? {
        TODO("Not yet implemented")
    }

    fun delete(user: User) {
        TODO("Not yet implemented")
    }

    fun updateRoleOfUser(email: String, roleOfUser: Role): User {
        TODO("Not yet implemented")
    }

    fun getDashboard() {
        TODO("Not yet implemented")
    }
}
