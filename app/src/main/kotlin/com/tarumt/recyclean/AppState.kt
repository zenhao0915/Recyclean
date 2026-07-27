package com.tarumt.recyclean

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tarumt.recyclean.navigation.NavigatorState
import com.tarumt.recyclean.screen.addsell.SalvageablePart
import com.tarumt.recyclean.util.data.Appointment
import com.tarumt.recyclean.util.data.Sellers
import com.tarumt.recyclean.util.data.User
import com.tarumt.recyclean.util.data.UserState
import kotlinx.coroutines.CoroutineScope

class AppState {
    lateinit var scope: CoroutineScope

    val isDebuggerMode by mutableStateOf(true) // Skip Firebase When Value Is "true"

    var currentUser by mutableStateOf<User?>(null)
    var currentUserState by mutableStateOf(UserState.Normal)
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val navigator = NavigatorState()
    var lastTouchOffset by mutableStateOf(Offset.Zero)

    var deviceToSell: String? = null
    var cachedBitmap by mutableStateOf<Bitmap?>(null)
    var showResult by mutableStateOf(false)
    var detectedDeviceName by mutableStateOf("")
    val cachedPartList = mutableStateListOf<SalvageablePart>()
    var selectedSeller by mutableStateOf(Sellers.SenHeng)

    // for verification screen I add here
    var currentVerificationAppointment by mutableStateOf<Appointment?>(null)

    val pendingAppointments = mutableStateListOf<Appointment>()
}