package com.tarumt.recyclean

import com.tarumt.recyclean.navigation.NavigatorState
import com.tarumt.recyclean.util.User
import kotlinx.coroutines.CoroutineScope

class AppState {
    var currentUser: User? = null
    val navigator = NavigatorState()
    var hasLoggedIn = true
}