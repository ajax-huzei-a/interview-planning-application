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
    override fun hashCode(): Int {
        return Objects.hash(userId, weekId)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookingLimitKey

        if (userId != other.userId) return false
        if (weekId != other.weekId) return false

        return true
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}
