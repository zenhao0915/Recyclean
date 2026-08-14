package com.tarumt.recyclean.screen.meeting

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.navigation.MeetingPageDestination
import com.tarumt.recyclean.util.data.Appointment

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DefaultLocale")
@Composable
fun ThirdPartyVerificationScreen(
    appointment: Appointment,
    viewModel: ThirdPartyVerificationViewModel = viewModel()
) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
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

            // 底部结算与确认
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
                    enabled = !viewModel.isSubmitting,
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
}