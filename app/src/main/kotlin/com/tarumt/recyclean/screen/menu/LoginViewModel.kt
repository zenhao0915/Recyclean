package com.tarumt.recyclean.screen.menu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.HomePageDestination
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.util.data.User
import com.tarumt.recyclean.util.data.UserState

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
                "[Debug Mode] Bypassed Firebase authentication!",
                isSuccess = true
            )
            appState.navigator.navigateTo(HomePageDestination, appState.lastTouchOffset)
            return
        }

        // Firebase Connection
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

        appState.auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                if (uid != null) {
                    fetchUserRoleAndNavigate(uid, trimmedEmail)
                } else {
                    isLoading = false
                    NotificationManager.addToast(
                        "Session error: User UID missing.",
                        isSuccess = false
                    )
                }
            }
            .addOnFailureListener { exception ->
                isLoading = false
                NotificationManager.addToast(
                    exception.localizedMessage ?: "Login failed.",
                    isSuccess = false
                )
            }
    }

    private fun fetchUserRoleAndNavigate(uid: String, email: String) {
        appState.db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                isLoading = false
                val roleString = document.getString("role") ?: "User"

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
            }
            .addOnFailureListener { e ->
                isLoading = false
                NotificationManager.addToast(
                    "Failed to fetch user profile: ${e.localizedMessage}",
                    isSuccess = false
                )
            }
    }

    fun processRegisterUser(userName: String) {
        NotificationManager.addToast("User $userName Registered Successfully!")
    }

    fun processForgetPassword(email: String) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            NotificationManager.addToast("Please enter your email address in the field above.", isSuccess = false)
            return
        }

        if (appState.isDebuggerMode) {
            NotificationManager.addToast("🔧 [Debug] Simulated sending password reset email to $trimmedEmail", isSuccess = true)
            return
        }

        isLoading = true
        appState.auth.sendPasswordResetEmail(trimmedEmail)
            .addOnSuccessListener {
                isLoading = false
                NotificationManager.addToast("Password reset link sent! Please check your email inbox.", isSuccess = true)
            }
            .addOnFailureListener { e ->
                isLoading = false
                NotificationManager.addToast("Failed to send reset link: ${e.localizedMessage}", isSuccess = false)
            }
    }
}