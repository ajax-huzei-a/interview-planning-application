package com.intellistart.interviewplanning.appltication.service

import com.google.protobuf.Duration
import com.google.type.Date
import com.intellistart.interviewplanning.appltication.port.PeriodOperationsInPort
import com.intellistart.interviewplanning.appltication.validation.PeriodValidator
import com.intellistart.interviewplanning.domain.exception.PeriodException
import com.intellistart.interviewplanning.domain.model.Period
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

@Service
class PeriodOperations(
    private val periodValidator: PeriodValidator,
    private val timeService: TimeService
) : PeriodOperationsInPort {

    override fun obtainPeriod(fromString: String, toString: String, date: LocalDate): Period {
        return runCatching {
            val from = timeService.convertToLocalTime(fromString)
            val to = timeService.convertToLocalTime(toString)
            obtainPeriod(from, to, date)
        }.getOrElse {
            throw PeriodException(PeriodException.PeriodExceptionProfile.INVALID_BOUNDARIES)
        }
    }

    private fun obtainPeriod(from: LocalTime, to: LocalTime, date: LocalDate): Period {
        periodValidator.validate(from, to, date)

        return Period(date, from, to)
    }

    override fun areOverlapping(period1: Period, period2: Period): Boolean {
        val from1 = period1.from
        val from2 = period2.from

        return isTimeInPeriod(from1, period2) || isTimeInPeriod(from2, period1)
    }

    override fun isFirstInsideSecond(first: Period, second: Period): Boolean =
        !first.from.isBefore(second.from) && !first.to.isAfter(second.to)

    private fun isTimeInPeriod(time: LocalTime, period: Period): Boolean =
        time.isAfter(period.from) && time.isBefore(period.to)

    override fun obtainPeriod(from: Duration, to: Duration, date: Date): Period {
        val localDate = LocalDate.of(date.year, date.month, date.day)
        val localTimeFrom = LocalTime.ofSecondOfDay(from.seconds)
        val localTimeTo = LocalTime.ofSecondOfDay(to.seconds)

        return obtainPeriod(localTimeFrom, localTimeTo, localDate)
    }
}
