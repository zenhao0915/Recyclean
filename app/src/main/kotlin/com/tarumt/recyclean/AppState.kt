package com.tarumt.recyclean

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.tarumt.recyclean.navigation.NavigatorState
import com.tarumt.recyclean.screen.SalvageablePart
import com.tarumt.recyclean.util.data.Appointment
import com.tarumt.recyclean.util.data.Sellers
import com.tarumt.recyclean.util.data.User

class AppState {
    var currentUser: User? = null
    val navigator = NavigatorState()
    var lastTouchOffset by mutableStateOf(Offset.Zero)

    var hasLoggedIn = true

    var deviceToSell: String? = null
    var cachedBitmap by mutableStateOf<Bitmap?>(null)
    var showResult by mutableStateOf(false)
    var detectedDeviceName by mutableStateOf("")
    val cachedPartList = mutableStateListOf<SalvageablePart>()
    var selectedSeller by mutableStateOf(Sellers.SenHeng)

    val pendingAppointments = mutableStateListOf<Appointment>()
}