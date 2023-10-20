package intellistart.interviewplanning.controllers.dto

import intellistart.interviewplanning.model.user.User

data class UsersDto(
    val users: List<User> = listOf()
)

fun List<User>.toUsersDto(): UsersDto = UsersDto(this)
