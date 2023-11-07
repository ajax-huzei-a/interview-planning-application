package intellistart.interviewplanning.model.period

import com.google.protobuf.Duration
import com.google.type.Date
import intellistart.interviewplanning.exceptions.SlotException
import intellistart.interviewplanning.exceptions.SlotException.SlotExceptionProfile
import intellistart.interviewplanning.model.period.validation.PeriodValidator
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalTime

@Service
class PeriodService(
    private val periodValidator: PeriodValidator,
    private val timeService: TimeService
) {

    fun obtainPeriod(fromString: String, toString: String, date: LocalDate): Mono<Period> =
        Mono.zip(
            timeService.convertToLocalTime(fromString),
            timeService.convertToLocalTime(toString)
        ).onErrorMap {
            SlotException(SlotExceptionProfile.INVALID_BOUNDARIES)
        }.flatMap { tuple ->
            obtainPeriod(tuple.t1, tuple.t2, date)
        }

    private fun obtainPeriod(from: LocalTime, to: LocalTime, date: LocalDate): Mono<Period> {
        return periodValidator.validate(from, to, date)
            .map { Period(date, from, to) }
    }

    fun areOverlapping(period1: Period, period2: Period): Boolean {
        val from1 = period1.from
        val from2 = period2.from

        return isTimeInPeriod(from1, period2) || isTimeInPeriod(from2, period1)
    }

    fun isFirstInsideSecond(first: Period, second: Period): Boolean =
        !first.from.isBefore(second.from) && !first.to.isAfter(second.to)

    private fun isTimeInPeriod(time: LocalTime, period: Period): Boolean =
        time.isAfter(period.from) && time.isBefore(period.to)

    fun obtainPeriod(from: Duration, to: Duration, date: Date): Mono<Period> {
        val localDate = LocalDate.of(date.year, date.month, date.day)
        val localTimeFrom = LocalTime.ofSecondOfDay(from.seconds)
        val localTimeTo = LocalTime.ofSecondOfDay(to.seconds)

        return obtainPeriod(localTimeFrom, localTimeTo, localDate)
    }
}
