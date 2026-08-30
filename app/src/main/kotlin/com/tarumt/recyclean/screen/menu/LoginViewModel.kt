package com.tarumt.recyclean.screen.menu

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.HomePageDestination
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.common.SessionManager
import com.tarumt.recyclean.util.data.User
import com.tarumt.recyclean.util.data.UserProfileDto
import com.tarumt.recyclean.util.data.UserState
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

class LoginViewModel : ViewModel() {
    var usernameInput by mutableStateOf("")
    var passwordInput by mutableStateOf("")

    var isLoading by mutableStateOf(false)
        private set

    var loadingMessage by mutableStateOf("Connecting To Database....")
        private set

    private fun formatUsername(rawName: String): String {
        val clean = rawName.trim().lowercase().replace(" ", "")
        return if (clean.endsWith("@recyclean.app")) {
            clean
        } else {
            "$clean@recyclean.app"
        }
    }

    suspend fun checkAutoLogin(context: Context, onComplete: () -> Unit = {}): Boolean {
        if (appState.isDebuggerMode) {
            withContext(Dispatchers.Main) { onComplete() }
            return false
        }

        return withContext(Dispatchers.IO) {
            try {
                val savedCredentials =
                    SessionManager.getSavedCredentials(context) ?: return@withContext false

                val (savedUsername, savedPassword) = savedCredentials

                val isSuccess = withTimeoutOrNull(8000L.milliseconds) {
                    val matchedUsers = appState.supabase.from("users")
                        .select {
                            filter {
                                eq("email", savedUsername)
                                eq("password", savedPassword)
                            }
                        }.decodeList<UserProfileDto>()

                    if (matchedUsers.isEmpty()) {
                        SessionManager.clearSession(context)
                        return@withTimeoutOrNull false
                    }

                    val profile = matchedUsers.first()

                    if (profile.isBlacklisted == true) {
                        SessionManager.clearSession(context)
                        appState.currentUser = null
                        val reason = profile.blacklistReason?.ifBlank { "Violation of platform policies" }
                            ?: "Violation of platform policies"
                        NotificationManager.addToast(
                            "Account suspended: $reason",
                            isSuccess = false,
                            isPriority = true
                        )
                        return@withTimeoutOrNull false
                    }

                    val mappedState = when (profile.role?.lowercase()) {
                        "admin" -> UserState.Admin
                        "thirdparty" -> UserState.ThirdParty
                        else -> UserState.Normal
                    }

                    appState.currentUserState = mappedState
                    appState.currentUser = User(
                        userNameWithEmail = savedUsername,
                        currentUserState = mappedState
                    )

                    NotificationManager.addToast("Session Restored!", isSuccess = true)

                    withContext(Dispatchers.Main) {
                        appState.navigator.navigateTo(HomePageDestination, appState.lastTouchOffset)
                    }
                    true
                }

                isSuccess ?: false
            } catch (e: Exception) {
                Log.e("AutoLogin", "Error during auto login check", e)
                appState.currentUser = null
                false
            } finally {
                withContext(Dispatchers.Main) {
                    onComplete()
                }
            }
        }
    }

    fun processUserLogin(
        userName: String,
        password: String,
        userState: UserState,
        context: Context? = null
    ) {
        val trimmedPassword = password.trim()

        if (userName.isBlank() || trimmedPassword.isBlank()) {
            NotificationManager.addToast("Please fill in both username and password.", isSuccess = false)
            return
        }

        val fullUsername = formatUsername(userName)

        if (appState.isDebuggerMode) {
            val dummyUser = User(
                userNameWithEmail = fullUsername,
                currentUserState = userState
            )
            appState.currentUser = dummyUser
            appState.currentUserState = userState
            NotificationManager.addToast("[Debug Mode] Bypassed authentication!", isSuccess = true)
            appState.navigator.navigateTo(HomePageDestination, appState.lastTouchOffset)
            return
        }

        isLoading = true
        loadingMessage = "Verifying Credentials...."

        viewModelScope.launch {
            try {
                val matchedUsers = appState.supabase.from("users")
                    .select {
                        filter {
                            eq("email", fullUsername)
                            eq("password", trimmedPassword)
                        }
                    }.decodeList<UserProfileDto>()

                if (matchedUsers.isEmpty()) {
                    isLoading = false
                    NotificationManager.addToast("Invalid Username or Password!", isSuccess = false)
                    return@launch
                }

                val profile = matchedUsers.first()

                if (profile.isBlacklisted == true) {
                    isLoading = false
                    context?.let { SessionManager.clearSession(it) }
                    val reason = profile.blacklistReason?.ifBlank { "Violation of platform policies" }
                        ?: "Violation of platform policies"
                    NotificationManager.addToast(
                        "Login Denied: Your account has been blacklisted. Reason: $reason",
                        isSuccess = false,
                        isPriority = true
                    )
                    return@launch
                }

                val roleString = profile.role ?: "Normal"
                val mappedState = when (roleString.lowercase()) {
                    "admin" -> UserState.Admin
                    "thirdparty" -> UserState.ThirdParty
                    else -> UserState.Normal
                }

                if (mappedState != userState) {
                    isLoading = false
                    NotificationManager.addToast(
                        "Access Denied: This account is registered as '${mappedState.name}', not '${userState.name}'!",
                        isSuccess = false
                    )
                    return@launch
                }

                context?.let { SessionManager.saveSession(it, fullUsername, trimmedPassword) }

                isLoading = false
                appState.currentUserState = mappedState
                appState.currentUser = User(
                    userNameWithEmail = fullUsername,
                    currentUserState = mappedState
                )

                NotificationManager.addToast("Welcome back, $userName!", isSuccess = true)
                appState.navigator.navigateTo(HomePageDestination, appState.lastTouchOffset)
                usernameInput = ""
                passwordInput = ""

            } catch (e: Exception) {
                isLoading = false
                Log.e("LoginError", "Login failed", e)
                NotificationManager.addToast("Login failed: ${e.localizedMessage}", isSuccess = false)
            }
        }
    }

