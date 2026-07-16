package com.tarumt.recyclean

import androidx.compose.runtime.mutableStateListOf
import com.tarumt.recyclean.navigation.NavigatorState
import com.tarumt.recyclean.screen.Appointment
import com.tarumt.recyclean.util.data.User

class AppState {
    var currentUser: User? = null
    val navigator = NavigatorState()
    var hasLoggedIn = true

    var deviceToSell: String? = null

    val pendingAppointments = mutableStateListOf<Appointment>()
}