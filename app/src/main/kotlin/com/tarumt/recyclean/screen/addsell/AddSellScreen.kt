package com.tarumt.recyclean.screen.addsell

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.bronzeColor
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.greenCyanColor
import com.tarumt.recyclean.common.lightBlueColor
import com.tarumt.recyclean.common.orangeCreamColor
import com.tarumt.recyclean.common.skyBlueColor
import com.tarumt.recyclean.navigation.AddSellPageDestination
import com.tarumt.recyclean.navigation.MeetingPageDestination
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.util.DrawTemplate
import com.tarumt.recyclean.util.GlassBox
import com.tarumt.recyclean.util.data.Appointment
import com.tarumt.recyclean.util.data.AppointmentStatus
import com.tarumt.recyclean.util.data.Sellers
import com.tarumt.recyclean.util.data.toDto
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

data class SalvageablePart(
    val name: String, val estimatedPrice: Double, var isSelected: Boolean = false
)

fun String.convertToPart() = appState.apply {
    deviceToSell = this@convertToPart
    navigator.navigateTo(AddSellPageDestination, Offset.Zero)
}

@SuppressLint("DefaultLocale")
@Composable
@Preview
fun AddSellScreen(viewModel: AddSellViewModel = viewModel()) = DrawTemplate {
    val sellerRowScrollState = rememberScrollState()

    var manualInput by remember(appState.deviceToSell) {
        mutableStateOf(
            appState.deviceToSell ?: ""
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            viewModel.analyzeDeviceImage(bitmap)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            NotificationManager.addToast(
                "Camera permission is required to scan devices.",
                isSuccess = false
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Recycle & Salvage Parts",
            fontFamily = defaultFont,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Upload a photo or enter the model. Our AI will automatically identify salvageable components for merchants to bid.",
            fontFamily = defaultFont,
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color.Gray.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center
        ) {
            appState.cachedBitmap?.let { cbm ->
                Image(
                    bitmap = cbm.asImageBitmap(),
                    contentDescription = "Captured Device",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { appState.cachedBitmap = null; appState.showResult = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } ?: run {
                GlassBox {
                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }, shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Take Photo (AI Scan)",
                            fontFamily = defaultFont,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Text(
            text = "OR",
            fontFamily = defaultFont,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = manualInput,
            onValueChange = {
                manualInput = it
                appState.deviceToSell = it
            },
            placeholder = {
                Text(
                    text = "PlayStation 5 / iPad Pro",
                    fontSize = 14.sp,
                    color = Color.LightGray.copy(alpha = 0.75f)
                )
            },
            label = { Text(text = "Manual Input Device Model") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = greenCyanColor, focusedLabelColor = greenCyanColor
            )
        )

        if (manualInput.isNotEmpty() && appState.cachedBitmap == null) {
            Button(
                onClick = { viewModel.analyzeDeviceText(manualInput) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Analyze Text with AI", fontFamily = defaultFont)
            }
        }

        if (viewModel.errorMessage.isNotEmpty()) {
            Text(
                text = viewModel.errorMessage,
                color = Color.Red,
                fontSize = 13.sp,
                fontFamily = defaultFont
            )
        }

        AnimatedVisibility(visible = viewModel.isAnalyzing) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                CircularProgressIndicator(color = skyBlueColor)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Thinking...",
                    fontFamily = defaultFont,
                    fontSize = 14.sp,
                    color = skyBlueColor
                )
            }
        }

        AnimatedVisibility(visible = appState.showResult) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(0.5.dp, Color.LightGray, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "📦 Identified: ${appState.detectedDeviceName}",
                    fontFamily = defaultFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF3B30)
                )

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Text(
                    text = "Select parts you wish to sell:",
                    fontFamily = defaultFont,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                appState.cachedPartList.forEach { part ->
                    var checked by remember(part.isSelected) { mutableStateOf(part.isSelected) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = checked, onCheckedChange = {
                                    checked = it
                                    part.isSelected = it
                                }, colors = CheckboxDefaults.colors(checkedColor = bronzeColor)
                            )
                            Text(
                                modifier = Modifier.widthIn(max = 160.dp),
                                text = part.name,
                                fontFamily = defaultFont,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Start
                            )
                        }
                        Text(
                            text = "RM ${String.format("%.2f", part.estimatedPrice)}",
                            textAlign = TextAlign.Center,
                            fontFamily = defaultFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Text(
                    text = "Select Target Recycler:",
                    fontFamily = defaultFont,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(sellerRowScrollState)
                        .background(Color.Gray.copy(alpha = 0.08f), CircleShape)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Sellers.entries.forEach { seller ->
                        val isSelected = appState.selectedSeller == seller
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    color = if (isSelected) lightBlueColor else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { appState.selectedSeller = seller }
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center) {
                            Text(
                                text = seller.sellerName,
                                fontFamily = defaultFont,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                val totalPrice =
                    appState.cachedPartList.filter { it.isSelected }.sumOf { it.estimatedPrice }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Est. Value:",
                        fontFamily = defaultFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "RM ${String.format("%.2f", totalPrice)}",
                        fontFamily = defaultFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = orangeCreamColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val selectedParts = appState.cachedPartList.filter { it.isSelected }

                        if (selectedParts.isNotEmpty()) {
                            val currentEmail = appState.currentUser?.userNameWithEmail?.trim()
                            val activeUserName =
                                if (!currentEmail.isNullOrBlank()) currentEmail else "DebugUser"

                            val newAppointment = Appointment(
                                appointmentId = "APT-${
                                    System.currentTimeMillis().toString().takeLast(4)
                                }",
                                userName = activeUserName,
                                deviceName = appState.detectedDeviceName,
                                scheduledDate = "Pending Date",
                                estimatedValue = totalPrice,
                                status = AppointmentStatus.PENDING,
                                selectedParts = selectedParts,
                                targetSeller = appState.selectedSeller.sellerName
                            )
                            if (!appState.isDebuggerMode) {
                                appState.scope.launch {
                                    try {
                                        appState.supabase.from("appointments")
                                            .insert(newAppointment.toDto())
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                            appState.pendingAppointments.add(0, newAppointment)
                            appState.navigator.navigateTo(
                                MeetingPageDestination,
                                appState.lastTouchOffset
                            )

                            appState.cachedBitmap = null
                            manualInput = ""
                            appState.deviceToSell = ""
                            appState.showResult = false
                            appState.cachedPartList.clear()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = orangeCreamColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Assign To Seller",
                        fontFamily = defaultFont,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}