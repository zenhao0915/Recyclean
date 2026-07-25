package com.tarumt.recyclean.util.data

class User(val userName: String, password: Int, var currentUserState: UserState = UserState.Normal) {
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