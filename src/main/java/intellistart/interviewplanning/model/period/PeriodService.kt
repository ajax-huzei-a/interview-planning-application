package intellistart.interviewplanning.model.period

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import intellistart.interviewplanning.model.period.validation.PeriodValidator
import org.springframework.stereotype.Service
import java.time.LocalTime

/**
 * Service for Period entity.
 */
@Service
class PeriodService(
    private val periodRepository: PeriodRepository,
    private val periodValidator: PeriodValidator,
    private val timeService: TimeService
) {

    /**
     * Alias for [obtainPeriod] with time conversion.
     *
     * @throws SlotException when parameters are invalid:
     * can't be read as time
     * wrong business logic
     */
    fun obtainPeriod(fromString: String, toString: String): Period {
        return runCatching {
            val from = timeService.convert(fromString)
            val to = timeService.convert(toString)
            obtainPeriod(from, to)
        }.getOrElse {
            throw SlotException(SlotExceptionProfile.INVALID_BOUNDARIES)
        }
    }

    /**
     * Obtain period by "from" and "to": find if exists, create if not.
     *
     * @param from - LocalTime lower time boundary
     * @param to - LocalTime upper time boundary
     *
     * @throws SlotException when wrong business logic.
     */
    private fun obtainPeriod(from: LocalTime, to: LocalTime): Period {
        periodValidator.validate(from, to)

        val periodOptional = periodRepository.findPeriodByFromAndTo(from, to)

        return periodOptional.orElseGet {
            periodRepository.save(
                Period(0L, from, to, hashSetOf(), hashSetOf(), hashSetOf())
            )
        }
    }

    /**
     * Tell if times of periods cross. Boundaries are inclusive.
     *
     * @return true if periods are overlapping.
     */
    fun areOverlapping(period1: Period, period2: Period): Boolean {
        val from1 = period1.from
        val from2 = period2.from

        return isTimeInPeriod(from1, period2) || isTimeInPeriod(from2, period1)
    }

    /**
     * Boundaries are inclusive.
     *
     * @return true if first period is inside the second period.
     */
    fun isFirstInsideSecond(first: Period, second: Period): Boolean =
        !first.from.isBefore(second.from) && !first.to.isAfter(second.to)

    /**
     * Tell if given time isn't smaller than "from" and smaller than "to".
     */
    private fun isTimeInPeriod(time: LocalTime, period: Period): Boolean =
        !time.isBefore(period.from) && time.isBefore(period.to)
}
