package intellistart.interviewplanning.model.period

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalTime

@Document
data class Period(

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var date: LocalDate = LocalDate.now(),

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    var from: LocalTime = LocalTime.now(),

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    var to: LocalTime = LocalTime.now()

) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Period

        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + to.hashCode()
        result = 31 * result + from.hashCode()
        return result
    }
}
