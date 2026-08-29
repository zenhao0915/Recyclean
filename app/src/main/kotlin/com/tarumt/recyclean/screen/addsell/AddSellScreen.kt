package com.tarumt.recyclean.screen.addsell

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.bronzeColor
import com.tarumt.recyclean.common.defaultBoldFont
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class SalvageablePart(
    val name: String,
    val estimatedPrice: Double,
    var isSelected: Boolean = false
)

fun String.convertToPart() = appState.apply {
    deviceToSell = this@convertToPart
    navigator.navigateTo(AddSellPageDestination, Offset.Zero)
}

@SuppressLint("DefaultLocale")
@Composable
@Preview
fun AddSellScreen(viewModel: AddSellViewModel = viewModel()) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    var manualInput by remember(appState.deviceToSell) {
        mutableStateOf(appState.deviceToSell ?: "")
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

    if (isLandscape) {
        // 🔄 Landscape 左右双栏布局
        AddSellLandscapeContent(
            viewModel = viewModel,
            manualInput = manualInput,
            onManualInputChange = {
                manualInput = it
                appState.deviceToSell = it
            },
            onCameraClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
        )
    } else {
        // 📱 Portrait 原版单列滚动布局
        AddSellPortraitContent(
            viewModel = viewModel,
            manualInput = manualInput,
            onManualInputChange = {
                manualInput = it
                appState.deviceToSell = it
            },
            onCameraClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }
        )
    }
}

