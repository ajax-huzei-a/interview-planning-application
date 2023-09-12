package intellistart.interviewplanning.controllers.dto

import intellistart.interviewplanning.model.interviewerslot.InterviewerSlot

/**
 * Class for Interviewer Slot DTO Request.
 */
data class InterviewerSlotDtoRequest(
    var week: Long = 0,
    var dayOfWeek: String = "",
    var from: String = "",
    var to: String = ""
)

/**
 * Class for Interviewer Slot DTO Response.
 */
data class InterviewerSlotDtoResponse(
    val interviewerId: Long,
    val interviewerSlotId: Long,
    val week: Long,
    val dayOfWeek: String,
    val from: String,
    val to: String
)

fun InterviewerSlot.toDtoResponse(): InterviewerSlotDtoResponse = InterviewerSlotDtoResponse(
    interviewerId = user.id,
    interviewerSlotId = id,
    week = week.id,
    dayOfWeek = dayOfWeek.toString(),
    from = period.from.toString(),
    to = period.to.toString()
)
