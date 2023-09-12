package intellistart.interviewplanning.controllers.dto

/**
 * DTO for user with CANDIDATE role.
 */
data class CandidateDto(
    val email: String
) {
    val role = "CANDIDATE"
}
