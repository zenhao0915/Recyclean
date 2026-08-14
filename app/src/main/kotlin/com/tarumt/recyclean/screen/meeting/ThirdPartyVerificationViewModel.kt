package com.tarumt.recyclean.screen.meeting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.MeetingPageDestination
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.screen.addsell.SalvageablePart
import com.tarumt.recyclean.util.data.Appointment
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

class ThirdPartyVerificationViewModel : ViewModel() {

    var verifyingParts = mutableStateListOf<SalvageablePart>()
        private set

    var totalPayout by mutableStateOf(0.0)
        private set

    var isSubmitting by mutableStateOf(false)
        private set

    private var currentAppointment: Appointment? = null

    fun loadAppointment(appointment: Appointment) {
        currentAppointment = appointment
        verifyingParts.clear()

        // 默认勾选全部零件
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

    /**
     * 🌟 核心：完成验机并将 COMPLETED 状态与实收总额回写 Supabase
     */
    fun completeTransaction() {
        val appt = currentAppointment ?: return
        val checkedParts = verifyingParts.filter { it.isSelected }

        if (checkedParts.isEmpty()) {
            NotificationManager.addToast("Please select at least one valid part to proceed.", isSuccess = false)
            return
        }

        // 1. Debug 模式逻辑
        if (appState.isDebuggerMode) {
            appState.pendingAppointments.remove(appt)
            NotificationManager.addToast(
                "[Debug] RM ${String.format("%.2f", totalPayout)} paid to ${appt.userName}!",
                isSuccess = true
            )
            appState.navigator.navigateTo(MeetingPageDestination, Offset.Zero)
            return
        }

        // 2. Supabase 模式逻辑
        isSubmitting = true
        viewModelScope.launch {
            try {
                // 更新 appointments 表中的状态和最终核定结算价
                appState.supabase.from("appointments").update(
                    mapOf(
                        "status" to "COMPLETED",
                        "estimated_value" to totalPayout
                    )
                ) {
                    filter {
                        eq("appointment_id", appt.appointmentId)
                    }
                }

                // 待处理列表中移除此单
                appState.pendingAppointments.remove(appt)
                isSubmitting = false

                NotificationManager.addToast(
                    "Transaction successful! Transferred RM ${String.format("%.2f", totalPayout)} to ${appt.userName}.",
                    isSuccess = true
                )

                // 返回预约列表
                appState.navigator.navigateTo(MeetingPageDestination, Offset.Zero)
            } catch (e: Exception) {
                isSubmitting = false
                NotificationManager.addToast("Transaction failed: ${e.localizedMessage}", isSuccess = false)
            }
        }
    }
}