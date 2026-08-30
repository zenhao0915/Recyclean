package com.tarumt.recyclean.screen.menu

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.tarumt.recyclean.common.SessionManager
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.LoginPageDestination
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.util.data.UserState

class ProfileViewModel : ViewModel() {
    fun processUserLogout(context: Context? = null) = runCatching {
        context?.let { SessionManager.clearSession(it) }

        appState.resetAllSession()

        NotificationManager.addToast(
            "Logged out successfully!",
            isSuccess = true,
            isPriority = true
        )
        appState.navigator.navigateTo(LoginPageDestination, appState.lastTouchOffset)
    }.getOrElse {
        Log.d("ERROR", "User Failed To Logout!")
        NotificationManager.addToast("User Failed To Logout!", isSuccess = false, isPriority = true)
    }
}