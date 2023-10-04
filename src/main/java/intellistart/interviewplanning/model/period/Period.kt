package intellistart.interviewplanning.model.period

import java.time.LocalDate
import java.time.LocalTime

data class Period(

    var id: Long = 0,

    var date: LocalDate = LocalDate.now(),

    var from: LocalTime = LocalTime.now(),

    var to: LocalTime = LocalTime.now(),

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

        return true
    }
}
