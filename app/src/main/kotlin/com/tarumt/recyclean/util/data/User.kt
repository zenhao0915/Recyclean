package com.tarumt.recyclean.util.data

class User(val userName: String, password: String, var currentUserState: UserState = UserState.Normal) {
    val isAdmin: Boolean
        get() = currentUserState == UserState.Admin

    val isValidUser: Boolean
        get() = true

    val hasLoggedIn: Boolean
        get() = isValidUser
}

enum class UserState {
    Normal, Admin, ThirdParty
}