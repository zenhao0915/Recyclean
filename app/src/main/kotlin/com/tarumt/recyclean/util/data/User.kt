package com.tarumt.recyclean.util.data

class User(val userName: String, password: String, val isAdmin: Boolean = false) {
    var hasLoggedIn = false
}