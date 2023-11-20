package com.intellistart.interviewplanning.appltication.service

import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Component
class TimeService {

    fun convertToLocalTime(source: String?): LocalTime =
        runCatching {
            LocalTime.parse(source, formatterTime)
        }.getOrElse {
            throw IllegalArgumentException("Illegal data")
        }

    fun calculateDurationMinutes(from: LocalTime, to: LocalTime): Int {
        val duration = Duration.between(from, to)

        val minutes = duration.toMinutesPart()
        val hours = duration.toHoursPart()

        return hours * NUM_OF_MINUTES_IN_HOUR + minutes
    }

    companion object {
        private val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
        private const val NUM_OF_MINUTES_IN_HOUR = 60
    }
}