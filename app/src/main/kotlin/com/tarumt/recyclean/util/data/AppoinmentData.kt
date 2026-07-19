package com.tarumt.recyclean.util.data

import com.tarumt.recyclean.screen.addsell.SalvageablePart

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

