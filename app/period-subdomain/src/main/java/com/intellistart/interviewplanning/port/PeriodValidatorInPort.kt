package com.intellistart.interviewplanning.port

import java.time.LocalDate
import java.time.LocalTime

interface PeriodValidatorInPort {

    fun validate(from: LocalTime, to: LocalTime, date: LocalDate)
}
