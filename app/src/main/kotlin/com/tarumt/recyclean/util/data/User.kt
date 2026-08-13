package com.tarumt.recyclean.util.data

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: String,
    val username: String? = null,
    val security_pin: String? = null,
    val role: String? = "Normal"
)

class User(
    val userNameWithEmail: String,
    password: Int,
    var currentUserState: UserState = UserState.Normal
) {
    val userName: String
        get() = userNameWithEmail.replace("@recyclean.app", "")
    val isAdmin: Boolean
        get() = currentUserState == UserState.Admin

    val isValidUser: Boolean
        get() = true // Check With Firebase

    val hasLoggedIn: Boolean
        get() = isValidUser
}

enum class UserState {
    Normal, Admin, ThirdParty
}