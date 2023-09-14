package intellistart.interviewplanning.model.period

import intellistart.interviewplanning.model.user.UserService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
    fun convert(source: String?): LocalTime {
        return try {
            LocalTime.parse(source, formatter)
        } catch (e: DateTimeParseException) {
            logger.warn("Failed to parse to LocalDate", e)
            throw IllegalArgumentException("Illegal data")
        }
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
        private val logger: Logger = LogManager.getLogger(UserService::class.java)
    }
}
