package com.tarumt.recyclean

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.AppNavigator
import com.tarumt.recyclean.navigation.LoginPageDestination
import com.tarumt.recyclean.screen.HomeScreen
import com.tarumt.recyclean.screen.LoginScreen

@Composable
fun App() {
    val scope = rememberCoroutineScope()

    MaterialTheme {
        AppNavigator(appState.navigator, homeContent = {
            LoginScreen()
        }, destinationContent = { destination ->
            when (destination) {
                is LoginPageDestination -> LoginScreen()
                else -> HomeScreen()
            }
        })
    }
}