package com.tarumt.recyclean.screen

// Make sure SalvageablePart is accessible here!
data class Appointment(
    val appointmentId: String,
    val userName: String,
    val deviceName: String,
    val scheduledDate: String, // You can leave this as "Pending" until they pick a date
    val estimatedValue: Double,
    val status: AppointmentStatus,
    val selectedParts: List<SalvageablePart>, // New: To hold the checked items
    val targetSeller: String                  // New: To know which company they chose
)

enum class AppointmentStatus {
    PENDING, REVIEWED, COMPLETED
}

