package com.tarumt.recyclean.screen.data

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.greenCyanColor
import com.tarumt.recyclean.common.skyBlueColor
import com.tarumt.recyclean.util.DrawTemplate
import com.tarumt.recyclean.util.data.Appointment

@Composable
@Preview
fun DefaultDataScreen(
    viewModel: DefaultDataViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(Unit) {
        viewModel.fetchTransactionHistory()
    }

    val transactions = viewModel.completedTransactions

    if (isLandscape) {
        // 🔄 Landscape 双列布局 (左侧：收益看板与数据概览，右侧：交易历史明细列表)
        DefaultDataLandscape(
            viewModel = viewModel,
            transactions = transactions
        )
    } else {
        // 📱 Portrait 经典单列滚动布局
        DefaultDataPortrait(
            viewModel = viewModel,
            transactions = transactions
        )
    }
}

// =============================================================================
// 📱 1. Portrait 视图
// =============================================================================
@Composable
private fun DefaultDataPortrait(
    viewModel: DefaultDataViewModel,
    transactions: List<Appointment>
) = DrawTemplate {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Recycling Impact & History",
            fontFamily = defaultBoldFont,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Your contribution to e-waste recycling and total earnings history.",
            fontFamily = defaultFont,
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        // 顶部环保收益数据看板
        ImpactDashboardCard(
            totalEarnings = viewModel.totalEarnings,
            devicesCount = viewModel.totalDevicesCount,
            partsCount = viewModel.totalPartsSavedCount
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Completed Transactions (${transactions.size})",
                fontFamily = defaultBoldFont,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        AnimatedVisibility(visible = viewModel.isLoading) {
            CircularProgressIndicator(color = skyBlueColor, modifier = Modifier.padding(16.dp))
        }

        if (transactions.isEmpty() && !viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No completed transactions yet.\nCompleted orders will show up here!",
                    fontFamily = defaultFont,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        } else {
            transactions.forEach { item ->
                TransactionCard(appointment = item)
            }
        }
    }
}

// =============================================================================
// 🔄 2. Landscape 视图 (左右分栏，兼容左侧导航栏)
// =============================================================================
@Composable
private fun DefaultDataLandscape(
    viewModel: DefaultDataViewModel,
    transactions: List<Appointment>
) = Box(modifier = Modifier.fillMaxSize().background(color = Color.White), contentAlignment = Alignment.Center) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 左列：标题 + 概览卡片
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Recycling Impact",
                fontFamily = defaultBoldFont,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Track your recycling earnings and salvage contributions.",
                fontFamily = defaultFont,
                fontSize = 12.sp,
                color = Color.Gray
            )

            ImpactDashboardCard(
                totalEarnings = viewModel.totalEarnings,
                devicesCount = viewModel.totalDevicesCount,
                partsCount = viewModel.totalPartsSavedCount,
                isLandscape = true
            )
        }

        // 右列：已完成订单明细 LazyColumn
        Column(
            modifier = Modifier
                .weight(1.15f)
                .fillMaxHeight()
        ) {
            Text(
                text = "Completed Transactions (${transactions.size})",
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
            } else if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No completed transactions yet.",
                        fontFamily = defaultFont,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions, key = { it.appointmentId }) { item ->
                        TransactionCard(appointment = item)
                    }
                }
            }
        }
    }
}

/**
 * 🌟 顶部环保成就与统计看板卡片
 */
@SuppressLint("DefaultLocale")
@Composable
fun ImpactDashboardCard(
    totalEarnings: Double,
    devicesCount: Int,
    partsCount: Int,
    isLandscape: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF2E7D32), greenCyanColor)
                    )
                )
                .padding(if (isLandscape) 14.dp else 20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(if (isLandscape) 10.dp else 16.dp)
            ) {
                Column {
                    Text(
                        text = "Total Cash Earned",
                        fontFamily = defaultFont,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = String.format("RM %.2f", totalEarnings),
                        fontFamily = defaultBoldFont,
                        fontSize = if (isLandscape) 24.sp else 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.3f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatMetricItem(
                        icon = Icons.Default.Devices,
                        label = "Devices Recycled",
                        value = "$devicesCount Units"
                    )

                    StatMetricItem(
                        icon = Icons.Default.Extension,
                        label = "Parts Salvaged",
                        value = "$partsCount Items"
                    )
                }
            }
        }
    }
}

@Composable
fun StatMetricItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = label,
                fontFamily = defaultFont,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = value,
                fontFamily = defaultBoldFont,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}