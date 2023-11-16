package com.intellistart.interviewplanning.slot.domain.model

import com.intellistart.interviewplanning.domain.model.Period
import com.intellistart.interviewplanning.model.booking.Booking

data class Slot(

    val id: String,

    val period: Period,

    val bookings: List<Booking>

)
