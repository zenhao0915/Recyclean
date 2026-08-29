package com.tarumt.recyclean.screen.meeting

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.orangeCreamColor
import com.tarumt.recyclean.common.skyBlueColor
import com.tarumt.recyclean.util.DrawTemplate
import com.tarumt.recyclean.util.data.Appointment
import com.tarumt.recyclean.util.data.AppointmentStatus

@Composable
@Preview
fun DefaultMeetingScreen(
    viewModel: DefaultMeetingViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(Unit) {
        viewModel.fetchUserAppointments()
    }

    val appointments = appState.pendingAppointments
    val pendingList = appointments.filter { it.status == AppointmentStatus.PENDING }

    if (isLandscape) {
        // 🔄 Landscape 双列布局 (左侧：说明与概览，右侧：预约卡片列表)
        MeetingLandscapeContent(
            viewModel = viewModel,
            pendingAppointments = pendingList
        )
    } else {
        // 📱 Portrait 经典单列滚动布局
        MeetingPortraitContent(
            viewModel = viewModel,
            pendingAppointments = pendingList
        )
    }

    // 取消预约确认弹窗
    viewModel.selectedAppointmentForCancel?.let { appt ->
        AlertDialog(
            onDismissRequest = { viewModel.closeCancelDialog() },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "Cancel Appointment?",
                    fontFamily = defaultBoldFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to cancel the recycling request for '${appt.deviceName}' assigned to '${appt.targetSeller}'?",
                    fontFamily = defaultFont,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmCancelAppointment(appt) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Confirm Cancel", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.closeCancelDialog() },
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Keep It", color = Color.Gray)
                }
            }
        )
    }
}

// =============================================================================
// 📱 1. Portrait 视图
// =============================================================================
@Composable
private fun MeetingPortraitContent(
    viewModel: DefaultMeetingViewModel,
    pendingAppointments: List<Appointment>
) = DrawTemplate {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "My Meetings",
            fontFamily = defaultBoldFont,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Track your submitted recycling requests or cancel pending appointments.",
            fontFamily = defaultFont,
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        )

        AnimatedVisibility(visible = viewModel.isLoading) {
            CircularProgressIndicator(color = skyBlueColor, modifier = Modifier.padding(16.dp))
        }

        if (pendingAppointments.isEmpty() && !viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No active appointments found.\nGo to Recycle page to assign items!",
                    fontFamily = defaultBoldFont,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        } else {
            pendingAppointments.forEach { appointment ->
                UserAppointmentCard(
                    appointment = appointment,
                    onCancelClick = { viewModel.openCancelDialog(appointment) }
                )
            }
        }
    }
}

// =============================================================================
// 🔄 2. Landscape 视图 (左右分栏，适配左侧 Navigator)
// =============================================================================
@Composable
private fun MeetingLandscapeContent(
    viewModel: DefaultMeetingViewModel,
    pendingAppointments: List<Appointment>
) = Box(modifier = Modifier.fillMaxSize().background(color = Color.White), contentAlignment = Alignment.Center) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 左列：说明标题与状态统计
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "My Meetings",
                fontFamily = defaultBoldFont,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Track your submitted recycling requests or cancel pending appointments.",
                fontFamily = defaultFont,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Pending Requests",
                        fontFamily = defaultFont,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${pendingAppointments.size} Active",
                        fontFamily = defaultBoldFont,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = orangeCreamColor
                    )
                    Text(
                        text = "Click cards on the right to inspect harvested parts breakdown.",
                        fontFamily = defaultFont,
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // 右列：预约卡片列表
        Column(
            modifier = Modifier
                .weight(1.25f)
                .fillMaxHeight()
        ) {
            Text(
                text = "Active Requests (${pendingAppointments.size})",
                fontFamily = defaultBoldFont,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = skyBlueColor, modifier = Modifier.size(28.dp))
                }
            } else if (pendingAppointments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active appointments found.\nGo to Recycle page to assign items!",
                        fontFamily = defaultBoldFont,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pendingAppointments, key = { it.appointmentId }) { appointment ->
                        UserAppointmentCard(
                            appointment = appointment,
                            onCancelClick = { viewModel.openCancelDialog(appointment) }
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// 📦 3. 支持折叠/展开的预约卡片 (带平滑展开动画)
// =============================================================================
@SuppressLint("DefaultLocale")
@Composable
fun UserAppointmentCard(
    appointment: Appointment,
    onCancelClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "ArrowRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isExpanded = !isExpanded
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 头部：设备名称 + 状态 Label + 展开/折叠箭头
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.deviceName.ifBlank { "Unknown Device" },
                    fontFamily = defaultBoldFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFFFF3E0),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = appointment.status.name,
                            fontSize = 10.sp,
                            color = Color(0xFFE65100),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(arrowRotation)
                    )
                }
            }

            // 商家信息与日期
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = "Seller",
                    modifier = Modifier.size(15.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Target Seller: ${appointment.targetSeller.ifBlank { "Unassigned" }}",
                    fontFamily = defaultFont,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date",
                    modifier = Modifier.size(15.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = appointment.scheduledDate,
                    fontFamily = defaultFont,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // 🌟 核心：仅在展开时显示零件拆解清单
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.35f))

                    val parts = appointment.selectedParts
                    Text(
                        text = "Selected Parts (${parts.size}):",
                        fontFamily = defaultFont,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )

                    parts.forEach { part ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• ${part.name}",
                                fontFamily = defaultFont,
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = String.format("RM %.2f", part.estimatedPrice),
                                fontFamily = defaultFont,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.35f))

            // 底部：总计估值 + 取消按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Est. Total Payout",
                        fontFamily = defaultFont,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = String.format("RM %.2f", appointment.estimatedValue),
                        fontFamily = defaultBoldFont,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = orangeCreamColor
                    )
                }

                OutlinedButton(
                    onClick = onCancelClick,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Cancel",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Cancel",
                        fontFamily = defaultBoldFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}