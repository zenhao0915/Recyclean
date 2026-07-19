package com.tarumt.recyclean

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.AddSellPageDestination
import com.tarumt.recyclean.navigation.AppNavigator
import com.tarumt.recyclean.navigation.AppointmentPageDestination
import com.tarumt.recyclean.navigation.DataPageDestination
import com.tarumt.recyclean.navigation.LoginPageDestination
import com.tarumt.recyclean.navigation.ProfilePageDestination
import com.tarumt.recyclean.screen.AddSellScreen
import com.tarumt.recyclean.screen.AppointmentListScreen
import com.tarumt.recyclean.screen.AppointmentScreen
import com.tarumt.recyclean.screen.HomeScreen
import com.tarumt.recyclean.screen.LoginScreen
import com.tarumt.recyclean.screen.ProfileScreen
import com.tarumt.recyclean.util.DrawNavigator

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun App() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val change = event.changes.firstOrNull()
                            if (change != null && change.pressed && !change.previousPressed) {
                                appState.lastTouchOffset = change.position
                            }
                        }
                    }
                }
        ) {
            Scaffold(containerColor = Color.White, bottomBar = {
                // Navigator
                if (appState.navigator.current != null && appState.navigator.current !is LoginPageDestination) DrawNavigator()
            }, floatingActionButton = {}) { innerPadding ->
                Box(modifier = Modifier.padding(if (appState.navigator.current != null && appState.navigator.current !is LoginPageDestination) innerPadding else PaddingValues(0.dp))) {
                    AppNavigator(appState.navigator, homeContent = {
                        LoginScreen()
                    }, destinationContent = { destination ->
                        when (destination) {
                            is LoginPageDestination -> LoginScreen()
                            is AppointmentPageDestination -> AppointmentScreen()
                            is AddSellPageDestination -> AddSellScreen()
                            is DataPageDestination -> AppointmentListScreen(onAppointmentClick = { })
                            is ProfilePageDestination -> ProfileScreen()
                            else -> HomeScreen()
                        }
                    })
                }
            }
        }
    }
}