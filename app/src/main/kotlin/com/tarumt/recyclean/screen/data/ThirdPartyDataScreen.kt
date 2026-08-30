package com.tarumt.recyclean.screen.data

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.tarumt.recyclean.common.skyBlueColor
import com.tarumt.recyclean.util.DrawTemplate
import com.tarumt.recyclean.util.data.Appointment

@Composable
@Preview
fun ThirdPartyDataScreen(
    viewModel: ThirdPartyDataViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(Unit) {
        viewModel.fetchMerchantData()
    }

    val transactions = viewModel.purchasedTransactions
    val chartPoints = viewModel.chartPoints

    if (isLandscape) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            contentAlignment = Alignment.Center
        ) {
            ThirdPartyDataLandscapeContent(
                viewModel = viewModel,
                transactions = transactions,
                chartPoints = chartPoints
            )
        }
    } else {
        ThirdPartyDataPortraitContent(
            viewModel = viewModel,
            transactions = transactions,
            chartPoints = chartPoints
        )
    }
}

@Composable
private fun ThirdPartyDataPortraitContent(
    viewModel: ThirdPartyDataViewModel,
    transactions: List<Appointment>,
    chartPoints: List<MerchantChartPoint>
) = DrawTemplate {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Procurement Analytics",
            fontFamily = defaultBoldFont,
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

        MerchantProcurementCard(
            totalSpend = viewModel.totalProcurementCost,
            devicesCount = viewModel.totalDevicesPurchased,
            partsCount = viewModel.totalPartsAcquired
        )

        if (chartPoints.isNotEmpty()) {
            ProcurementTrendChartCard(points = chartPoints)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Completed Purchase Orders (${transactions.size})",
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

@Composable
private fun ThirdPartyDataLandscapeContent(
    viewModel: ThirdPartyDataViewModel,
    transactions: List<Appointment>,
    chartPoints: List<MerchantChartPoint>
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Procurement Analytics",
                fontFamily = defaultBoldFont,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            MerchantProcurementCard(
                totalSpend = viewModel.totalProcurementCost,
                devicesCount = viewModel.totalDevicesPurchased,
                partsCount = viewModel.totalPartsAcquired,
                isLandscape = true
            )

            if (chartPoints.isNotEmpty()) {
                ProcurementTrendChartCard(points = chartPoints, isLandscape = true)
            }
        }

        Column(
            modifier = Modifier
                .weight(1.2f)
                .fillMaxHeight()
        ) {
            Text(
                text = "Completed Orders (${transactions.size})",
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
                        text = "No completed purchase orders found.",
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
                        MerchantTransactionCard(appointment = item)
                    }
                }
            }
        }
    }
}

/**
 * 🌟 逐单交易趋势曲线图表卡片
 */
@SuppressLint("DefaultLocale")
@Composable
fun ProcurementTrendChartCard(
    points: List<MerchantChartPoint>,
    isLandscape: Boolean = false
) {
    val displayPoints = points.takeLast(6)
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
                .padding(if (isLandscape) 12.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        text = "Transactions Done",
                        fontFamily = defaultBoldFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }

                Text(
                    text = "${points.size} Orders",
                    fontFamily = defaultFont,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            val maxSpend = (displayPoints.maxOfOrNull { it.totalSpend } ?: 1.0).coerceAtLeast(100.0)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isLandscape) 120.dp else 145.dp)
                    .padding(top = 8.dp, bottom = 4.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val spacing = if (displayPoints.size > 1) width / (displayPoints.size - 1) else width

                    val gridSteps = 3
                    for (i in 0..gridSteps) {
                        val y = height * (i.toFloat() / gridSteps)
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.35f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    if (displayPoints.size == 1) {
                        val pointY =
                            height - (displayPoints[0].totalSpend.toFloat() / maxSpend.toFloat() * (height * 0.78f))
                        drawCircle(
                            color = Color(0xFF2B6CB0),
                            radius = 6.dp.toPx(),
                            center = Offset(width / 2, pointY)
                        )
                        return@Canvas
                    }

                    val coordinates = displayPoints.mapIndexed { index, item ->
                        val x = index * spacing
                        val y =
                            height - (item.totalSpend.toFloat() / maxSpend.toFloat() * (height * 0.78f))
                        Offset(x, y)
                    }

                    val strokePath = Path().apply {
                        moveTo(coordinates.first().x, coordinates.first().y)
                        for (i in 0 until coordinates.size - 1) {
                            val p0 = coordinates[i]
                            val p1 = coordinates[i + 1]
                            val controlX = (p0.x + p1.x) / 2f
                            cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
                        }
                    }

                    val fillPath = Path().apply {
                        addPath(strokePath)
                        lineTo(coordinates.last().x, height)
                        lineTo(coordinates.first().x, height)
                        close()
                    }

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

                    drawPath(
                        path = strokePath,
                        color = Color(0xFF2B6CB0),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                displayPoints.forEach { point ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        // 1. 订单编号
                        Text(
                            text = "#${point.orderIndex}",
                            fontFamily = defaultBoldFont,
                            fontSize = 11.sp,
                            color = Color(0xFF1A365D),
                            fontWeight = FontWeight.Bold
                        )

                        // 2. 真实交易日期
                        Text(
                            text = point.dateLabel,
                            fontFamily = defaultFont,
                            fontSize = 9.sp,
                            color = Color(0xFF2B6CB0),
                            fontWeight = FontWeight.SemiBold
                        )

                        // 3. 交易金额
                        Text(
                            text = String.format("RM %.0f", point.totalSpend),
                            fontFamily = defaultFont,
                            fontSize = 9.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun MerchantProcurementCard(
    totalSpend: Double,
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
                        colors = listOf(Color(0xFF1A365D), Color(0xFF2B6CB0))
                    )
                )
                .padding(if (isLandscape) 14.dp else 20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(if (isLandscape) 10.dp else 16.dp)) {
                Column {
                    Text(
                        text = "Total Procurement Spend",
                        fontFamily = defaultFont,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = String.format("RM %.2f", totalSpend),
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

/**
 * 🌟 支持展开/收起的商家采购单卡片
 */
@SuppressLint("DefaultLocale")
@Composable
fun MerchantTransactionCard(appointment: Appointment) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.deviceName.ifBlank { "Unknown Device" },
                    fontFamily = defaultBoldFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "COMPLETED",
                                fontSize = 9.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Client",
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(5.dp))
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
                    modifier = Modifier.size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Acquisition Date: ${appointment.scheduledDate}",
                    fontFamily = defaultFont,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.35f))

                    Text(
                        text = "Acquired Parts Breakdown:",
                        fontFamily = defaultFont,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )

                    appointment.selectedParts.forEach { part ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
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
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Procurement Cost",
                        fontFamily = defaultFont,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }

                Text(
                    text = String.format("RM %.2f", appointment.estimatedValue),
                    fontFamily = defaultBoldFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A365D)
                )
            }
        }
    }
}