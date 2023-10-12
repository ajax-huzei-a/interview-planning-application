package intellistart.interviewplanning.model.slot

import intellistart.interviewplanning.model.booking.Booking
import intellistart.interviewplanning.model.period.Period
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Slot(

    @Id
    var id: ObjectId = ObjectId(),

    var period: Period = Period(),

    var bookings: List<Booking> = listOf()

) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Slot

        if (id != other.id) return false
        if (period != other.period) return false
        if (bookings != other.bookings) return false

        return true
    }
}
