package intellistart.interviewplanning.model.booking

import intellistart.interviewplanning.model.candidateslot.CandidateSlot
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot
import intellistart.interviewplanning.model.period.Period
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Column
import javax.persistence.ManyToOne
import javax.persistence.JoinColumn

/**
 * Booking entity.
 */
@Entity
@Table(name = "bookings")
class Booking(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    var id: Long = 0,

    var subject: String = "",

    var description: String = "",

    @ManyToOne
    @JoinColumn(name = "interviewer_slot_id")
    var interviewerSlot: InterviewerSlot = InterviewerSlot(),

    @ManyToOne
    @JoinColumn(name = "candidate_slot_id")
    var candidateSlot: CandidateSlot = CandidateSlot(),

    @ManyToOne
    @JoinColumn(name = "period_id")
    var period: Period = Period(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Booking) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
