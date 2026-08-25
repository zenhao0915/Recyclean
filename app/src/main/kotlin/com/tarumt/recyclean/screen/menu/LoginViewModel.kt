package com.tarumt.recyclean.screen.menu

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.HomePageDestination
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.util.data.User
import com.tarumt.recyclean.util.data.UserProfileDto
import com.tarumt.recyclean.util.data.UserState
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

class LoginViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set

    var loadingMessage by mutableStateOf("Connecting To Database....")
        private set
    suspend fun checkAutoLogin(onComplete: () -> Unit = {}): Boolean {
        if (appState.isDebuggerMode) {
            onComplete()
            return false
        }

        return withContext(Dispatchers.IO) {
            try {
                val isSuccess = withTimeoutOrNull(8000L.milliseconds) {
                    appState.supabase.auth.awaitInitialization()

                    val currentSession = appState.supabase.auth.currentSessionOrNull()
                    val currentSupabaseUser = appState.supabase.auth.currentUserOrNull()

                    if (currentSession == null || currentSupabaseUser == null) {
                        return@withTimeoutOrNull false
                    }

                    val uid = currentSupabaseUser.id
                    val email = currentSupabaseUser.email.orEmpty()

                    val profile = appState.supabase.from("users")
                        .select(Columns.list("id", "username", "role", "is_blacklisted", "blacklist_reason")) {
                            filter { eq("id", uid) }
                        }.decodeSingle<UserProfileDto>()

                    if (profile.isBlacklisted == true) {
                        appState.supabase.auth.signOut()
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
                        userNameWithEmail = profile.username ?: email.substringBefore("@"),
                        currentUserState = mappedState
                    )

                    NotificationManager.addToast("Session Restored!", isSuccess = true)

                    // 切回主线程执行页面跳转
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

    fun processUserLogin(userName: String, password: String, userState: UserState) {
        val trimmedUsername = userName.trim()
        val trimmedPassword = password.trim()

        if (appState.isDebuggerMode) {
            val dummyUser = User(
                userNameWithEmail = trimmedUsername.ifBlank { "DebugUser" },
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

        if (trimmedUsername.isBlank() || trimmedPassword.isBlank()) {
            NotificationManager.addToast(
                "Please fill in both username and password.",
                isSuccess = false
            )
            return
        }

        val trimmedEmail = usernameToEmail(trimmedUsername)

        isLoading = true
        loadingMessage = "Connecting To Database...."

        viewModelScope.launch {
            try {
                appState.supabase.auth.signInWith(Email) {
                    email = trimmedEmail
                    this.password = trimmedPassword
                }

                val currentSupabaseUser = appState.supabase.auth.currentUserOrNull()
                val uid = currentSupabaseUser?.id

                if (uid != null) {
                    loadingMessage = "Verifying Account Status...."
                    fetchUserRoleAndNavigate(uid, trimmedUsername, expectedRole = userState)
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

    private fun fetchUserRoleAndNavigate(uid: String, username: String, expectedRole: UserState) {
        viewModelScope.launch {
            try {
                val profile = appState.supabase.from("users")
                    .select {
                        filter {
                            eq("id", uid)
                        }
                    }.decodeSingle<UserProfileDto>()

                // 🌟 2. 正常登录拦截：若已封禁，登出并提示原因
                if (profile.isBlacklisted == true) {
                    appState.supabase.auth.signOut()
                    appState.currentUser = null
                    isLoading = false

                    val reason =
                        profile.blacklistReason?.ifBlank { "Violation of platform policies" }
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

                if (mappedState != expectedRole) {
                    appState.supabase.auth.signOut()
                    appState.currentUser = null
                    isLoading = false

                    NotificationManager.addToast(
                        "Access Denied: This account is registered as '${mappedState.name}', not '${expectedRole.name}'!",
                        isSuccess = false
                    )
                    return@launch
                }

                isLoading = false
                appState.currentUserState = mappedState
                appState.currentUser = User(
                    userNameWithEmail = username,
                    currentUserState = mappedState
                )

                NotificationManager.addToast("Welcome back, $username!", isSuccess = true)
                appState.navigator.navigateTo(HomePageDestination, appState.lastTouchOffset)
            } catch (e: Exception) {
                isLoading = false
                appState.supabase.auth.signOut()
                NotificationManager.addToast(
                    "Failed to fetch user profile: ${e.localizedMessage}",
                    isSuccess = false
                )
            }
        }
    }

    private fun usernameToEmail(username: String): String {
        return username.trim().lowercase().replace(" ", "") + "@recyclean.app"
    }

    fun processRegisterUser(
        userNameInput: String,
        passwordInput: String,
        securityPinInput: String,
        userState: UserState = appState.currentUserState
    ) {
        val trimmedUsername = userNameInput.trim()
        val trimmedPassword = passwordInput.trim()
        val trimmedPin = securityPinInput.trim()

        if (userState != UserState.Normal) {
            NotificationManager.addToast(
                "Registration is restricted to Normal users. Merchants and Admins are pre-registered by system.",
                isSuccess = false
            )
            return
        }

        if (trimmedUsername.isBlank() || trimmedPassword.isBlank() || trimmedPin.isBlank()) {
            NotificationManager.addToast(
                "Please fill in Username, Password, and Security PIN.",
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
                "[Debug] User $userNameInput registered successfully!",
                isSuccess = true
            )
            processUserLogin(trimmedUsername, trimmedPassword, userState)
            return
        }

        isLoading = true
        loadingMessage = "Connecting To Database...."

        viewModelScope.launch {
            try {
                loadingMessage = "Checking Username Availability...."
                val existingUsers = appState.supabase.from("users")
                    .select {
                        filter { eq("username", trimmedUsername) }
                    }.decodeList<UserProfileDto>()

                if (existingUsers.isNotEmpty()) {
                    isLoading = false
                    NotificationManager.addToast(
                        "Username '$trimmedUsername' is already taken!",
                        isSuccess = false
                    )
                    return@launch
                }

                loadingMessage = "Creating Account & Syncing Profile...."
                val virtualEmail = usernameToEmail(trimmedUsername)
                appState.supabase.auth.signUpWith(Email) {
                    email = virtualEmail
                    password = trimmedPassword
                }

                val newUser = appState.supabase.auth.currentUserOrNull()

                if (newUser != null) {
                    appState.supabase.from("users").upsert(
                        UserProfileDto(
                            id = newUser.id,
                            username = trimmedUsername,
                            securityPin = trimmedPin,
                            role = UserState.Normal.name
                        )
                    )

                    isLoading = false
                    NotificationManager.addToast("Registered successfully!", isSuccess = true)
                    processUserLogin(trimmedUsername, trimmedPassword, UserState.Normal)
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

    fun processForgetPassword(
        usernameInput: String,
        securityPinInput: String,
        newPasswordInput: String
    ) {
        val trimmedUsername = usernameInput.trim()
        val trimmedPin = securityPinInput.trim()
        val trimmedNewPassword = newPasswordInput.trim()

        if (trimmedUsername.isBlank() || trimmedPin.isBlank() || trimmedNewPassword.isBlank()) {
            NotificationManager.addToast(
                "Please enter Username, Security PIN, and New Password.",
                isSuccess = false
            )
            return
        }

        if (trimmedNewPassword.length < 6) {
            NotificationManager.addToast(
                "New password must be at least 6 characters.",
                isSuccess = false
            )
            return
        }

        if (appState.isDebuggerMode) {
            NotificationManager.addToast(
                "[Debug] Simulated sending password using pin",
                isSuccess = true
            )
            return
        }

        isLoading = true
        loadingMessage = "Verifying Security PIN With Database...."

        viewModelScope.launch {
            try {
                val isSuccess = appState.supabase.postgrest.rpc(
                    function = "reset_password_with_pin",
                    parameters = mapOf(
                        "target_username" to trimmedUsername,
                        "input_pin" to trimmedPin,
                        "new_password" to trimmedNewPassword
                    )
                ).decodeAs<Boolean>()

                isLoading = false

                if (isSuccess) {
                    NotificationManager.addToast(
                        "Password reset successfully! You can log in now.",
                        isSuccess = true
                    )
                } else {
                    NotificationManager.addToast(
                        "Failed: Invalid Username or Security PIN.",
                        isSuccess = false
                    )
                }
            } catch (e: Exception) {
                isLoading = false
                NotificationManager.addToast(
                    "Error resetting password: ${e.localizedMessage}",
                    isSuccess = false
                )
            }
        }
    }
}