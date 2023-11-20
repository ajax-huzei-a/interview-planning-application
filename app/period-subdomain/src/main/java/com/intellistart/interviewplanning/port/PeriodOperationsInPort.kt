package com.intellistart.interviewplanning.port

import com.google.protobuf.Duration
import com.google.type.Date
import com.intellistart.interviewplanning.domain.model.Period
import java.time.LocalDate

interface PeriodOperationsInPort {

    fun obtainPeriod(fromString: String, toString: String, date: LocalDate): Period

    fun areOverlapping(period1: Period, period2: Period): Boolean

    fun isFirstInsideSecond(first: Period, second: Period): Boolean

    fun obtainPeriod(from: Duration, to: Duration, date: Date): Period
}
