package intellistart.interviewplanning.model.candidateslot

import intellistart.interviewplanning.model.booking.Booking
import intellistart.interviewplanning.model.period.Period
import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Column
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn
import javax.persistence.OneToMany

/**
 * CandidateSlot entity.
 */
@Entity
@Table(name = "candidate_slots")
data class CandidateSlot (

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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CandidateSlot) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
