package intellistart.interviewplanning.model.slot

import intellistart.interviewplanning.model.booking.Booking
import intellistart.interviewplanning.model.period.Period

data class Slot(

    var id: Long = 0,

    var period: Period = Period(),

    var bookings: MutableSet<Booking> = HashSet()

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
