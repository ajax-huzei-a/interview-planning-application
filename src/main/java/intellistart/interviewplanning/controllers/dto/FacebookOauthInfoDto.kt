package intellistart.interviewplanning.controllers.dto

/**
 * DTO for facebook client id.
 */
data class FacebookOauthInfoDto(
    var clientId: String,
    var redirectUri: String,
    var tokenRequestUrl: String
)
