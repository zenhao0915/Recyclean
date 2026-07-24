package com.tarumt.recyclean.screen.meeting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.VerificationPageDestination
import com.tarumt.recyclean.util.data.Appointment

class ThirdPartyMeetingViewModel : ViewModel() {

    // State to hold the specific appointment when the pop-up is open
    var selectedAppointment by mutableStateOf<Appointment?>(null)
        private set

    // Opens the pop-up by finding the appointment ID in the global state
    fun openApprovalDialog(appointmentId: String) {
        selectedAppointment = appState.pendingAppointments.find { it.appointmentId == appointmentId }
    }

    // Closes the pop-up
    fun closeApprovalDialog() {
        selectedAppointment = null
    }

    // Update this function in ThirdPartyMeetingViewModel.kt
    fun approveAppointment(appointment: Appointment) {
        // Temporarily store the appointment we want to verify in AppState so the next screen can read it
        appState.currentVerificationAppointment = appointment
        closeApprovalDialog()

        // Navigate to the new Verification Screen (You'll need to define this destination object in your navigation framework)
        appState.navigator.navigateTo(VerificationPageDestination, Offset.Zero)
    }

    // Backend logic when third party rejects
    fun rejectAppointment(appointment: Appointment) {
        // Removes the appointment from the list and closes the pop-up
        appState.pendingAppointments.remove(appointment)
        closeApprovalDialog()
    }
}