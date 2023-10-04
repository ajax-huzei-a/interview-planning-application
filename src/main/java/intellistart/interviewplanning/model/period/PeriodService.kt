package intellistart.interviewplanning.model.period

import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import intellistart.interviewplanning.model.period.validation.PeriodValidator
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

@Service
class PeriodService(
    private val periodValidator: PeriodValidator,
    private val timeService: TimeService
) {

    fun obtainPeriod(fromString: String, toString: String, date: LocalDate): Period {
        return runCatching {
            val from = timeService.convert(fromString)
            val to = timeService.convert(toString)
            obtainPeriod(from, to, date)
        }.getOrElse {
            throw SlotException(SlotExceptionProfile.INVALID_BOUNDARIES)
        }
    }

    private fun obtainPeriod(from: LocalTime, to: LocalTime, date: LocalDate): Period {
        periodValidator.validate(from, to, date)

        return Period(0L, date, from, to)
    }

    fun areOverlapping(period1: Period, period2: Period): Boolean {
        val from1 = period1.from
        val from2 = period2.from

        return isTimeInPeriod(from1, period2) || isTimeInPeriod(from2, period1)
    }

    fun isFirstInsideSecond(first: Period, second: Period): Boolean =
        !first.from.isBefore(second.from) && !first.to.isAfter(second.to)

    private fun isTimeInPeriod(time: LocalTime, period: Period): Boolean =
        !time.isBefore(period.from) && time.isBefore(period.to)
}
