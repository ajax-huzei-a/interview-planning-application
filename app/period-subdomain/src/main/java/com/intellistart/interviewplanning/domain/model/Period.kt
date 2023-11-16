package com.intellistart.interviewplanning.domain.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate
import java.time.LocalTime

data class Period(

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val date: LocalDate,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    val from: LocalTime,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    val to: LocalTime

)
