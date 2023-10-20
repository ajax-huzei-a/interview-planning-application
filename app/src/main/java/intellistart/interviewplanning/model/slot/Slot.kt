package intellistart.interviewplanning.model.slot

import intellistart.interviewplanning.model.booking.Booking
import intellistart.interviewplanning.model.period.Period
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Slot(

    @Id
    val id: ObjectId,

    val period: Period,

    val bookings: List<Booking>

)
