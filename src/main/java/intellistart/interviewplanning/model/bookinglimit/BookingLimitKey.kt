package intellistart.interviewplanning.model.bookinglimit

import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable

/**
 * Embedded entity (complex PK) for BookingLimit entity.
 */
@Embeddable
data class BookingLimitKey(

    @Column(name = "user_id")
    var userId: Long = 0,

    @Column(name = "week_id")
    var weekId: Long = 0
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BookingLimitKey) return false
        return userId == other.userId && weekId == other.weekId
    }

    override fun hashCode(): Int {
        return Objects.hash(userId, weekId)
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
