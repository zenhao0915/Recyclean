package com.tarumt.recyclean.screen.meeting

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.recyclean.util.data.Appointment
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.navigation.MeetingPageDestination
import androidx.compose.ui.geometry.Offset

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun ThirdPartyVerificationScreen(
    appointment: Appointment,
    viewModel: ThirdPartyVerificationViewModel = viewModel()
) {
    // Load the data into the ViewModel as soon as the screen opens
    LaunchedEffect(appointment) {
        viewModel.loadAppointment(appointment)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify Device Parts", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { appState.navigator.navigateTo(MeetingPageDestination, Offset.Zero) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF466EF2),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Info & Checklist
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.deviceName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    text = "Seller: ${appointment.userName}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Checklist (Tick received parts in good condition):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Checklist
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
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF466EF2))
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = part.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Text(
                                text = String.format("RM %.2f", part.estimatedPrice),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (part.isSelected) Color(0xFF2E7D32) else Color.Gray
                            )
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            }

            // Bottom Section: Total & Transfer Button
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
                    Text(text = "Final Transaction Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = String.format("RM %.2f", viewModel.totalPayout),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = Color(0xFF2E7D32)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.completeTransaction() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF466EF2)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Transfer Funds to Seller", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}