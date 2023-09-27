package intellistart.interviewplanning.model.bookinglimit

import intellistart.interviewplanning.model.user.User
import intellistart.interviewplanning.model.week.Week
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.Table

/**
 * Entity for storing limit of booking per week for users with Interviewer role.
 */
@Entity
@Table(name = "booking_limits")
data class BookingLimit(

    @EmbeddedId
    var id: BookingLimitKey = BookingLimitKey(),

    @Column(name = "booking_limit")
    var bookingLimit: Int = 0,

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    var user: User = User(),

    @ManyToOne
    @MapsId("weekId")
    @JoinColumn(name = "week_id")
    var week: Week = Week()
) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookingLimit

        if (id != other.id) return false
        if (bookingLimit != other.bookingLimit) return false
        if (user != other.user) return false
        if (week != other.week) return false

        return true
    }
}
