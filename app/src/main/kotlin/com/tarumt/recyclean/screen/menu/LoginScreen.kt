package com.tarumt.recyclean.screen.menu

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.recyclean.R
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.defaultFontSize
import com.tarumt.recyclean.common.greenCyanColor
import com.tarumt.recyclean.common.skyBlueColor
import com.tarumt.recyclean.navigation.HomePageDestination
import com.tarumt.recyclean.navigation.navReveal
import com.tarumt.recyclean.util.GlassBox
import com.tarumt.recyclean.util.WindowWidthSizeClass
import com.tarumt.recyclean.util.data.UserState

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
@Preview
fun LoginScreen(viewModel: LoginViewModel = viewModel()) =
    Box(contentAlignment = Alignment.Center) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp

        val windowSizeClass = when {
            screenWidth < 600 -> WindowWidthSizeClass.Compact
            screenWidth in 600..839 -> WindowWidthSizeClass.Medium
            else -> WindowWidthSizeClass.Expanded
        }

        val isTablet = windowSizeClass != WindowWidthSizeClass.Compact

        val cardWidth = if (isTablet) 680.dp else 320.dp
        val cardHeight = if (isTablet) 420.dp else 500.dp

        Image(
            painter = painterResource(R.drawable.bg),
            contentScale = ContentScale.Crop,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize()
        )

        val userName = rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }

        GlassBox(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight),
            borderWidth = 1.5.dp,
            contentAlignment = Alignment.Center
        ) {
            if (isTablet) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(40.dp)
                ) {
                    Box(
                        modifier = Modifier.weight(1f), contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(200.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoginFormFields(
                            viewModel,
                            userName = userName.value,
                            onUserNameChange = { userName.value = it },
                            password = password.value,
                            onPasswordChange = { password.value = it })
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(150.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoginFormFields(
                            viewModel,
                            userName = userName.value,
                            onUserNameChange = { userName.value = it },
                            password = password.value,
                            onPasswordChange = { password.value = it })
                    }
                }
            }
        }
    }

@Composable
fun LoginFormFields(
    viewModel: LoginViewModel,
    userName: String,
    onUserNameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    // Username Input
    GlassBox(
        modifier = Modifier
            .width(250.dp)
            .height(50.dp),
        shape = CircleShape,
        isDarkTheme = true,
        contentAlignment = Alignment.CenterStart
    ) {
        TextField(
            value = userName,
            onValueChange = onUserNameChange,
            placeholder = {
                Text(
                    text = "Username/Email",
                    fontFamily = defaultFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black.copy(alpha = 0.5f),
                    textAlign = TextAlign.Start
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
    }

    // Password Input
    GlassBox(
        modifier = Modifier
            .width(250.dp)
            .height(50.dp),
        shape = CircleShape,
        isDarkTheme = true,
        contentAlignment = Alignment.CenterStart
    ) {
        TextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = {
                Text(
                    text = "Password",
                    fontFamily = defaultFont,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black.copy(alpha = 0.5f),
                    textAlign = TextAlign.Start
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
    }

    // Buttons Row
    Row(
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center
    ) {
        GlassBox(
            modifier = Modifier
                .width(120.dp)
                .height(45.dp)
                //.navReveal(appState.navigator, HomePageDestination)
                .background(color = greenCyanColor, shape = CircleShape)
                .clickable(enabled = true, onClick = {
                    viewModel.processRegisterUser(userName)
                }),
            shape = CircleShape,
            isDarkTheme = true,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Register",
                fontFamily = defaultFont,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Spacer(Modifier.width(8.dp))

        GlassBox(
            modifier = Modifier
                .width(120.dp)
                .height(45.dp)
                .navReveal(appState.navigator, HomePageDestination)
                .background(color = skyBlueColor, shape = CircleShape),
            shape = CircleShape,
            isDarkTheme = true,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Login",
                fontFamily = defaultFont,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }

    // Admin/Role Selection Row
    Row(
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.35f), CircleShape)
            .border(width = 0.5.dp, Color.Black.copy(alpha = 0.2f), CircleShape),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserState.entries.forEach { userState ->
            val isSelected = appState.currentUserState == userState
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        color = if (isSelected) skyBlueColor.copy(alpha = 0.5f)
                        else Color.Transparent, shape = CircleShape
                    )
                    .clickable {
                        appState.currentUserState = userState
                        appState.currentUser?.currentUserState = userState
                    }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center) {
                Text(
                    text = userState.name,
                    fontFamily = if (isSelected) defaultBoldFont else defaultFont,
                    fontSize = defaultFontSize,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.6f)
                )
            }
        }
    }

    Text(
        modifier = Modifier.clickable {
            viewModel.processForgetPassword()
        },
        text = "Forgot Password?",
        fontFamily = defaultFont,
        fontSize = 12.sp,
        fontWeight = FontWeight.W900,
        textDecoration = TextDecoration.Underline
    )
}