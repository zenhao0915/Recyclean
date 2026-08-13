package com.tarumt.recyclean.util.data

import com.tarumt.recyclean.screen.addsell.SalvageablePart
import kotlinx.serialization.Serializable

@Serializable
data class SalvageablePartDto(
    val name: String = "",
    val estimatedPrice: Double = 0.0,
    val isSelected: Boolean = true
)

@Serializable
data class AppointmentDto(
    val appointment_id: String? = null,
    val user_name: String = "",
    val device_name: String = "",
    val scheduled_date: String = "Pending Date",
    val estimated_value: Double = 0.0,
    val status: String = "PENDING",
    val selected_parts: List<SalvageablePartDto> = emptyList(),
    val target_seller: String = "",
    val created_at: String? = null
)

// 🌟 3. UI 界面使用的模型 (保持不变，避免影响其他 UI 页面)
data class Appointment(
    val appointmentId: String,
    val userName: String,
    val deviceName: String,
    val scheduledDate: String,
    val estimatedValue: Double,
    val status: AppointmentStatus,
    val selectedParts: List<SalvageablePart>,
    val targetSeller: String
)

enum class AppointmentStatus {
    PENDING, REVIEWED, COMPLETED
}

fun AppointmentDto.toAppointment(): Appointment {
    return Appointment(
        appointmentId = this.appointment_id ?: "APT-${System.currentTimeMillis().toString().takeLast(4)}",
        userName = this.user_name,
        deviceName = this.device_name,
        scheduledDate = this.scheduled_date,
        estimatedValue = this.estimated_value,
        status = runCatching { AppointmentStatus.valueOf(this.status) }.getOrDefault(AppointmentStatus.PENDING),
        selectedParts = this.selected_parts.map {
            SalvageablePart(it.name, it.estimatedPrice, it.isSelected)
        },
        targetSeller = this.target_seller
    )
}

fun Appointment.toDto(): AppointmentDto {
    return AppointmentDto(
        appointment_id = this.appointmentId,
        user_name = this.userName,
        device_name = this.deviceName,
        scheduled_date = this.scheduledDate,
        estimated_value = this.estimatedValue,
        status = this.status.name,
        selected_parts = this.selectedParts.map {
            SalvageablePartDto(it.name, it.estimatedPrice, it.isSelected)
        },
        target_seller = this.targetSeller
    )
}