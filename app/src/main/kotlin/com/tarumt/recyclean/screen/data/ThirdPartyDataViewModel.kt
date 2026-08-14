package com.tarumt.recyclean.screen.data

import androidx.compose.runtime.getValue
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

data class MerchantChartPoint(
    val dateLabel: String,
    val totalSpend: Double,
    val orderCount: Int
)

class ThirdPartyDataViewModel : ViewModel() {

    val purchasedTransactions = mutableStateListOf<Appointment>()

    var isLoading by mutableStateOf(false)
        private set

    var totalProcurementCost by mutableStateOf(0.0)
        private set

    var totalDevicesPurchased by mutableStateOf(0)
        private set

    var totalPartsAcquired by mutableStateOf(0)
        private set

    // 🌟 图表绘图数据源
    var chartPoints by mutableStateOf<List<MerchantChartPoint>>(emptyList())
        private set

    fun fetchMerchantData() {
        val currentMerchantName = appState.currentUser?.userName?.trim() ?: "SenHeng"

        if (appState.isDebuggerMode) {
            purchasedTransactions.clear()
            val filteredMock = getMockHistory().filter {
                it.targetSeller.equals(currentMerchantName, ignoreCase = true) || it.targetSeller == "SenHeng"
            }
            purchasedTransactions.addAll(filteredMock)
            calculateMetrics()
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val dtos = appState.supabase.from("appointments")
                    .select {
                        filter {
                            eq("target_seller", currentMerchantName)
                            eq("status", "COMPLETED")
                        }
                    }
                    .decodeList<AppointmentDto>()

                purchasedTransactions.clear()
                purchasedTransactions.addAll(dtos.map { it.toAppointment() }.reversed())
                calculateMetrics()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    private fun calculateMetrics() {
        totalProcurementCost = purchasedTransactions.sumOf { it.estimatedValue }
        totalDevicesPurchased = purchasedTransactions.size
        totalPartsAcquired = purchasedTransactions.sumOf { it.selectedParts.size }

        // 🌟 按日期聚合生成图表点
        chartPoints = if (purchasedTransactions.isEmpty()) {
            emptyList()
        } else {
            purchasedTransactions
                .groupBy { it.scheduledDate }
                .map { (date, list) ->
                    MerchantChartPoint(
                        dateLabel = date.take(6), // 简化日期标签 (如 "02 Aug")
                        totalSpend = list.sumOf { it.estimatedValue },
                        orderCount = list.size
                    )
                }
        }
    }

    private fun getMockHistory(): List<Appointment> {
        return listOf(
            Appointment(
                appointmentId = "APT-5519",
                userName = "user_brian",
                deviceName = "PlayStation 5",
                scheduledDate = "28 Jul 2026",
                estimatedValue = 420.00,
                status = AppointmentStatus.COMPLETED,
                selectedParts = listOf(
                    SalvageablePart("Power Supply Unit", 180.0, true),
                    SalvageablePart("BD-ROM Optical Drive", 240.0, true)
                ),
                targetSeller = "SenHeng"
            ),
            Appointment(
                appointmentId = "APT-7102",
                userName = "user_alice",
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
            ),
            Appointment(
                appointmentId = "APT-8821",
                userName = "user_kevin",
                deviceName = "iPhone 12 Pro Max",
                scheduledDate = "12 Aug 2026",
                estimatedValue = 320.00,
                status = AppointmentStatus.COMPLETED,
                selectedParts = listOf(
                    SalvageablePart("OLED Display Assembly", 220.0, true),
                    SalvageablePart("Taptic Engine", 100.0, true)
                ),
                targetSeller = "SenHeng"
            ),
            Appointment(
                appointmentId = "APT-9930",
                userName = "user_diana",
                deviceName = "MacBook Pro M1",
                scheduledDate = "14 Aug 2026",
                estimatedValue = 780.00,
                status = AppointmentStatus.COMPLETED,
                selectedParts = listOf(
                    SalvageablePart("Retina Display", 500.0, true),
                    SalvageablePart("Trackpad Unit", 280.0, true)
                ),
                targetSeller = "SenHeng"
            )
        )
    }
}