    fun processRegisterUser(
        userNameInput: String,
        passwordInput: String,
        securityPinInput: String,
        userState: UserState = appState.currentUserState,
        context: Context? = null
    ) {
        val trimmedPassword = passwordInput.trim()
        val trimmedPin = securityPinInput.trim()

        if (userState != UserState.Normal) {
            NotificationManager.addToast("Registration is restricted to Normal users.", isSuccess = false)
            return
        }

        if (userNameInput.isBlank() || trimmedPassword.isBlank() || trimmedPin.isBlank()) {
            NotificationManager.addToast("Please fill in all fields.", isSuccess = false)
            return
        }

        if (trimmedPassword.length < 6) {
            NotificationManager.addToast("Password must be at least 6 characters.", isSuccess = false)
            return
        }

        val fullUsername = formatUsername(userNameInput)

        isLoading = true
        loadingMessage = "Checking Username Availability...."

        viewModelScope.launch {
            try {
                val existingUsers = appState.supabase.from("users")
                    .select {
                        filter { eq("email", fullUsername) }
                    }.decodeList<UserProfileDto>()

                if (existingUsers.isNotEmpty()) {
                    isLoading = false
                    NotificationManager.addToast("Username '$fullUsername' is already taken!", isSuccess = false)
                    return@launch
                }

                loadingMessage = "Creating Account...."

                val newUser = UserProfileDto(
                    id = UUID.randomUUID().toString(),
                    email = fullUsername,
                    password = trimmedPassword,
                    securityPin = trimmedPin,
                    username = userNameInput,
                    role = UserState.Normal.name,
                    isBlacklisted = false
                )

                appState.supabase.from("users").insert(newUser)

                isLoading = false
                NotificationManager.addToast("Registered successfully!", isSuccess = true)
                processUserLogin(fullUsername, trimmedPassword, UserState.Normal, context)

            } catch (e: Exception) {
                isLoading = false
                Log.e("RegisterError", "Registration failed", e)
                NotificationManager.addToast("Registration failed: ${e.localizedMessage}", isSuccess = false)
            }
        }
    }

    fun processForgetPassword(
        usernameInput: String,
        securityPinInput: String,
        newPasswordInput: String
    ) {
        val trimmedPin = securityPinInput.trim()
        val trimmedNewPassword = newPasswordInput.trim()

        if (usernameInput.isBlank() || trimmedPin.isBlank() || trimmedNewPassword.isBlank()) {
            NotificationManager.addToast("Please enter Username, Security PIN, and New Password.", isSuccess = false)
            return
        }

        if (trimmedNewPassword.length < 6) {
            NotificationManager.addToast("New password must be at least 6 characters.", isSuccess = false)
            return
        }

        val fullUsername = formatUsername(usernameInput)

        isLoading = true
        loadingMessage = "Verifying Security PIN...."

        viewModelScope.launch {
            try {
                val matchedUsers = appState.supabase.from("users")
                    .select {
                        filter {
                            eq("email", fullUsername)
                            eq("security_pin", trimmedPin)
                        }
                    }.decodeList<UserProfileDto>()

                if (matchedUsers.isEmpty()) {
                    isLoading = false
                    NotificationManager.addToast("Failed: Invalid Username or Security PIN.", isSuccess = false)
                    return@launch
                }

                appState.supabase.from("users").update(
                    {
                        set("password", trimmedNewPassword)
                    }
                ) {
                    filter {
                        eq("email", fullUsername)
                    }
                }

                isLoading = false
                NotificationManager.addToast("Password reset successfully! You can log in now.", isSuccess = true)

            } catch (e: Exception) {
                isLoading = false
                Log.e("ResetPasswordError", "Reset password failed", e)
                NotificationManager.addToast("Error resetting password: ${e.localizedMessage}", isSuccess = false)
            }
        }
    }
}