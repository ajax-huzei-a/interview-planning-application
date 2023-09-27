package intellistart.interviewplanning.model.period

import com.fasterxml.jackson.annotation.JsonIgnore
import intellistart.interviewplanning.model.booking.Booking
import intellistart.interviewplanning.model.candidateslot.CandidateSlot
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot
import java.time.LocalTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * Entity for period of time.
 */
@Entity
@Table(name = "periods")
data class Period(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "period_id")
    var id: Long = 0,

    @Column(name = "period_from")
    var from: LocalTime = LocalTime.now(),

    @Column(name = "period_to")
    var to: LocalTime = LocalTime.now(),

    @OneToMany(mappedBy = "period")
    @JsonIgnore
    var interviewerSlots: MutableSet<InterviewerSlot> = HashSet(),

    @OneToMany(mappedBy = "period")
    @JsonIgnore
    var candidateSlots: MutableSet<CandidateSlot> = HashSet(),

    @OneToMany(mappedBy = "period")
    @JsonIgnore
    var bookings: MutableSet<Booking> = HashSet()
) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Period

        if (id != other.id) return false
        if (from != other.from) return false
        if (to != other.to) return false
        if (interviewerSlots != other.interviewerSlots) return false
        if (candidateSlots != other.candidateSlots) return false
        if (bookings != other.bookings) return false

        return true
    }
}
