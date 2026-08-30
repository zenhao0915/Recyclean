package com.tarumt.recyclean.screen.meeting

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.screen.addsell.SalvageablePart
import com.tarumt.recyclean.util.data.Appointment
import com.tarumt.recyclean.util.data.AppointmentCompleteUpdateDto
import com.tarumt.recyclean.util.data.SalvageablePartDto
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class ThirdPartyVerificationViewModel : ViewModel() {

    var verifyingParts = mutableStateListOf<SalvageablePart>()
        private set

    var totalPayout by mutableStateOf(0.0)
        private set

    var isSubmitting by mutableStateOf(false)
        private set

    var doneSubmission by mutableStateOf(false)
        private set

    private var currentAppointment: Appointment? = null


    fun loadAppointment(appointment: Appointment) {
        if (currentAppointment?.appointmentId == appointment.appointmentId && verifyingParts.isNotEmpty()) {
            return
        }

        currentAppointment = appointment
        verifyingParts.clear()
        doneSubmission = false

        appointment.selectedParts.forEach { part ->
            verifyingParts.add(part.copy(isSelected = true))
        }
        calculateTotal()
    }

    fun togglePartVerification(part: SalvageablePart, isChecked: Boolean) {
        val index = verifyingParts.indexOf(part)
        if (index != -1) {
            verifyingParts[index] = part.copy(isSelected = isChecked)
        }
        calculateTotal()
    }

    private fun calculateTotal() {
        totalPayout = verifyingParts.filter { it.isSelected }.sumOf { it.estimatedPrice }
    }

    @SuppressLint("DefaultLocale")
    fun completeTransaction() {
        val appt = currentAppointment ?: return
        val checkedParts = verifyingParts.filter { it.isSelected }

        if (checkedParts.isEmpty()) {
            NotificationManager.addToast("Please select at least one valid part to proceed.", isSuccess = false)
            return
        }

        if (appState.isDebuggerMode) {
            appState.pendingAppointments.remove(appt)
            NotificationManager.addToast(
                "[Debug] RM ${String.format("%.2f", totalPayout)} paid to ${appt.userName}!",
                isSuccess = true
            )
            doneSubmission = true
            return
        }

        isSubmitting = true
        viewModelScope.launch {
            try {
                val partDtos = checkedParts.map { part ->
                    SalvageablePartDto(
                        name = part.name,
                        estimatedPrice = part.estimatedPrice,
                        isSelected = part.isSelected
                    )
                }

                appState.supabase.from("appointments").update(
                    AppointmentCompleteUpdateDto(
                        status = "COMPLETED",
                        estimatedValue = totalPayout,
                        selectedParts = partDtos
                    )
                ) {
                    filter {
                        eq("appointment_id", appt.appointmentId)
                    }
                }

                appState.pendingAppointments.remove(appt)
                isSubmitting = false

                NotificationManager.addToast(
                    "Transaction successful! Transferred RM ${String.format("%.2f", totalPayout)} to ${appt.userName}.",
                    isSuccess = true
                )
                doneSubmission = true
            } catch (e: Exception) {
                isSubmitting = false
                NotificationManager.addToast("Transaction failed: ${e.localizedMessage}", isSuccess = false)
            }
        }
    }
}