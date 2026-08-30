package com.tarumt.recyclean.screen.data

import android.annotation.SuppressLint
import android.util.Log
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
import com.tarumt.recyclean.util.data.AppointmentDto
import com.tarumt.recyclean.util.data.AppointmentStatus
import com.tarumt.recyclean.util.data.toAppointment
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    @SerialName("id")
    val id: String = "",
    @SerialName("username")
    val username: String? = null,
    @SerialName("role")
    val role: String? = "User",
    @SerialName("is_blacklisted")
    val isBlacklisted: Boolean? = false,
    @SerialName("blacklist_reason")
    val blacklistReason: String? = ""
)

@Serializable
private data class UpdateBlacklistDto(
    @SerialName("is_blacklisted")
    val isBlacklisted: Boolean,
    @SerialName("blacklist_reason")
    val blacklistReason: String
)

data class UserData(
    val fullId: String = "",
    val name: String,
    val id: String,
    val level: String = "Bronze",
    var status: String = "Active",
    val totalRevenue: String = "RM 0.00",
    var isBlacklisted: Boolean = false,
    var blacklistReason: String = ""
)

class AdminDataViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")

    var selectedUserId by mutableStateOf<String?>(null)

    val userList = mutableStateListOf<UserData>()

    val completedTransactions = mutableStateListOf<Appointment>()

    private val currentUsername: String
        get() = appState.currentUser?.userName?.trim() ?: "Admin"

    val filteredUsers: List<UserData>
        get() = if (searchQuery.isBlank()) {
            userList
        } else {
            userList.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.id.contains(searchQuery, ignoreCase = true) ||
                        it.fullId.contains(searchQuery, ignoreCase = true)
            }
        }

    val selectedUser: UserData?
        get() = userList.find { it.id == selectedUserId || it.fullId == selectedUserId }

    init {
        fetchUsers()
    }

    @SuppressLint("DefaultLocale")
    fun fetchUsers() {
        searchQuery = ""
        selectedUserId = null

        if (appState.isDebuggerMode) {
            loadMockData()
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val userDtos = appState.supabase.from("users")
                    .select()
                    .decodeList<UserProfileDto>()

                val appointmentDtos = try {
                    appState.supabase.from("appointments")
                        .select {
                            filter {
                                eq("status", "COMPLETED")
                            }
                        }
                        .decodeList<AppointmentDto>()
                } catch (_: Exception) {
                    emptyList()
                }

                val myTransactions = appointmentDtos
                    .filter { it.user_name.equals(currentUsername, ignoreCase = true) }
                    .map { it.toAppointment() }
                    .reversed()

                completedTransactions.clear()
                completedTransactions.addAll(myTransactions)

                val revenueMap = appointmentDtos.groupBy { it.user_name }.mapValues { entry ->
                    entry.value.sumOf { it.estimated_value }
                }

                userList.clear()
                userDtos.forEach { dto ->
                    val cleanUsername = dto.username ?: "User_${dto.id.take(4)}"
                    val revenue = revenueMap[cleanUsername] ?: revenueMap[dto.id] ?: 0.0

                    val level = when {
                        revenue >= 3000.0 -> "Platinum"
                        revenue >= 1500.0 -> "Gold"
                        revenue >= 500.0  -> "Silver"
                        else -> "Bronze"
                    }
                    val isBl = dto.isBlacklisted == true

                    userList.add(
                        UserData(
                            fullId = dto.id,
                            id = if (dto.id.length >= 8) dto.id.take(8) else dto.id,
                            name = cleanUsername,
                            level = level,
                            status = if (isBl) "Blacklisted" else "Active",
                            totalRevenue = String.format("RM %.2f", revenue),
                            isBlacklisted = isBl,
                            blacklistReason = dto.blacklistReason.orEmpty()
                        )
                    )
                }

            } catch (e: Exception) {
                Log.e("AdminDataViewModel", "Error fetching users", e)
                NotificationManager.addToast("Failed to load users: ${e.localizedMessage}", isSuccess = false)
            } finally {
                isLoading = false
            }
        }
    }

    fun saveBlacklist(user: UserData, isBlacklisted: Boolean, reason: String) {
        if (appState.isDebuggerMode) {
            val index = userList.indexOfFirst { it.fullId == user.fullId || it.id == user.id }
            if (index != -1) {
                userList[index] = userList[index].copy(
                    isBlacklisted = isBlacklisted,
                    blacklistReason = reason,
                    status = if (isBlacklisted) "Blacklisted" else "Active"
                )
            }
            selectedUserId = null
            NotificationManager.addToast("[Debug] User blacklist updated.", isSuccess = true)
            return
        }

        viewModelScope.launch {
            try {
                appState.supabase.from("users").update(
                    UpdateBlacklistDto(
                        isBlacklisted = isBlacklisted,
                        blacklistReason = reason
                    )
                ) {
                    filter {
                        eq("id", user.fullId)
                    }
                }

                val index = userList.indexOfFirst { it.fullId == user.fullId || it.id == user.id }
                if (index != -1) {
                    userList[index] = userList[index].copy(
                        isBlacklisted = isBlacklisted,
                        blacklistReason = reason,
                        status = if (isBlacklisted) "Blacklisted" else "Active"
                    )
                }
                selectedUserId = null
                NotificationManager.addToast("User moderation updated successfully.", isSuccess = true)
            } catch (e: Exception) {
                Log.e("AdminDataViewModel", "Failed to update blacklist", e)
                NotificationManager.addToast("Failed to update: ${e.localizedMessage}", isSuccess = false)
            }
        }
    }

    private fun loadMockData() {
        userList.clear()
        userList.addAll(
            listOf(
                UserData(fullId = "mock-1", name = "Ling Yue", id = "1224", level = "Gold", status = "Active", totalRevenue = "RM 3,450.00"),
                UserData(fullId = "mock-2", name = "Alice Smith", id = "1001", level = "Silver", status = "Active", totalRevenue = "RM 890.00"),
                UserData(fullId = "mock-3", name = "Bob Johnson", id = "1002", level = "Bronze", status = "Active", totalRevenue = "RM 420.00"),
                UserData(fullId = "mock-4", name = "Charlie Brown", id = "1003", level = "Gold", status = "Active", totalRevenue = "RM 2,100.00"),
                UserData(fullId = "mock-5", name = "Diana Prince", id = "1004", level = "Platinum", status = "Active", totalRevenue = "RM 5,600.00"),
                UserData(fullId = "mock-6", name = "Evan Wright", id = "1005", level = "Bronze", status = "Inactive", totalRevenue = "RM 150.00")
            )
        )

        completedTransactions.clear()
        completedTransactions.addAll(
            listOf(
                Appointment(
                    appointmentId = "APT-1001",
                    userName = currentUsername,
                    deviceName = "iPhone 13 Pro",
                    scheduledDate = "18 Aug 2026",
                    estimatedValue = 680.0,
                    status = AppointmentStatus.COMPLETED,
                    selectedParts = listOf(
                        SalvageablePart("OLED Screen", 350.0, true),
                        SalvageablePart("Battery", 120.0, true),
                        SalvageablePart("Camera Module", 210.0, true)
                    ),
                    targetSeller = "SenHeng"
                )
            )
        )
    }
}