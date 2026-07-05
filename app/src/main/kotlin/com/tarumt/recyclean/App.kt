package com.tarumt.recyclean

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.AddSellPageDestination
import com.tarumt.recyclean.navigation.AppNavigator
import com.tarumt.recyclean.navigation.AppointmentPageDestination
import com.tarumt.recyclean.navigation.DataPageDestination
import com.tarumt.recyclean.navigation.HomePageDestination
import com.tarumt.recyclean.navigation.LoginPageDestination
import com.tarumt.recyclean.navigation.ProfilePageDestination
import com.tarumt.recyclean.screen.AddSellScreen
import com.tarumt.recyclean.screen.AdminDataSceen
import com.tarumt.recyclean.screen.AppointmentScreen
import com.tarumt.recyclean.screen.HomeScreen
import com.tarumt.recyclean.screen.LoginScreen
import com.tarumt.recyclean.screen.ProfileScreen
import com.tarumt.recyclean.util.DrawNavigator

@Composable
fun App() {
    MaterialTheme {
        AppNavigator(appState.navigator, homeContent = {
            LoginScreen()
        }, destinationContent = { destination ->
            when (destination) {
                is LoginPageDestination -> LoginScreen()
                is HomePageDestination -> HomeScreen()
                is AppointmentPageDestination -> AppointmentScreen()
                is AddSellPageDestination -> AddSellScreen()
                is DataPageDestination -> AdminDataSceen()
                is ProfilePageDestination -> ProfileScreen()
            }
        })

        // Navigator
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(12.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) { if (appState.navigator.current != null && appState.navigator.current !is LoginPageDestination) DrawNavigator() }
    }
}