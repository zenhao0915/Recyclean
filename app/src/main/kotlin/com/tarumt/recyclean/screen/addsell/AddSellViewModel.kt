package com.tarumt.recyclean.screen.addsell

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.tarumt.recyclean.common.api_key
import com.tarumt.recyclean.common.appState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class AddSellViewModel : ViewModel() {

    var isAnalyzing by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    private val geminiModel = GenerativeModel(modelName = "gemini-3.1-flash-lite", apiKey = api_key)

    private val baseAiPrompt = """
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

    fun analyzeDeviceImage(bitmap: Bitmap) {
        appState.cachedBitmap = bitmap
        errorMessage = ""
        viewModelScope.launch {
            isAnalyzing = true
            appState.showResult = false

            try {
                val response = withContext(Dispatchers.IO) {
                    geminiModel.generateContent(
                        content {
                            image(bitmap)
                            text(baseAiPrompt)
                        })
                }

                val jsonResult = response.text ?: ""
                parseAndSaveJson(jsonResult, "Detected Smart Device")
            } catch (e: Exception) {
                Log.e("GeminiAI", "Error calling API", e)
                errorMessage = "AI Analysis Failed: Please try again."
            } finally {
                isAnalyzing = false
            }
        }
    }

    fun analyzeDeviceText(manualInput: String) {
        errorMessage = ""
        viewModelScope.launch {
            isAnalyzing = true
            appState.showResult = false
            try {
                val response = withContext(Dispatchers.IO) {
                    geminiModel.generateContent(
                        "$baseAiPrompt\n\nUser Inputted Device Model: $manualInput"
                    )
                }

                val jsonResult = response.text ?: ""
                parseAndSaveJson(jsonResult, manualInput)
            } catch (e: Exception) {
                Log.e("GeminiAI", "Error calling API", e)
                errorMessage = "AI Parsing Failed: Check text input or connection."
            } finally {
                isAnalyzing = false
            }
        }
    }

    private fun parseAndSaveJson(jsonResult: String, defaultName: String) {
        Log.d("GeminiAI", "Raw Response: $jsonResult")
        appState.cachedPartList.clear()
        val jsonArray = JSONArray(jsonResult.trim())
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            appState.cachedPartList.add(
                SalvageablePart(
                    name = obj.getString("name"),
                    estimatedPrice = obj.getDouble("price")
                )
            )
        }
        appState.detectedDeviceName = defaultName
        appState.showResult = true
    }
}