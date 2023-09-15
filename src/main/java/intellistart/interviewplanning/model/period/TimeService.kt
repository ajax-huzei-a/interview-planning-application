package intellistart.interviewplanning.model.period

import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Utils class to perform time operations.
 */
@Component
class TimeService {

    /**
     * Convert String to LocalTime by pattern HH:mm.
     *
     * @throws IllegalArgumentException if String doesn't satisfy the pattern
     */
    fun convert(source: String?): LocalTime =
        runCatching {
            LocalTime.parse(source, formatter)
        }.getOrElse {
            throw IllegalArgumentException("Illegal data")
        }

    /**
     * Calculate duration from "from" to "to" in minutes.
     *
     * @param from - LocalTime
     * @param to - LocalTime
     */
    fun calculateDurationMinutes(from: LocalTime?, to: LocalTime?): Int {
        val duration = Duration.between(from, to)

        val minutes = duration.toMinutesPart()
        val hours = duration.toHoursPart()

        return hours * NUM_OF_MINUTES_IN_HOUR + minutes
    }

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("HH:mm")
        private const val NUM_OF_MINUTES_IN_HOUR = 60
    }
}
