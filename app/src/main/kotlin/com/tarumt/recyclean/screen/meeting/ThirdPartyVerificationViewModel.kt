package com.tarumt.recyclean.screen.meeting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.ui.geometry.Offset
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.MeetingPageDestination
import com.tarumt.recyclean.screen.addsell.SalvageablePart
import com.tarumt.recyclean.util.data.Appointment

class ThirdPartyVerificationViewModel : ViewModel() {

    // The list of parts we are actively verifying on this screen
    var verifyingParts = mutableStateListOf<SalvageablePart>()
        private set

    // The dynamically updating total payout
    var totalPayout by mutableStateOf(0.0)
        private set

    private var currentAppointment: Appointment? = null

    // Called when the screen first opens to load the data
    fun loadAppointment(appointment: Appointment) {
        currentAppointment = appointment
        verifyingParts.clear()

        // We load the parts and assume everything is verified (checked) by default
        appointment.selectedParts.forEach { part ->
            verifyingParts.add(part.copy(isSelected = true))
        }
        calculateTotal()
    }

    // Handles the checkbox ticking/unticking
    fun togglePartVerification(part: SalvageablePart, isChecked: Boolean) {
        val index = verifyingParts.indexOf(part)
        if (index != -1) {
            // Replace the item in the state list to trigger UI recomposition
            verifyingParts[index] = part.copy(isSelected = isChecked)
        }
        calculateTotal()
    }

    private fun calculateTotal() {
        totalPayout = verifyingParts.filter { it.isSelected }.sumOf { it.estimatedPrice }
    }

    // Handles the final "Complete Transaction" button
    fun completeTransaction() {
        currentAppointment?.let { appt ->
            // In a real app, you would push this to a database and save to transaction history here.

            // Remove the appointment from the pending list since it is now completed
            appState.pendingAppointments.remove(appt)

            // Navigate back to the meeting list
            appState.navigator.navigateTo(MeetingPageDestination, Offset.Zero)
        }
    }
}