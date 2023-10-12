package intellistart.interviewplanning.model.booking

import intellistart.interviewplanning.model.period.Period
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Booking(

    var id: ObjectId = ObjectId(),

    var subject: String = "",

    var description: String = "",

    var interviewerSlotId: ObjectId = ObjectId(),

    var candidateSlotId: ObjectId = ObjectId(),

    var period: Period = Period()

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
        if (interviewerSlotId != other.interviewerSlotId) return false
        if (candidateSlotId != other.candidateSlotId) return false
        if (period != other.period) return false

        return true
    }
}
