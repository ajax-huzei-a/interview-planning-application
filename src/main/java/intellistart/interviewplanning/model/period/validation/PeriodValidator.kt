package intellistart.interviewplanning.model.period.validation

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import intellistart.interviewplanning.model.period.TimeService
import org.springframework.stereotype.Service
import java.time.LocalTime

/**
 * Business logic validator class.
 */
@Service
class PeriodValidator(
    private val timeService: TimeService,
) {

    /**
     * Validate lower and upper boundaries of future period.
     *
     *
     *  * minimal duration validation
     *  * extreme values validation
     *  * rounding of minutes validation.
     *
     *
     * @param from LocalTime, lower time boundary
     * @param to LocalTime, upper time boundary
     *
     * @throws SlotException  when validation is incorrect
     */
    fun validate(from: LocalTime, to: LocalTime) {
        checkExtremeValues(from, to)
        checkDuration(from, to)
        checkRoundingMinutes(from, to)
    }

    private fun checkRoundingMinutes(from: LocalTime, to: LocalTime) {
        if (!validateRoundingMinutes(from) || !validateRoundingMinutes(to)) {
            throw SlotException(SlotExceptionProfile.INVALID_BOUNDARIES)
        }
    }

    private fun validateRoundingMinutes(boundary: LocalTime): Boolean =
        boundary.minute == THIRTY_MINUTES || boundary.minute == 0

    private fun checkDuration(lowerBoundary: LocalTime, upperBoundary: LocalTime) {
        if (timeService.calculateDurationMinutes(lowerBoundary, upperBoundary) < MIN_DURATION) {
            throw SlotException(SlotExceptionProfile.INVALID_BOUNDARIES)
        }
    }

    private fun checkExtremeValues(lowerBoundary: LocalTime, upperBoundary: LocalTime) {
        if (!validateExtremeIsLowerCorrect(lowerBoundary) || !validateExtremeIsUpperCorrect(upperBoundary)) {
            throw SlotException(SlotExceptionProfile.INVALID_BOUNDARIES)
        }
    }

    private fun validateExtremeIsUpperCorrect(localTime: LocalTime): Boolean = when {
        localTime.hour > HIGHER_EXTREME -> false
        localTime.hour < HIGHER_EXTREME -> true
        else -> localTime.minute == 0
    }

    private fun validateExtremeIsLowerCorrect(localTime: LocalTime): Boolean =
        localTime.hour >= LOWER_EXTREME

    companion object {
        private const val THIRTY_MINUTES: Int = 30
        private const val MIN_DURATION: Int = 90
        private const val LOWER_EXTREME: Short = 8
        private const val HIGHER_EXTREME: Short = 22
    }
}
