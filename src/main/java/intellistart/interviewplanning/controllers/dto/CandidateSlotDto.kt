package intellistart.interviewplanning.controllers.dto

import com.fasterxml.jackson.annotation.JsonFormat
import intellistart.interviewplanning.model.candidateslot.CandidateSlot
import java.time.LocalDate

/**
 * DTO for CandidateSlot.
 */
data class CandidateSlotDto(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var date:LocalDate = LocalDate.now(),
    var from: String = "",
    var to: String = ""
)

fun CandidateSlot.toDTO():CandidateSlotDto = CandidateSlotDto(
    date = date,
    from = period.from.toString(),
    to = period.to.toString()
)
