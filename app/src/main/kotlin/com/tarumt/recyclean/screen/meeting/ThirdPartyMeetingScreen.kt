package com.tarumt.recyclean.screen.meeting

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.util.data.Appointment

@Preview(showBackground = true, name = "Appointment List Preview")
@Composable
fun ThirdPartyMeetingScreenPreview() {
    MaterialTheme {
        ThirdPartyMeetingScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdPartyMeetingScreen(
    viewModel: ThirdPartyMeetingViewModel = viewModel(),
    appointments: List<Appointment> = appState.pendingAppointments,
    onAppointmentClick: (String) -> Unit = { viewModel.openApprovalDialog(it) }
) {
    // 进入界面时刷新当前商家的订单
    LaunchedEffect(Unit) {
        viewModel.fetchInitialAppointments()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${viewModel.currentMerchantName} Appointments",
                        fontWeight = FontWeight.Bold,
                        fontFamily = defaultFont
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A365D),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF2B6CB0)
                )
            } else if (appointments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No pending appointments for ${viewModel.currentMerchantName}.\nNew incoming recycle requests will appear here!",
                        fontFamily = defaultFont,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(appointments) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onClick = { onAppointmentClick(appointment.appointmentId) }
                        )
                    }
                }
            }
        }
    }

    // --- POP-UP SCREEN UI ---
    viewModel.selectedAppointment?.let { appt ->
        AlertDialog(
            onDismissRequest = { viewModel.closeApprovalDialog() },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "Appointment Approval",
                    fontFamily = defaultFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier.verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Device: ${appt.deviceName}",
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = defaultFont
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    Text(
                        text = "Spare Parts Requested:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = defaultFont
                    )

                    appt.selectedParts.forEach { part ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "- ${part.name}",
                                fontSize = 13.sp,
                                fontFamily = defaultFont,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 12.dp)
                            )
                            Text(
                                text = String.format("RM %.2f", part.estimatedPrice),
                                fontSize = 13.sp,
                                fontFamily = defaultFont,
                                maxLines = 1
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estimated Payout:",
                            fontWeight = FontWeight.Bold,
                            fontFamily = defaultFont
                        )
                        Text(
                            text = String.format("RM %.2f", appt.estimatedValue),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            fontFamily = defaultFont,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.approveAppointment(appt) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B6CB0)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "Verify & Accept",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = defaultFont
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.rejectAppointment(appt) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Reject", fontWeight = FontWeight.Bold, fontFamily = defaultFont)
                }
            }
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun AppointmentCard(appointment: Appointment, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.deviceName,
                    fontWeight = FontWeight.Bold,
                    fontFamily = defaultFont,
                    fontSize = 17.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Seller / Client: ${appointment.userName}",
                    fontSize = 13.sp,
                    fontFamily = defaultFont,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        modifier = Modifier.size(15.dp),
                        tint = Color(0xFF2B6CB0)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = appointment.scheduledDate,
                        fontSize = 12.sp,
                        fontFamily = defaultFont,
                        color = Color(0xFF2B6CB0)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = String.format("RM %.2f", appointment.estimatedValue),
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = defaultFont,
                    fontSize = 16.sp,
                    color = Color(0xFF1A365D)
                )
                Spacer(modifier = Modifier.height(6.dp))
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
                Spacer(modifier = Modifier.height(6.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View Details",
                    tint = Color.Gray
                )
            }
        }
    }
}