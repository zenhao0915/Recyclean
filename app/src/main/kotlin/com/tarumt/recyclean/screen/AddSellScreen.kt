package com.tarumt.recyclean.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.tarumt.recyclean.common.api_key
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.bronzeColor
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.greenCyanColor
import com.tarumt.recyclean.common.lightBlueColor
import com.tarumt.recyclean.common.orangeCreamColor
import com.tarumt.recyclean.common.skyBlueColor
import com.tarumt.recyclean.navigation.AddSellPageDestination
import com.tarumt.recyclean.util.DrawTemplate
import com.tarumt.recyclean.util.GlassBox
import com.tarumt.recyclean.util.data.Sellers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

data class SalvageablePart(
    val name: String, val estimatedPrice: Double, var isSelected: Boolean = false
)

fun String.convertToPart() = appState.apply {
    deviceToSell = this@convertToPart
    navigator.navigateTo(AddSellPageDestination, Offset.Zero)
}

@SuppressLint("DefaultLocale")
@Composable
@Preview
fun AddSellScreen() = DrawTemplate {
    val coroutineScope = rememberCoroutineScope()
    val sellerRowScrollState = rememberScrollState()

    var manualInput by remember { mutableStateOf(appState.deviceToSell ?: "") }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var detectedDeviceName by remember { mutableStateOf("") }
    val partList = remember { mutableStateListOf<SalvageablePart>() }

    var selectedSeller by remember { mutableStateOf(Sellers.SenHeng) }

    val geminiModel = remember {
        GenerativeModel(
            modelName = "gemini-3.1-flash-lite", apiKey = api_key
        )
    }
    val baseAiPrompt = """
        You are an expert in electronics salvage, repair, and e-waste recycling in Malaysia.
        Analyze the provided input (image or text) and identify the device.
        List exactly 3 to 10 valuable, functional salvageable parts/components that can be extracted from this device to be sold to third-party repair shops.
        Estimate a reasonable market recycling value for each part in Malaysian Ringgit (RM).
        
        CRITICAL REQUIREMENT: You must reply ONLY with a valid JSON array. Do NOT wrap it in ```json ... ``` blocks, do NOT write introductory or concluding text.
        Format example:
        [
          {"name": "A15 Bionic Motherboard (Motherboard)", "price": 320.0},
          {"name": "OLED Screen Panel (Screen)", "price": 180.5}
        ]
    """.trimIndent()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            errorMessage = ""
            coroutineScope.launch {
                isAnalyzing = true
                showResult = false

                try {
                    val response = withContext(Dispatchers.IO) {
                        geminiModel.generateContent(
                            content {
                                image(bitmap)
                                text(baseAiPrompt)
                            })
                    }

                    val jsonResult = response.text ?: ""
                    Log.d("GeminiAI", "Raw Response: $jsonResult")

                    partList.clear()
                    val jsonArray = JSONArray(jsonResult.trim())
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        partList.add(
                            SalvageablePart(
                                name = obj.getString("name"),
                                estimatedPrice = obj.getDouble("price")
                            )
                        )
                    }
                    detectedDeviceName = "Detected Smart Device"
                    showResult = true
                } catch (e: Exception) {
                    Log.e("GeminiAI", "Error calling API", e)
                    errorMessage = "AI Analysis Failed: Please try again."
                } finally {
                    isAnalyzing = false
                }
            }
        }
    }

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
                GlassBox {
                    Button(
                        onClick = { cameraLauncher.launch() }, shape = RoundedCornerShape(12.dp)
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
                    fontSize = 14.sp,
                    color = Color.LightGray.copy(alpha = 0.75f)
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
                    errorMessage = ""
                    coroutineScope.launch {
                        isAnalyzing = true
                        showResult = false
                        try {
                            val response = withContext(Dispatchers.IO) {
                                geminiModel.generateContent(
                                    "$baseAiPrompt\n\nUser Inputted Device Model: $manualInput"
                                )
                            }

                            val jsonResult = response.text ?: ""
                            Log.d("GeminiAI", "Raw Response: $jsonResult")

                            partList.clear()
                            val jsonArray = JSONArray(jsonResult.trim())
                            for (i in 0 until jsonArray.length()) {
                                val obj = jsonArray.getJSONObject(i)
                                partList.add(
                                    SalvageablePart(
                                        name = obj.getString("name"),
                                        estimatedPrice = obj.getDouble("price")
                                    )
                                )
                            }
                            detectedDeviceName = manualInput
                            showResult = true
                        } catch (e: Exception) {
                            Log.e("GeminiAI", "Error calling API", e)
                            errorMessage = "AI Parsing Failed: Check text input or connection."
                        } finally {
                            isAnalyzing = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Analyze Text with AI", fontFamily = defaultFont)
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, fontSize = 13.sp, fontFamily = defaultFont)
        }

        AnimatedVisibility(visible = isAnalyzing) {
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

        AnimatedVisibility(visible = showResult) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(0.5.dp, Color.LightGray, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "📦 Identified: $detectedDeviceName",
                    fontFamily = defaultFont,
                    fontSize = 16.sp,
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
                                }, colors = CheckboxDefaults.colors(checkedColor = bronzeColor)
                            )
                            Text(
                                modifier = Modifier.widthIn(max = 160.dp),
                                text = part.name,
                                fontFamily = defaultFont,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Start
                            )
                        }
                        Text(
                            text = "RM ${String.format("%.2f", part.estimatedPrice)}",
                            textAlign = TextAlign.Center,
                            fontFamily = defaultFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
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
                        val isSelected = selectedSeller == seller
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    color = if (isSelected) lightBlueColor else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedSeller = seller }
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = seller.sellerName,
                                fontFamily = defaultFont,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

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
                        color = orangeCreamColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        // 1. Filter out only the parts the user actually checked
                        val selectedParts = partList.filter { it.isSelected }

                        if (selectedParts.isNotEmpty()) {
                            // 2. Create a new Appointment object
                            val newAppointment = Appointment(
                                appointmentId = "APT-${System.currentTimeMillis().toString().takeLast(4)}", // Generates a random ID like APT-5832
                                userName = "Current User", // You can replace this with actual user profile data later
                                deviceName = detectedDeviceName,
                                scheduledDate = "Pending Date",
                                estimatedValue = totalPrice,
                                status = AppointmentStatus.PENDING,
                                selectedParts = selectedParts,
                                targetSeller = selectedSeller.sellerName
                            )

                            // 3. Save it to our global state
                            appState.pendingAppointments.add(newAppointment)

                            // 4. (Optional) Navigate the user to a success screen or clear the form
                            // appState.navigator.navigateTo(SuccessScreenDestination)

                            // Reset for the next demo scan
                            capturedBitmap = null
                            manualInput = ""
                            showResult = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = orangeCreamColor),
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
    }
}