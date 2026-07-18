package com.tarumt.recyclean.util.data

class User(val userName: String, password: String, var currentUserState: UserState = UserState.Normal) {
    var hasLoggedIn = false
    val isAdmin: Boolean
        get() = currentUserState == UserState.Admin

    val isValidUser: Boolean
        get() = true
}

enum class UserState {
    Normal, Admin, ThirdParty
}