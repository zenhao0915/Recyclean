package com.tarumt.recyclean.screen.meeting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.VerificationPageDestination
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.util.data.Appointment
import com.tarumt.recyclean.util.data.AppointmentDto
import com.tarumt.recyclean.util.data.toAppointment
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ThirdPartyMeetingViewModel : ViewModel() {

    var selectedAppointment by mutableStateOf<Appointment?>(null)
        private set

    init {
        fetchInitialAppointments()
        listenToRealtimeAppointments()
    }

    private fun fetchInitialAppointments() {
        if (appState.isDebuggerMode) return

        viewModelScope.launch {
            try {
                val dtos = appState.supabase.from("appointments")
                    .select()
                    .decodeList<AppointmentDto>()

                appState.pendingAppointments.clear()
                appState.pendingAppointments.addAll(dtos.map { it.toAppointment() }.reversed())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun listenToRealtimeAppointments() {
        if (appState.isDebuggerMode) return

        viewModelScope.launch {
            try {
                val channel = appState.supabase.channel("merchant-appointments-live")

                val changeFlow = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                    table = "appointments"
                }

                channel.subscribe()

                changeFlow.onEach { change ->
                    val newDto = change.decodeRecord<AppointmentDto>()
                    val newAppointment = newDto.toAppointment()

                    if (appState.pendingAppointments.none { it.appointmentId == newAppointment.appointmentId }) {
                        appState.pendingAppointments.add(0, newAppointment)

                        NotificationManager.addToast(
                            "New Order！Device: ${newAppointment.deviceName} (RM ${newAppointment.estimatedValue})",
                            isSuccess = true,
                            isPriority = true
                        )
                    }
                }.launchIn(viewModelScope)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun openApprovalDialog(appointmentId: String) {
        selectedAppointment = appState.pendingAppointments.find { it.appointmentId == appointmentId }
    }

    fun closeApprovalDialog() {
        selectedAppointment = null
    }

    fun approveAppointment(appointment: Appointment) {
        appState.currentVerificationAppointment = appointment
        closeApprovalDialog()
        appState.navigator.navigateTo(VerificationPageDestination, Offset.Zero)
    }

    fun rejectAppointment(appointment: Appointment) {
        appState.pendingAppointments.remove(appointment)
        closeApprovalDialog()
    }
}