package com.tarumt.recyclean.screen.meeting

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.tarumt.recyclean.R
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.navigation.MeetingPageDestination
import com.tarumt.recyclean.util.data.Appointment
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun ThirdPartyVerificationScreen(
    appointment: Appointment,
    viewModel: ThirdPartyVerificationViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(appointment) {
        viewModel.loadAppointment(appointment)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Verify Device Parts",
                        fontWeight = FontWeight.Bold,
                        fontFamily = defaultFont
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        appState.navigator.navigateTo(
                            MeetingPageDestination,
                            Offset.Zero
                        )
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A365D),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isLandscape) {
            // 🔄 横屏双列分栏
            VerificationLandscapeContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                appointment = appointment,
                viewModel = viewModel
            )
        } else {
            // 📱 竖屏单列布局（保持你的原版结构）
            VerificationPortraitContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(20.dp),
                appointment = appointment,
                viewModel = viewModel
            )
        }
    }

    // 完成动画弹窗
    if (viewModel.doneSubmission) {
        LaunchedEffect(Unit) {
            delay(2500L.milliseconds)
            appState.navigator.navigateTo(MeetingPageDestination, Offset.Zero)
        }

        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    DoneAnimation()

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Payment Completed!",
                        fontFamily = defaultFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF2E7D32)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Transferred RM ${String.format("%.2f", viewModel.totalPayout)} to ${appointment.userName}",
                        fontFamily = defaultFont,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

// =============================================================================
// 📱 竖屏视图（你的原始布局）
// =============================================================================
@SuppressLint("DefaultLocale")
@Composable
private fun VerificationPortraitContent(
    modifier: Modifier = Modifier,
    appointment: Appointment,
    viewModel: ThirdPartyVerificationViewModel
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = appointment.deviceName,
                fontWeight = FontWeight.Bold,
                fontFamily = defaultFont,
                fontSize = 22.sp
            )
            Text(
                text = "Seller / Client: ${appointment.userName}",
                fontSize = 14.sp,
                fontFamily = defaultFont,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Checklist (Tick verified parts in working condition):",
                fontWeight = FontWeight.Bold,
                fontFamily = defaultFont,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewModel.verifyingParts.forEach { part ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = part.isSelected,
                            onCheckedChange = { isChecked ->
                                viewModel.togglePartVerification(part, isChecked)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2B6CB0))
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = part.name,
                                fontSize = 14.sp,
                                fontFamily = defaultFont,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = String.format("RM %.2f", part.estimatedPrice),
                            fontSize = 14.sp,
                            fontFamily = defaultFont,
                            fontWeight = FontWeight.Bold,
                            color = if (part.isSelected) Color(0xFF2E7D32) else Color.Gray
                        )
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Final Verified Payout:",
                    fontWeight = FontWeight.Bold,
                    fontFamily = defaultFont,
                    fontSize = 16.sp
                )
                Text(
                    text = String.format("RM %.2f", viewModel.totalPayout),
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = defaultFont,
                    fontSize = 24.sp,
                    color = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.completeTransaction() },
                enabled = !viewModel.isSubmitting && !viewModel.doneSubmission,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A365D)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (viewModel.isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        "Transfer Funds & Complete Order",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = defaultFont,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// =============================================================================
// 🔄 横屏视图（左侧清单，右侧结算与按钮）
// =============================================================================
@SuppressLint("DefaultLocale")
@Composable
private fun VerificationLandscapeContent(
    modifier: Modifier = Modifier,
    appointment: Appointment,
    viewModel: ThirdPartyVerificationViewModel
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 左侧：零件清单 Checkbox 滚动列表
        Column(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
        ) {
            Text(
                text = "Checklist (Tick verified parts):",
                fontWeight = FontWeight.Bold,
                fontFamily = defaultFont,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                viewModel.verifyingParts.forEach { part ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(10.dp))
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = part.isSelected,
                            onCheckedChange = { isChecked ->
                                viewModel.togglePartVerification(part, isChecked)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2B6CB0))
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = part.name,
                            fontSize = 13.sp,
                            fontFamily = defaultFont,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = String.format("RM %.2f", part.estimatedPrice),
                            fontSize = 13.sp,
                            fontFamily = defaultFont,
                            fontWeight = FontWeight.Bold,
                            color = if (part.isSelected) Color(0xFF2E7D32) else Color.Gray
                        )
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                }
            }
        }

        // 右侧：设备详情 + 结算总额 + 提交按钮
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = appointment.deviceName,
                        fontWeight = FontWeight.Bold,
                        fontFamily = defaultFont,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Client: ${appointment.userName}",
                        fontSize = 12.sp,
                        fontFamily = defaultFont,
                        color = Color.Gray
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Verified Payout:",
                            fontWeight = FontWeight.Bold,
                            fontFamily = defaultFont,
                            fontSize = 14.sp
                        )
                        Text(
                            text = String.format("RM %.2f", viewModel.totalPayout),
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = defaultFont,
                            fontSize = 20.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }

                    Button(
                        onClick = { viewModel.completeTransaction() },
                        enabled = !viewModel.isSubmitting && !viewModel.doneSubmission,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A365D)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        if (viewModel.isSubmitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                "Transfer Funds",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontFamily = defaultFont,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DoneAnimation() {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(ImageDecoderDecoder.Factory())
        }
        .build()

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(R.drawable.payment_done)
            .crossfade(true)
            .build(),
        imageLoader = imageLoader,
        contentDescription = "Payment Done Animation",
        modifier = Modifier.size(160.dp)
    )
}