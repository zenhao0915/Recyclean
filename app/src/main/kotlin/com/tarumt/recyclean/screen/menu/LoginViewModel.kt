package com.tarumt.recyclean.screen.menu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.HomePageDestination
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.util.data.User
import com.tarumt.recyclean.util.data.UserState
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val id: String,
    val role: String? = "Normal"
)

class LoginViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set

    fun processUserLogin(userName: String, password: String, userState: UserState) {
        if (appState.isDebuggerMode) {
            val dummyUser = User(
                userName = userName.ifBlank { "DebugUser" },
                password = password.hashCode(),
                currentUserState = userState
            )
            appState.currentUser = dummyUser
            appState.currentUserState = userState

            NotificationManager.addToast(
                "[Debug Mode] Bypassed authentication!",
                isSuccess = true
            )
            appState.navigator.navigateTo(HomePageDestination, appState.lastTouchOffset)
            return
        }

        val trimmedEmail = userName.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            NotificationManager.addToast(
                "Please fill in both email and password.",
                isSuccess = false
            )
            return
        }

        isLoading = true

        viewModelScope.launch {
            try {
                appState.supabase.auth.signInWith(Email) {
                    email = trimmedEmail
                    this.password = trimmedPassword
                }

                val currentSupabaseUser = appState.supabase.auth.currentUserOrNull()
                val uid = currentSupabaseUser?.id

                if (uid != null) {
                    fetchUserRoleAndNavigate(uid, trimmedEmail)
                } else {
                    isLoading = false
                    NotificationManager.addToast(
                        "Session error: User UID missing.",
                        isSuccess = false
                    )
                }
            } catch (e: Exception) {
                isLoading = false
                NotificationManager.addToast(
                    e.localizedMessage ?: "Login failed.",
                    isSuccess = false
                )
            }
        }
    }

    private fun fetchUserRoleAndNavigate(uid: String, email: String) {
        viewModelScope.launch {
            try {
                val profile = appState.supabase.from("users")
                    .select {
                        filter {
                            eq("id", uid)
                        }
                    }.decodeSingle<UserProfileDto>()

                isLoading = false
                val roleString = profile.role ?: "User"

                val mappedState = when (roleString.lowercase()) {
                    "admin" -> UserState.Admin
                    "thirdparty", "recycler" -> UserState.ThirdParty
                    else -> UserState.Normal
                }

                appState.currentUserState = mappedState
                appState.currentUser = User(
                    userName = email,
                    password = 0,
                    currentUserState = mappedState
                )

                NotificationManager.addToast("Welcome back!", isSuccess = true)
                appState.navigator.navigateTo(HomePageDestination, appState.lastTouchOffset)
            } catch (e: Exception) {
                isLoading = false
                NotificationManager.addToast(
                    "Failed to fetch user profile: ${e.localizedMessage}",
                    isSuccess = false
                )
            }
        }
    }

    fun processRegisterUser(userName: String, password: String, userState: UserState = appState.currentUserState) {
        val trimmedEmail = userName.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            NotificationManager.addToast(
                "Please enter both email and password to register.",
                isSuccess = false
            )
            return
        }

        if (trimmedPassword.length < 6) {
            NotificationManager.addToast(
                "Password must be at least 6 characters.",
                isSuccess = false
            )
            return
        }

        if (appState.isDebuggerMode) {
            NotificationManager.addToast(
                "[Debug] User $trimmedEmail registered successfully!",
                isSuccess = true
            )
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                appState.supabase.auth.signUpWith(Email) {
                    email = trimmedEmail
                    this.password = trimmedPassword
                }

                val newUser = appState.supabase.auth.currentUserOrNull()

                if (newUser != null) {
                    runCatching {
                        appState.supabase.from("users").insert(
                            UserProfileDto(
                                id = newUser.id,
                                role = userState.name
                            )
                        )
                    }

                    isLoading = false
                    NotificationManager.addToast("Registration successful! You can now log in.", isSuccess = true)
                } else {
                    isLoading = false
                    NotificationManager.addToast("Registration submitted. Please check email for confirmation.", isSuccess = true)
                }
            } catch (e: Exception) {
                isLoading = false
                NotificationManager.addToast(
                    "Registration failed: ${e.localizedMessage}",
                    isSuccess = false
                )
            }
        }
    }

    fun processForgetPassword(email: String) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            NotificationManager.addToast(
                "Please enter your email address in the field above.",
                isSuccess = false
            )
            return
        }

        if (appState.isDebuggerMode) {
            NotificationManager.addToast(
                "[Debug] Simulated sending password reset email to $trimmedEmail",
                isSuccess = true
            )
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                appState.supabase.auth.resetPasswordForEmail(trimmedEmail)
                isLoading = false
                NotificationManager.addToast(
                    "Password reset link sent! Please check your email inbox.",
                    isSuccess = true
                )
            } catch (e: Exception) {
                isLoading = false
                NotificationManager.addToast(
                    "Failed to send reset link: ${e.localizedMessage}",
                    isSuccess = false
                )
            }
        }
    }
}