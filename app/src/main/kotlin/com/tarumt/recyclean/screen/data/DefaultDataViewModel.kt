package com.tarumt.recyclean.screen.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.screen.addsell.SalvageablePart
import com.tarumt.recyclean.util.data.Appointment
import com.tarumt.recyclean.util.data.AppointmentDto
import com.tarumt.recyclean.util.data.AppointmentStatus
import com.tarumt.recyclean.util.data.toAppointment
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class DefaultDataViewModel : ViewModel() {
    val completedTransactions = mutableStateListOf<Appointment>()

    var isLoading by mutableStateOf(false)
        private set

    var totalEarnings by mutableDoubleStateOf(0.0)
        private set

    var totalDevicesCount by mutableIntStateOf(0)
        private set

    var totalPartsSavedCount by mutableIntStateOf(0)
        private set

    fun fetchTransactionHistory() {
        if (appState.isDebuggerMode) {
            completedTransactions.clear()
            completedTransactions.addAll(getMockHistory())
            calculateStats()
            return
        }

        val userEmail = appState.currentUser?.userNameWithEmail?.trim() ?: return

        viewModelScope.launch {
            isLoading = true
            try {
                val dtos = appState.supabase.from("appointments")
                    .select {
                        filter {
                            eq("user_name", userEmail)
                            eq("status", "COMPLETED")
                        }
                    }
                    .decodeList<AppointmentDto>()

                completedTransactions.clear()
                completedTransactions.addAll(dtos.map { it.toAppointment() }.reversed())
                calculateStats()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    private fun calculateStats() {
        totalEarnings = completedTransactions.sumOf { it.estimatedValue }
        totalDevicesCount = completedTransactions.size
        totalPartsSavedCount = completedTransactions.sumOf { it.selectedParts.size }
    }

    private fun getMockHistory(): List<Appointment> {
        return listOf(
            Appointment(
                appointmentId = "APT-8821",
                userName = "DebugUser",
                deviceName = "iPhone 12 Pro Max",
                scheduledDate = "12 Aug 2026",
                estimatedValue = 320.00,
                status = AppointmentStatus.COMPLETED,
                selectedParts = listOf(
                    SalvageablePart("OLED Display Assembly", 220.0, true),
                    SalvageablePart("Taptic Engine", 100.0, true)
                ),
                targetSeller = "CompAsia"
            ),
            Appointment(
                appointmentId = "APT-7102",
                userName = "DebugUser",
                deviceName = "Dell XPS 13",
                scheduledDate = "02 Aug 2026",
                estimatedValue = 540.00,
                status = AppointmentStatus.COMPLETED,
                selectedParts = listOf(
                    SalvageablePart("16GB DDR4 RAM Module", 180.0, true),
                    SalvageablePart("512GB NVMe SSD", 210.0, true),
                    SalvageablePart("Battery Pack", 150.0, true)
                ),
                targetSeller = "SenHeng"
            )
        )
    }
}