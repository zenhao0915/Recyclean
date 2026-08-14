package com.tarumt.recyclean.screen.data

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Payments
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.skyBlueColor
import com.tarumt.recyclean.util.DrawTemplate
import com.tarumt.recyclean.util.data.Appointment

@Composable
@Preview
fun ThirdPartyDataScreen(
    viewModel: ThirdPartyDataViewModel = viewModel()
) = DrawTemplate {
    LaunchedEffect(Unit) {
        viewModel.fetchMerchantData()
    }

    val transactions = viewModel.purchasedTransactions
    val chartPoints = viewModel.chartPoints

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 头部标题
        Text(
            text = "Procurement Analytics",
            fontFamily = defaultFont,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = "Overview of acquired devices, harvested salvage parts, and inventory expenses.",
            fontFamily = defaultFont,
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        // 🌟 1. 商家采购概览卡片
        MerchantProcurementCard(
            totalSpend = viewModel.totalProcurementCost,
            devicesCount = viewModel.totalDevicesPurchased,
            partsCount = viewModel.totalPartsAcquired
        )

        // 🌟 2. 交易量与支出趋势图表卡片 (Plotting Chart)
        if (chartPoints.isNotEmpty()) {
            ProcurementTrendChartCard(points = chartPoints)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 列表标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Completed Purchase Orders (${transactions.size})",
                fontFamily = defaultFont,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        AnimatedVisibility(visible = viewModel.isLoading) {
            CircularProgressIndicator(color = skyBlueColor, modifier = Modifier.padding(16.dp))
        }

        // 🌟 3. 采购历史订单列表
        if (transactions.isEmpty() && !viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No completed purchase orders found for this merchant.",
                    fontFamily = defaultFont,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        } else {
            transactions.forEach { item ->
                MerchantTransactionCard(appointment = item)
            }
        }
    }
}

/**
 * 🌟 交易趋势曲线图表卡片 (Canvas Plotting)
 */
@SuppressLint("DefaultLocale")
@Composable
fun ProcurementTrendChartCard(points: List<MerchantChartPoint>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoGraph,
                        contentDescription = "Trend",
                        tint = Color(0xFF2B6CB0),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Procurement Spending Trend",
                        fontFamily = defaultFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }

            // 🌟 Canvas 曲线与面积绘制
            val maxSpend = (points.maxOfOrNull { it.totalSpend } ?: 1.0).coerceAtLeast(100.0)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(vertical = 8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val spacing = if (points.size > 1) width / (points.size - 1) else width

                    // 绘制 3 条水平参考线
                    val gridSteps = 3
                    for (i in 0..gridSteps) {
                        val y = height * (i.toFloat() / gridSteps)
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.4f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    if (points.size == 1) {
                        // 单点展示
                        val pointY = height - (points[0].totalSpend.toFloat() / maxSpend.toFloat() * height)
                        drawCircle(
                            color = Color(0xFF2B6CB0),
                            radius = 6.dp.toPx(),
                            center = Offset(width / 2, pointY)
                        )
                        return@Canvas
                    }

                    // 计算所有点的坐标
                    val coordinates = points.mapIndexed { index, item ->
                        val x = index * spacing
                        val y = height - (item.totalSpend.toFloat() / maxSpend.toFloat() * (height * 0.85f))
                        Offset(x, y)
                    }

                    // 构建贝塞尔平滑路径
                    val strokePath = Path().apply {
                        moveTo(coordinates.first().x, coordinates.first().y)
                        for (i in 0 until coordinates.size - 1) {
                            val p0 = coordinates[i]
                            val p1 = coordinates[i + 1]
                            val controlX = (p0.x + p1.x) / 2f
                            cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                        }
                    }

                    // 渐变填充闭合路径
                    val fillPath = Path().apply {
                        addPath(strokePath)
                        lineTo(coordinates.last().x, height)
                        lineTo(coordinates.first().x, height)
                        close()
                    }

                    // 1. 绘制面积渐变
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2B6CB0).copy(alpha = 0.35f),
                                Color(0xFF2B6CB0).copy(alpha = 0.0f)
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // 2. 绘制平滑主线
                    drawPath(
                        path = strokePath,
                        color = Color(0xFF2B6CB0),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 3. 绘制节点圆圈
                    coordinates.forEach { offset ->
                        drawCircle(
                            color = Color.White,
                            radius = 5.dp.toPx(),
                            center = offset
                        )
                        drawCircle(
                            color = Color(0xFF1A365D),
                            radius = 3.dp.toPx(),
                            center = offset
                        )
                    }
                }
            }

            // X 轴时间标签
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                points.forEach { point ->
                    Text(
                        text = point.dateLabel,
                        fontFamily = defaultFont,
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * 🌟 商家采购统计看板卡片
 */
@SuppressLint("DefaultLocale")
@Composable
fun MerchantProcurementCard(
    totalSpend: Double,
    devicesCount: Int,
    partsCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF1A365D), Color(0xFF2B6CB0))
                    )
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text(
                        text = "Total Procurement Spend",
                        fontFamily = defaultFont,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = String.format("RM %.2f", totalSpend),
                        fontFamily = defaultFont,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.3f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MerchantStatItem(
                        icon = Icons.Default.Devices,
                        label = "Devices Acquired",
                        value = "$devicesCount Units"
                    )

                    MerchantStatItem(
                        icon = Icons.Default.Extension,
                        label = "Parts Harvested",
                        value = "$partsCount Items"
                    )
                }
            }
        }
    }
}

@Composable
fun MerchantStatItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                fontFamily = defaultFont,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = value,
                fontFamily = defaultFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * 🌟 商家采购单卡片
 */
@SuppressLint("DefaultLocale")
@Composable
fun MerchantTransactionCard(appointment: Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 头部：设备名称 + 交易状态
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.deviceName.ifBlank { "Unknown Device" },
                    fontFamily = defaultFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "COMPLETED",
                            fontSize = 10.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 客户账号与交易日期
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Client",
                    modifier = Modifier.size(15.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Seller / Client: ${appointment.userName.ifBlank { "Anonymous" }}",
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
                    text = "Acquisition Date: ${appointment.scheduledDate}",
                    fontFamily = defaultFont,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))

            // 回收零件明细
            Text(
                text = "Acquired Parts Breakdown:",
                fontFamily = defaultFont,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )

            appointment.selectedParts.forEach { part ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• ${part.name}",
                        fontFamily = defaultFont,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = String.format("RM %.2f", part.estimatedPrice),
                        fontFamily = defaultFont,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))

            // 底部：采购支出结算
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = "Cost",
                        tint = Color(0xFF2B6CB0),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Procurement Cost",
                        fontFamily = defaultFont,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }

                Text(
                    text = String.format("RM %.2f", appointment.estimatedValue),
                    fontFamily = defaultFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color(0xFF1A365D)
                )
            }
        }
    }
}