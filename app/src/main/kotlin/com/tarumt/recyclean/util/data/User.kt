package com.tarumt.recyclean.util.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: String? = null,
    val email: String? = null,
    val password: String? = null,
    @SerialName("security_pin") val securityPin: String? = null,
    val username: String? = null,
    val role: String? = null,
    @SerialName("is_blacklisted") val isBlacklisted: Boolean? = false,
    @SerialName("blacklist_reason") val blacklistReason: String? = null
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