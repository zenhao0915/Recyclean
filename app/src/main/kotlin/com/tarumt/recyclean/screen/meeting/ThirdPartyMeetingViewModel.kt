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
import com.tarumt.recyclean.screen.addsell.SalvageablePart
import com.tarumt.recyclean.util.data.Appointment
import com.tarumt.recyclean.util.data.AppointmentDto
import com.tarumt.recyclean.util.data.AppointmentStatus
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

    var isLoading by mutableStateOf(false)
        private set

    // 获取当前登录的商家名称（如 "SenHeng", "CompAsia"）
    val currentMerchantName: String
        get() = appState.currentUser?.userName?.trim() ?: "SenHeng"

    init {
        fetchInitialAppointments()
        listenToRealtimeAppointments()
    }

    /**
     * 🌟 1. 初次拉取：只获取分配给当前商家的 PENDING 订单
     */
    fun fetchInitialAppointments() {
        if (appState.isDebuggerMode) {
            // Debug 模式下也执行商户数据隔离
            appState.pendingAppointments.clear()
            appState.pendingAppointments.addAll(
                getMockAppointments().filter {
                    it.targetSeller.equals(
                        currentMerchantName,
                        ignoreCase = true
                    ) || it.targetSeller == "SenHeng"
                }
            )
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                // 🌟 核心过滤：target_seller = 当前登录商家 且 状态为 PENDING
                val dtos = appState.supabase.from("appointments")
                    .select {
                        filter {
                            eq("target_seller", currentMerchantName)
                            eq("status", "PENDING")
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

    private fun listenToRealtimeAppointments() {
        if (appState.isDebuggerMode) return

        viewModelScope.launch {
            try {
                // 🌟 清理空格与特殊字符：例如 "PC Image" -> "pc_image"
                val sanitizedMerchant = currentMerchantName.trim().lowercase().replace(" ", "_")
                val channel = appState.supabase.channel("merchant_live_$sanitizedMerchant")

                val changeFlow =
                    channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                        table = "appointments"
                    }

                channel.subscribe()

                changeFlow.onEach { change ->
                    val newDto = change.decodeRecord<AppointmentDto>()
                    val newAppointment = newDto.toAppointment()

                    // 商家隔离比对（忽略大小写和空格）
                    if (newAppointment.targetSeller.trim()
                            .equals(currentMerchantName.trim(), ignoreCase = true) &&
                        newAppointment.status == AppointmentStatus.PENDING
                    ) {
                        if (appState.pendingAppointments.none { it.appointmentId == newAppointment.appointmentId }) {
                            appState.pendingAppointments.add(0, newAppointment)

                            NotificationManager.addToast(
                                "New Order for $currentMerchantName! Device: ${newAppointment.deviceName} (RM ${newAppointment.estimatedValue})",
                                isSuccess = true,
                                isPriority = true
                            )
                        }
                    }
                }.launchIn(viewModelScope)

            } catch (e: Exception) {
                // 捕获异常避免崩溃影响主流程
                e.printStackTrace()
            }
        }
    }

    fun openApprovalDialog(appointmentId: String) {
        selectedAppointment =
            appState.pendingAppointments.find { it.appointmentId == appointmentId }
    }

    fun closeApprovalDialog() {
        selectedAppointment = null
    }

    fun approveAppointment(appointment: Appointment) {
        appState.currentVerificationAppointment = appointment
        closeApprovalDialog()
        appState.navigator.navigateTo(VerificationPageDestination, Offset.Zero)
    }

    /**
     * 🌟 3. 拒绝预约：将 Supabase 数据库对应订单状态标记为 CANCELLED
     */
    fun rejectAppointment(appointment: Appointment) {
        appState.pendingAppointments.remove(appointment)
        closeApprovalDialog()

        if (appState.isDebuggerMode) {
            NotificationManager.addToast("[Debug] Appointment rejected.", isSuccess = true)
            return
        }

        viewModelScope.launch {
            try {
                appState.supabase.from("appointments").update(
                    mapOf("status" to "CANCELLED")
                ) {
                    filter {
                        eq("appointment_id", appointment.appointmentId)
                    }
                }
                NotificationManager.addToast("Appointment rejected.", isSuccess = true)
            } catch (e: Exception) {
                NotificationManager.addToast(
                    "Failed to reject: ${e.localizedMessage}",
                    isSuccess = false
                )
            }
        }
    }

    private fun getMockAppointments(): List<Appointment> {
        return listOf(
            Appointment(
                appointmentId = "APT-1001",
                userName = "user_kevin",
                deviceName = "iPhone 13 Pro",
                scheduledDate = "15 Aug 2026",
                estimatedValue = 450.00,
                status = AppointmentStatus.PENDING,
                selectedParts = listOf(
                    SalvageablePart("Super Retina XDR Display", 320.0, true),
                    SalvageablePart("LiDAR Scanner Unit", 130.0, true)
                ),
                targetSeller = "SenHeng"
            ),
            Appointment(
                appointmentId = "APT-1002",
                userName = "user_alice",
                deviceName = "MacBook Air M2",
                scheduledDate = "16 Aug 2026",
                estimatedValue = 680.00,
                status = AppointmentStatus.PENDING,
                selectedParts = listOf(
                    SalvageablePart("Liquid Retina Screen", 480.0, true),
                    SalvageablePart("Touch ID Keyboard", 200.0, true)
                ),
                targetSeller = "CompAsia"
            )
        )
    }
}