package intellistart.interviewplanning.model.user

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Column
import javax.persistence.EnumType
import javax.persistence.Enumerated

/**
 * User entity.
 */
@Entity
@Table(name = "users")
@JsonPropertyOrder("email", "role", "id")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var id: Long = 0,

    @Column(unique = true)
    var email: String = "",

    @Enumerated(EnumType.STRING)
    var role: Role = Role.INTERVIEWER
) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (email != other.email) return false
        if (role != other.role) return false

        return true
    }
}
