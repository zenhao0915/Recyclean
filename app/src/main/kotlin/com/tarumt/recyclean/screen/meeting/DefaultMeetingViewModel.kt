package com.tarumt.recyclean.screen.meeting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.screen.addsell.SalvageablePart
import com.tarumt.recyclean.util.data.Appointment
import com.tarumt.recyclean.util.data.AppointmentDto
import com.tarumt.recyclean.util.data.AppointmentStatus
import com.tarumt.recyclean.util.data.toAppointment
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class DefaultMeetingViewModel : ViewModel() {

    var selectedAppointmentForCancel by mutableStateOf<Appointment?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun fetchUserAppointments() {
        if (appState.isDebuggerMode) {
            if (appState.pendingAppointments.isEmpty()) {
                appState.pendingAppointments.addAll(getMockAppointments())
            }
            return
        }

        val currentUserEmail = appState.currentUser?.userNameWithEmail ?: return

        viewModelScope.launch {
            isLoading = true
            try {
                val dtos = appState.supabase.from("appointments")
                    .select {
                        filter {
                            eq("user_name", currentUserEmail)
                        }
                    }
                    .decodeList<AppointmentDto>()

                appState.pendingAppointments.clear()
                appState.pendingAppointments.addAll(dtos.map { it.toAppointment() }.reversed())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun openCancelDialog(appointment: Appointment) {
        selectedAppointmentForCancel = appointment
    }

    fun closeCancelDialog() {
        selectedAppointmentForCancel = null
    }

    fun confirmCancelAppointment(appointment: Appointment) {
        viewModelScope.launch {
            try {
                if (!appState.isDebuggerMode) {
                    appState.supabase.from("appointments")
                        .delete {
                            filter {
                                eq("appointment_id", appointment.appointmentId)
                            }
                        }
                }

                appState.pendingAppointments.remove(appointment)
                NotificationManager.addToast("Appointment cancelled successfully.", isSuccess = true)
            } catch (e: Exception) {
                NotificationManager.addToast("Failed to cancel: ${e.localizedMessage}", isSuccess = false)
            } finally {
                closeCancelDialog()
            }
        }
    }

    private fun getMockAppointments(): List<Appointment> {
        return listOf(
            Appointment(
                appointmentId = "APT-9821",
                userName = "DebugUser",
                deviceName = "PlayStation 5",
                scheduledDate = "Pending Date",
                estimatedValue = 450.00,
                status = AppointmentStatus.PENDING,
                selectedParts = listOf(
                    SalvageablePart("Motherboard", 300.0, true),
                    SalvageablePart("Power Supply Unit", 150.0, true)
                ),
                targetSeller = "SenHeng"
            ),
            Appointment(
                appointmentId = "APT-3304",
                userName = "DebugUser",
                deviceName = "iPhone 13 Pro",
                scheduledDate = "Pending Date",
                estimatedValue = 280.00,
                status = AppointmentStatus.PENDING,
                selectedParts = listOf(
                    SalvageablePart("OLED Screen Panel", 180.0, true),
                    SalvageablePart("Battery Cell", 100.0, true)
                ),
                targetSeller = "CompAsia"
            )
        )
    }
}