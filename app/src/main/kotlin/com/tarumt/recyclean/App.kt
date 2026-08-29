package com.tarumt.recyclean

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.vanillaColor
import com.tarumt.recyclean.navigation.AddSellPageDestination
import com.tarumt.recyclean.navigation.AppNavigator
import com.tarumt.recyclean.navigation.DataPageDestination
import com.tarumt.recyclean.navigation.LoginPageDestination
import com.tarumt.recyclean.navigation.MeetingPageDestination
import com.tarumt.recyclean.navigation.ProfilePageDestination
import com.tarumt.recyclean.navigation.VerificationPageDestination
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.screen.addsell.AddSellScreen
import com.tarumt.recyclean.screen.addsell.ThirdPartyAddSellScreen
import com.tarumt.recyclean.screen.data.AdminDataScreen
import com.tarumt.recyclean.screen.data.DefaultDataScreen
import com.tarumt.recyclean.screen.data.ThirdPartyDataScreen
import com.tarumt.recyclean.screen.meeting.DefaultMeetingScreen
import com.tarumt.recyclean.screen.meeting.ThirdPartyMeetingScreen
import com.tarumt.recyclean.screen.meeting.ThirdPartyVerificationScreen
import com.tarumt.recyclean.screen.menu.HomeScreen
import com.tarumt.recyclean.screen.menu.LoginScreen
import com.tarumt.recyclean.screen.menu.LoginViewModel
import com.tarumt.recyclean.screen.menu.ProfileScreen
import com.tarumt.recyclean.util.DrawNavigator
import com.tarumt.recyclean.util.data.UserState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun App() {
    appState.scope = rememberCoroutineScope()
    NotificationManager.UpdateNotification()

    val loginViewModel: LoginViewModel = viewModel()
    var isCheckingAutoLogin by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (!isCheckingAutoLogin) return@LaunchedEffect
        loginViewModel.checkAutoLogin {
            isCheckingAutoLogin = false
        }
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val showNav = appState.navigator.current != null && appState.navigator.current !is LoginPageDestination

    MaterialTheme {
        if (isCheckingAutoLogin) {
            SplashScreen()
        } else {
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
                if (isLandscape) {
                    // 🌟 1. 横屏：左侧放垂直侧边栏，右侧为 100% 展开的页面内容容器
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        if (showNav) {
                            DrawNavigator()
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            MainContentNavigator()
                            NotificationManager.CallToast()
                        }
                    }
                } else {
                    // 📱 2. 竖屏：维持原生 Scaffold 底部导航
                    Scaffold(
                        containerColor = Color.White,
                        bottomBar = {
                            if (showNav) DrawNavigator()
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(if (showNav) innerPadding else PaddingValues(0.dp))
                        ) {
                            MainContentNavigator()
                            NotificationManager.CallToast()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainContentNavigator() {
    AppNavigator(appState.navigator, homeContent = {
        LoginScreen()
    }, destinationContent = { destination ->
        val currentUserState = appState.currentUserState

        when (destination) {
            is LoginPageDestination -> LoginScreen()
            is ProfilePageDestination -> ProfileScreen()

            is VerificationPageDestination -> {
                appState.currentVerificationAppointment?.let { appt ->
                    ThirdPartyVerificationScreen(appointment = appt)
                }
            }

            is MeetingPageDestination -> {
                when (currentUserState) {
                    UserState.ThirdParty -> ThirdPartyMeetingScreen()
                    else -> DefaultMeetingScreen()
                }
            }

            is AddSellPageDestination -> {
                when (currentUserState) {
                    UserState.ThirdParty -> ThirdPartyAddSellScreen()
                    else -> AddSellScreen()
                }
            }

            is DataPageDestination -> {
                when (currentUserState) {
                    UserState.Admin -> AdminDataScreen()
                    UserState.ThirdParty -> ThirdPartyDataScreen()
                    else -> DefaultDataScreen()
                }
            }

            else -> HomeScreen()
        }
    })
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            CircularProgressIndicator(
                color = vanillaColor,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}