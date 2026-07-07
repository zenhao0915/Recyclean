package com.tarumt.recyclean.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.greenCyanColor
import com.tarumt.recyclean.util.DrawTemplate
import com.tarumt.recyclean.util.GlassBox
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

// 模拟零部件的数据结构
data class SalvageablePart(
    val name: String,
    val estimatedPrice: Double,
    var isSelected: Boolean = true
)

@SuppressLint("DefaultLocale")
@Composable
@Preview
fun AddSellScreen() = DrawTemplate {
    val coroutineScope = rememberCoroutineScope()

    var manualInput by remember { mutableStateOf("") }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }

    var detectedDeviceName by remember { mutableStateOf("iPhone 13 Pro (Detected by AI)") }
    val partList = remember { mutableStateListOf<SalvageablePart>() }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            coroutineScope.launch {
                isAnalyzing = true
                showResult = false
                delay(2500.milliseconds)
                isAnalyzing = false

                // 模拟 AI 拆解出来的电子元件数据
                partList.clear()
                partList.add(SalvageablePart("A15 Bionic Motherboard (主板)", 350.0))
                partList.add(SalvageablePart("OLED Super Retina Screen (屏幕)", 220.0))
                partList.add(SalvageablePart("Triple Camera Module (三摄镜头)", 180.0))
                partList.add(SalvageablePart("Original Li-ion Battery (原装锂电池)", 45.0))
                showResult = true
            }
        }
    }

    // 主体滚动布局
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
            capturedBitmap?.let { cbm ->
                Image(
                    bitmap = cbm.asImageBitmap(),
                    contentDescription = "Captured Device",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { capturedBitmap = null; showResult = false },
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
                GlassBox() {
                    Button(
                        onClick = { cameraLauncher.launch() },
                        shape = RoundedCornerShape(12.dp)
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
            onValueChange = { manualInput = it },
            placeholder = {
                Text(
                    text = "PlayStation 5 / iPad Pro",
                    fontSize = 14.sp
                )
            },
            label = { Text(text = "Manual Input Device Model") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = greenCyanColor, focusedLabelColor = greenCyanColor
            )
        )

        if (manualInput.isNotEmpty() && capturedBitmap == null) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        isAnalyzing = true
                        delay(2000.milliseconds)
                        isAnalyzing = false
                        detectedDeviceName = manualInput
                        partList.clear()
                        partList.add(SalvageablePart("Main Logic Board (核心电路板)", 280.0))
                        partList.add(SalvageablePart("Power Supply Unit (电源模块)", 90.0))
                        partList.add(SalvageablePart("Cooling Fan & Heatsink (散热系统)", 40.0))
                        showResult = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Analyze Text with AI", fontFamily = defaultFont)
            }
        }

        AnimatedVisibility(visible = isAnalyzing) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                CircularProgressIndicator(color = greenCyanColor)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AI is evaluating components...",
                    fontFamily = defaultFont,
                    fontSize = 14.sp,
                    color = greenCyanColor
                )
            }
        }

        AnimatedVisibility(visible = showResult) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(0.5.dp, Color.LightGray, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 识别出来的设备头部
                Text(
                    text = "📦 Identified: $detectedDeviceName",
                    fontFamily = defaultFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF3B30) // 你的标志性苹果红高亮
                )

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Text(
                    text = "Select parts you wish to sell:",
                    fontFamily = defaultFont,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                // 循环渲染组件列表
                partList.forEach { part ->
                    var checked by remember { mutableStateOf(part.isSelected) }
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
                                }, colors = CheckboxDefaults.colors(checkedColor = greenCyanColor)
                            )
                            Text(text = part.name, fontFamily = defaultFont, fontSize = 14.sp)
                        }
                        Text(
                            text = "RM ${String.format("%.2f", part.estimatedPrice)}",
                            fontFamily = defaultFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

                // 总估价计算
                val totalPrice = partList.filter { it.isSelected }.sumOf { it.estimatedPrice }
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
                        color = greenCyanColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 最终提交按钮
                Button(
                    onClick = { /* 提交到数据库，通知持牌商家竞价 */ },
                    colors = ButtonDefaults.buttonColors(containerColor = greenCyanColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Publish to Auction Pool (发布竞价)",
                        fontFamily = defaultFont,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp)) // 给底部的浮动 Navigator 留出空位
    }
}