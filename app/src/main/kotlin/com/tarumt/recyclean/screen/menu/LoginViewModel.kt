package com.tarumt.recyclean.screen.menu

import androidx.lifecycle.ViewModel
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.HomePageDestination
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.util.data.User
import com.tarumt.recyclean.util.data.UserState

class LoginViewModel : ViewModel() {

    fun processUserLogin(userName: String, password: String, userState: UserState) {
        appState.navigator.navigateTo(HomePageDestination, appState.lastTouchOffset)
        val userLogin =
            User(userName = userName, password = password.hashCode(), currentUserState = userState)

        appState.currentUser?.let {
            // Display User Has Logged In
        } ?: run {
            if (!userLogin.isValidUser) return@run
            // Display Invalid Toast Notification

            appState.currentUser = userLogin
            appState.navigator.navigateTo(HomePageDestination, appState.lastTouchOffset)
        }
    }

    fun processRegisterUser(userName: String) {
        NotificationManager.addToast("User $userName Registered Successfully!")
    }

    fun processForgetPassword() {

    }
}