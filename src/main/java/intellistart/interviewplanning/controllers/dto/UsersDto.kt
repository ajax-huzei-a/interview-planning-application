package intellistart.interviewplanning.controllers.dto

import intellistart.interviewplanning.model.user.User

data class UsersDto(
    val users: List<User>
)

fun List<User>.toUsersDto(): UsersDto = UsersDto(this)