// =============================================================================
// 📱 1. Portrait 单列视图 (保持你的原版结构)
// =============================================================================
@SuppressLint("DefaultLocale")
@Composable
private fun AddSellPortraitContent(
    viewModel: AddSellViewModel,
    manualInput: String,
    onManualInputChange: (String) -> Unit,
    onCameraClick: () -> Unit
) = DrawTemplate {
    val sellerRowScrollState = rememberScrollState()

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
            fontFamily = defaultBoldFont,
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

        // 相机 / 图片预览
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color.Gray.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            appState.cachedBitmap?.let { cbm ->
                Image(
                    bitmap = cbm.asImageBitmap(),
                    contentDescription = "Captured Device",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = {
                        appState.cachedBitmap = null
                        appState.showResult = false
                    },
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
                        onClick = onCameraClick,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Take Photo (AI Scan)",
                            fontFamily = defaultBoldFont,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Text(
            text = "OR",
            fontFamily = defaultBoldFont,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = manualInput,
            onValueChange = onManualInputChange,
            placeholder = {
                Text(
                    text = "PlayStation 5 / iPad Pro",
                    fontSize = 14.sp,
                    color = Color.LightGray.copy(alpha = 0.75f)
                )
            },
            label = { Text(text = "Manual Input Device Model", fontFamily = defaultFont) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = greenCyanColor,
                focusedLabelColor = greenCyanColor
            )
        )

        if (manualInput.isNotEmpty() && appState.cachedBitmap == null) {
            Button(
                onClick = { viewModel.analyzeDeviceText(manualInput) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Predict Price with AI", fontFamily = defaultBoldFont)
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

        // 识别结果卡片
        AnimatedVisibility(visible = appState.showResult) {
            SalvationResultCard(
                sellerRowScrollState = sellerRowScrollState,
                onAssignSuccess = { onManualInputChange("") }
            )
        }
    }
}

// =============================================================================
// 🔄 2. Landscape 双列视图 (左侧输入/拍摄，右侧估价结果)
// =============================================================================
@Composable
private fun AddSellLandscapeContent(
    viewModel: AddSellViewModel,
    manualInput: String,
    onManualInputChange: (String) -> Unit,
    onCameraClick: () -> Unit
) = Box(
    modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White),
    contentAlignment = Alignment.Center
) {
    val sellerRowScrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 左列：设备拍照与文字输入
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Recycle & Salvage Parts",
                fontFamily = defaultBoldFont,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Scan photo or enter model for instant AI evaluation.",
                fontFamily = defaultFont,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color.Gray.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                appState.cachedBitmap?.let { cbm ->
                    Image(
                        bitmap = cbm.asImageBitmap(),
                        contentDescription = "Captured Device",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = {
                            appState.cachedBitmap = null
                            appState.showResult = false
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                } ?: run {
                    GlassBox {
                        Button(
                            onClick = onCameraClick,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Take Photo (AI Scan)",
                                fontFamily = defaultBoldFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = manualInput,
                onValueChange = onManualInputChange,
                placeholder = {
                    Text(
                        text = "e.g. PlayStation 5 / iPad Pro",
                        fontSize = 12.sp,
                        color = Color.LightGray.copy(alpha = 0.75f)
                    )
                },
                label = { Text(text = "Device Model", fontFamily = defaultFont, fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = greenCyanColor,
                    focusedLabelColor = greenCyanColor
                )
            )

            if (manualInput.isNotEmpty() && appState.cachedBitmap == null) {
                Button(
                    onClick = { viewModel.analyzeDeviceText(manualInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    Text(
                        text = "Predict Price with AI",
                        fontFamily = defaultBoldFont,
                        fontSize = 13.sp
                    )
                }
            }

            if (viewModel.errorMessage.isNotEmpty()) {
                Text(
                    text = viewModel.errorMessage,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontFamily = defaultFont
                )
            }

            AnimatedVisibility(visible = viewModel.isAnalyzing) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    CircularProgressIndicator(color = skyBlueColor, modifier = Modifier.size(20.dp))
                    Text(
                        text = "AI Analyzing...",
                        fontFamily = defaultFont,
                        fontSize = 13.sp,
                        color = skyBlueColor
                    )
                }
            }
        }

        // 右列：估价拆解明细与回收商指派卡片
        Column(
            modifier = Modifier
                .weight(1.15f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            if (appState.showResult) {
                SalvationResultCard(
                    sellerRowScrollState = sellerRowScrollState,
                    onAssignSuccess = { onManualInputChange("") }
                )
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Waiting",
                            tint = skyBlueColor,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Awaiting Device Evaluation",
                            fontFamily = defaultBoldFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Take a photo or input a device model to preview AI salvageable parts breakdown.",
                            fontFamily = defaultFont,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// 📦 3. 零件估价与回收商指派公共卡片
// =============================================================================
@SuppressLint("DefaultLocale")
@Composable
private fun SalvationResultCard(
    sellerRowScrollState: androidx.compose.foundation.ScrollState,
    onAssignSuccess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(0.5.dp, Color.LightGray, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "📦 Identified: ${appState.detectedDeviceName}",
            fontFamily = defaultBoldFont,
            fontSize = 15.sp,
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
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                            part.isSelected = it
                        },
                        colors = CheckboxDefaults.colors(checkedColor = bronzeColor)
                    )
                    Text(
                        modifier = Modifier.widthIn(max = 160.dp),
                        text = part.name,
                        fontFamily = defaultFont,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Start
                    )
                }
                Text(
                    text = "RM ${String.format("%.2f", part.estimatedPrice)}",
                    textAlign = TextAlign.Center,
                    fontFamily = defaultFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
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
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = seller.sellerName,
                        fontFamily = defaultFont,
                        fontSize = 12.sp,
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
                fontFamily = defaultBoldFont,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "RM ${String.format("%.2f", totalPrice)}",
                fontFamily = defaultBoldFont,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                color = orangeCreamColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                val selectedParts = appState.cachedPartList.filter { it.isSelected }

                if (selectedParts.isNotEmpty()) {
                    val currentEmail = appState.currentUser?.userNameWithEmail?.trim()
                    val activeUserName =
                        if (!currentEmail.isNullOrBlank()) currentEmail else "DebugUser"

                    val newAppointment = Appointment(
                        appointmentId = "APT-${System.currentTimeMillis().toString().takeLast(4)}",
                        userName = activeUserName,
                        deviceName = appState.detectedDeviceName,
                        scheduledDate = LocalDate.now()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
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
                    onAssignSuccess()
                    appState.deviceToSell = ""
                    appState.showResult = false
                    appState.cachedPartList.clear()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = orangeCreamColor),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Text(
                text = "Assign To Seller",
                fontFamily = defaultBoldFont,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}