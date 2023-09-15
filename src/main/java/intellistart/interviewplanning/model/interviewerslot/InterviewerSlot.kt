package intellistart.interviewplanning.model.interviewerslot

import com.fasterxml.jackson.annotation.JsonIgnore
import intellistart.interviewplanning.model.booking.Booking
import intellistart.interviewplanning.model.dayofweek.DayOfWeek
import intellistart.interviewplanning.model.period.Period
import intellistart.interviewplanning.model.user.User
import intellistart.interviewplanning.model.week.Week
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Column
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Enumerated

/**
 * InterviewerSlot entity.
 */
@Entity
@Table(name = "interviewer_slots")
data class InterviewerSlot(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interviewer_slot_id")
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "week_id")
    var week: Week = Week(),

    @Enumerated
    var dayOfWeek: DayOfWeek = DayOfWeek.MON,

    @ManyToOne
    @JoinColumn(name = "period_id")
    var period: Period = Period(),

    @JsonIgnore
    @OneToMany(mappedBy = "interviewerSlot")
    var bookings: MutableSet<Booking> = HashSet(),

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User = User()
) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InterviewerSlot

        if (id != other.id) return false
        if (week != other.week) return false
        if (dayOfWeek != other.dayOfWeek) return false
        if (period != other.period) return false
        if (bookings != other.bookings) return false
        if (user != other.user) return false

        return true
    }
}
