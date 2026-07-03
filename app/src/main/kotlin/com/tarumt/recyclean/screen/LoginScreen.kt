package com.tarumt.recyclean.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarumt.recyclean.R
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.greenCyanColor
import com.tarumt.recyclean.common.skyBlueColor
import com.tarumt.recyclean.navigation.HomePageDestination
import com.tarumt.recyclean.navigation.navReveal
import com.tarumt.recyclean.util.GlassBox
import com.tarumt.recyclean.util.GlassLiquidSwitch

@Composable
@Preview
fun LoginScreen() = Box(contentAlignment = Alignment.Center) {
    Image(
        painter = painterResource(R.drawable.bg),
        contentScale = ContentScale.Crop,
        contentDescription = "Background",
        modifier = Modifier.fillMaxSize()
    )

    val width = 320.dp
    val height = 500.dp
    val isAdmin = remember { mutableStateOf(true) }
    val userName = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    GlassBox(
        modifier = Modifier
            .width(width)
            .height(height),
        borderWidth = 1.5.dp,
        contentAlignment = Alignment.Center
    ) {
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
                GlassBox(
                    modifier = Modifier
                        .width(250.dp)
                        .height(50.dp),
                    shape = CircleShape,
                    isDarkTheme = true,
                    contentAlignment = Alignment.CenterStart
                ) {
                    TextField(
                        value = userName.value,
                        onValueChange = { userName.value = it },
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
                        value = password.value,
                        onValueChange = { password.value = it },
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    GlassBox(
                        modifier = Modifier
                            .width(120.dp)
                            .height(45.dp)
                            .navReveal(appState.navigator, HomePageDestination)
                            .background(color = greenCyanColor, shape = CircleShape),
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

                // Admin Login Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Admin Login",
                        fontFamily = defaultBoldFont,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.9f)
                    )
                    GlassLiquidSwitch(
                        checked = isAdmin.value, onCheckedChange = {
                            appState.hasLoggedIn = !appState.hasLoggedIn
                            isAdmin.value = appState.hasLoggedIn
                        })
                }

                Text(
                    text = "Forgot Password?",
                    fontFamily = defaultFont,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W900,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}