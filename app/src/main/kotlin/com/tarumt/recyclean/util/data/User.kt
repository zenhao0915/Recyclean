package com.tarumt.recyclean.util.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserProfileDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("username")
    val username: String? = null,
    @SerialName("security_pin")
    val securityPin: String? = null,
    @SerialName("role")
    val role: String? = "Normal",
    @SerialName("is_blacklisted")
    val isBlacklisted: Boolean? = false,
    @SerialName("blacklist_reason")
    val blacklistReason: String? = ""
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