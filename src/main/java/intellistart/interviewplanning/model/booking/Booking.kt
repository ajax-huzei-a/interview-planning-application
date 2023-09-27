package intellistart.interviewplanning.model.booking

import intellistart.interviewplanning.model.candidateslot.CandidateSlot
import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot
import intellistart.interviewplanning.model.period.Period
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

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
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Booking

        if (id != other.id) return false
        if (subject != other.subject) return false
        if (description != other.description) return false
        if (interviewerSlot != other.interviewerSlot) return false
        if (candidateSlot != other.candidateSlot) return false
        if (period != other.period) return false

        return true
    }
}
