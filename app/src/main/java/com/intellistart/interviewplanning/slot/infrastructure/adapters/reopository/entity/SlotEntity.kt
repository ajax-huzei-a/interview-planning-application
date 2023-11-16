package com.intellistart.interviewplanning.slot.infrastructure.adapters.reopository.entity

import com.intellistart.interviewplanning.infrastructure.adapters.reopository.entity.PeriodEntity
import com.intellistart.interviewplanning.model.booking.Booking
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class SlotEntity(

    @Id
    val id: ObjectId,

    val period: PeriodEntity,

    val bookings: List<Booking>

)
