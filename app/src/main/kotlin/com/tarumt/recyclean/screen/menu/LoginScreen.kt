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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.recyclean.R
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.defaultFontSize
import com.tarumt.recyclean.common.greenCyanColor
import com.tarumt.recyclean.common.skyBlueColor
import com.tarumt.recyclean.notification.NotificationManager
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

        val username = rememberSaveable { mutableStateOf("") }
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
                            username = username.value,
                            onUsernameChange = { username.value = it.replace("\\", "") },
                            password = password.value,
                            onPasswordChange = { password.value = it.replace("\\", "") })
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
                            username = username.value,
                            onUsernameChange = { username.value = it.replace("\\", "") },
                            password = password.value,
                            onPasswordChange = { password.value = it.replace("\\", "") })
                    }
                }
            }
        }

        // 🌟 统一全屏转圈加载遮罩
        if (viewModel.isLoading) {
            Dialog(
                onDismissRequest = { },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                GlassBox(
                    modifier = Modifier
                        .width(260.dp)
                        .height(150.dp),
                    shape = RoundedCornerShape(20.dp),
                    borderWidth = 1.2.dp,
                    isDarkTheme = false,
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = skyBlueColor,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = viewModel.loadingMessage,
                            fontFamily = defaultFont,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

@Composable
fun LoginFormFields(
    viewModel: LoginViewModel,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit
) {
    val showRegisterPinDialog = rememberSaveable { mutableStateOf(false) }
    val showForgetPasswordDialog = rememberSaveable { mutableStateOf(false) }

    val registerPin = rememberSaveable { mutableStateOf("") }

    val resetUsername = rememberSaveable { mutableStateOf("") }
    val resetPin = rememberSaveable { mutableStateOf("") }
    val resetNewPassword = rememberSaveable { mutableStateOf("") }

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
            value = username,
            onValueChange = onUsernameChange,
            placeholder = {
                Text(
                    text = "Username",
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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
        // Register Button
        GlassBox(
            modifier = Modifier
                .width(120.dp)
                .height(45.dp)
                .background(color = greenCyanColor, shape = CircleShape)
                .clip(CircleShape)
                .clickable(enabled = !viewModel.isLoading, onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        NotificationManager.addToast(
                            "Please fill in Username and Password first.",
                            isSuccess = false
                        )
                    } else if (appState.currentUserState != UserState.Normal) {
                        NotificationManager.addToast(
                            "Registration is restricted to Normal users.",
                            isSuccess = false
                        )
                    } else {
                        showRegisterPinDialog.value = true
                    }
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

        // Login Button
        GlassBox(
            modifier = Modifier
                .width(120.dp)
                .height(45.dp)
                .background(color = skyBlueColor, shape = CircleShape)
                .clip(CircleShape)
                .clickable(enabled = !viewModel.isLoading, onClick = {
                    viewModel.processUserLogin(
                        username,
                        password,
                        userState = appState.currentUserState
                    )
                }),
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

    // Role Selection Row
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

    // Forgot Password Text Button
    Text(
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                resetUsername.value = username
                showForgetPasswordDialog.value = true
            },
        text = "Forgot Password?",
        fontFamily = defaultFont,
        fontSize = 12.sp,
        fontWeight = FontWeight.W900,
        textDecoration = TextDecoration.Underline
    )

    // =========================================================================
    // 🌟 1. 注册设置 Security PIN 弹窗
    // =========================================================================
    if (showRegisterPinDialog.value) {
        AlertDialog(
            onDismissRequest = { showRegisterPinDialog.value = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "Set Security PIN",
                    fontFamily = defaultFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Set a 4-digit Security PIN. You will need this PIN to reset your password if you ever forget it.",
                        fontFamily = defaultFont,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = registerPin.value,
                        onValueChange = { input ->
                            if (input.length <= 4) {
                                registerPin.value = input.filter { it.isDigit() }
                            }
                        },
                        label = { Text("4-Digit Security PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (registerPin.value.length == 4) {
                            showRegisterPinDialog.value = false
                            viewModel.processRegisterUser(
                                userNameInput = username,
                                passwordInput = password,
                                securityPinInput = registerPin.value,
                                userState = appState.currentUserState
                            )
                            registerPin.value = ""
                        } else {
                            NotificationManager.addToast(
                                "Security PIN must be exactly 4 digits.",
                                isSuccess = false
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = greenCyanColor),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Register Now", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showRegisterPinDialog.value = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // =========================================================================
    // 🌟 2. 重置密码 弹窗 (PIN 码认证)
    // =========================================================================
    if (showForgetPasswordDialog.value) {
        AlertDialog(
            onDismissRequest = { showForgetPasswordDialog.value = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "Reset Password",
                    fontFamily = defaultFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = resetUsername.value,
                        onValueChange = { resetUsername.value = it },
                        label = { Text("Username") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = resetPin.value,
                        onValueChange = { input ->
                            if (input.length <= 4) {
                                resetPin.value = input.filter { it.isDigit() }
                            }
                        },
                        label = { Text("4-Digit Security PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = resetNewPassword.value,
                        onValueChange = { resetNewPassword.value = it },
                        label = { Text("New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showForgetPasswordDialog.value = false
                        viewModel.processForgetPassword(
                            usernameInput = resetUsername.value,
                            securityPinInput = resetPin.value,
                            newPasswordInput = resetNewPassword.value
                        )
                        resetPin.value = ""
                        resetNewPassword.value = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = skyBlueColor),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Reset Password", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showForgetPasswordDialog.value = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}