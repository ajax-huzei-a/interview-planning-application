package com.intellistart.interviewplanning.model.booking

import com.intellistart.interviewplanning.domain.model.Period
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Booking(

    val id: ObjectId,

    val subject: String,

    val description: String,

    val interviewerSlotId: ObjectId,

    val candidateSlotId: ObjectId,

    val period: Period

)
