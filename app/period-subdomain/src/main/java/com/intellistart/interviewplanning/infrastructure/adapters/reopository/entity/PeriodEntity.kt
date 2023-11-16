package com.intellistart.interviewplanning.infrastructure.adapters.reopository.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.intellistart.interviewplanning.domain.model.Period
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalTime

@Document
data class PeriodEntity(

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val date: LocalDate,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    val from: LocalTime,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    val to: LocalTime

)

fun Period.toEntity() = PeriodEntity(
    date = date,
    from = from,
    to = to
)

fun PeriodEntity.toDomain() = Period(
    date = date,
    from = from,
    to = to
)
