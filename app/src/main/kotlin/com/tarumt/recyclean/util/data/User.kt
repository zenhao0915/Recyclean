package com.tarumt.recyclean.util.data

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: String,
    val username: String? = null,
    val securityPin: String? = null,
    val role: String? = "Normal"
)

class User(
    val userNameWithEmail: String,
    var currentUserState: UserState = UserState.Normal
) {
    val userName: String
        get() = userNameWithEmail.replace("@recyclean.app", "")
    val isAdmin: Boolean
        get() = currentUserState == UserState.Admin
}

enum class UserState {
    Normal, Admin, ThirdParty
}