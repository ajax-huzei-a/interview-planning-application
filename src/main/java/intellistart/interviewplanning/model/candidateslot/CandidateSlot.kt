package intellistart.interviewplanning.model.candidateslot

import intellistart.interviewplanning.model.booking.Booking
import intellistart.interviewplanning.model.period.Period
import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * CandidateSlot entity.
 */
@Entity
@Table(name = "candidate_slots")
data class CandidateSlot(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_slot_id")
    var id: Long = 0,

    var date: LocalDate = LocalDate.now(),

    @ManyToOne
    @JoinColumn(name = "period_id")
    var period: Period = Period(),

    @OneToMany(mappedBy = "candidateSlot")
    var bookings: MutableSet<Booking> = HashSet(),

    var email: String = "",

    var name: String = ""
) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CandidateSlot

        if (id != other.id) return false
        if (date != other.date) return false
        if (period != other.period) return false
        if (bookings != other.bookings) return false
        if (email != other.email) return false
        if (name != other.name) return false

        return true
    }
}
