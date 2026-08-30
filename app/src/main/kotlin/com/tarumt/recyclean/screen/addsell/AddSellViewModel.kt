package com.tarumt.recyclean.screen.addsell

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.tarumt.recyclean.common.api_key
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.util.data.UserProfileDto
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class AddSellViewModel : ViewModel() {

    var isAnalyzing by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    private val candidateModels = listOf(
        "gemini-3.5-flash-lite",
        "gemini-3.6-flash",
        "gemini-3.7-flash",
        "gemini-3.1-flash-lite"
    )

    private fun createModel(modelName: String): GenerativeModel {
        return GenerativeModel(
            modelName = modelName,
            apiKey = api_key,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                temperature = 0.1f
            }
        )
    }

    private val baseAiPrompt = """
        Identify device from input. Return 2-10 valuable salvageable parts with Malaysian market recycling prices in MYR (RM), sorted by value desc.
        Format: JSON array only. Each item has "name" (with category in parentheses, e.g. "OLED Panel (Screen)") and "estimatedPrice" (numeric).
        Return [] if non-electronic.
    """.trimIndent()

    private suspend fun checkIsUserBlacklisted(): Boolean {
        if (appState.isDebuggerMode) return false

        val currentEmail = appState.currentUser?.userNameWithEmail ?: return false

        return try {
            val profile = appState.supabase.from("users").select {
                filter { eq("email", currentEmail) }
            }.decodeSingle<UserProfileDto>()

            if (profile.isBlacklisted == true) {
                val reason = profile.blacklistReason?.ifBlank { "Violation of platform policies" }
                    ?: "Violation of platform policies"
                NotificationManager.addToast(
                    "Action Denied: Your account is blacklisted ($reason).",
                    isSuccess = false,
                    isPriority = true
                )
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("AddSellViewModel", "Error checking blacklist status", e)
            false
        }
    }

    private suspend fun executeWithFallback(
        action: suspend (GenerativeModel) -> GenerateContentResponse
    ): String {
        var lastException: Exception? = null

        for (modelName in candidateModels) {
            try {
                Log.d("GeminiAI", "Attempting request with: $modelName")
                val model = createModel(modelName)
                val response = action(model)
                val text = response.text

                if (!text.isNullOrBlank()) {
                    Log.d("GeminiAI", "Success using model: $modelName")
                    return text
                }
            } catch (e: Exception) {
                Log.w("GeminiAI", "Model $modelName failed: ${e.message}. Trying next backup model...")
                lastException = e
            }
        }

        throw lastException ?: IllegalStateException("All candidate models failed to return a response.")
    }

    fun analyzeDeviceImage(bitmap: Bitmap) {
        appState.cachedBitmap = bitmap
        errorMessage = ""
        viewModelScope.launch {
            if (checkIsUserBlacklisted()) return@launch

            isAnalyzing = true
            appState.showResult = false

            try {
                val jsonResult = withContext(Dispatchers.IO) {
                    executeWithFallback { model ->
                        model.generateContent(
                            content {
                                image(bitmap)
                                text(baseAiPrompt)
                            }
                        )
                    }
                }

                parseAndSaveJson(jsonResult, "Detected Smart Device")
            } catch (e: Exception) {
                Log.e("GeminiAI", "All models failed for image analysis", e)
                errorMessage = "AI Analysis Failed: Please try again."
            } finally {
                isAnalyzing = false
            }
        }
    }

    fun analyzeDeviceText(manualInput: String) {
        errorMessage = ""
        viewModelScope.launch {
            if (checkIsUserBlacklisted()) return@launch

            isAnalyzing = true
            appState.showResult = false
            try {
                val jsonResult = withContext(Dispatchers.IO) {
                    executeWithFallback { model ->
                        model.generateContent(
                            "$baseAiPrompt\n\nUser Inputted Device Model: $manualInput"
                        )
                    }
                }

                parseAndSaveJson(jsonResult, manualInput)
            } catch (e: Exception) {
                Log.e("GeminiAI", "All models failed for text analysis", e)
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
                    estimatedPrice = obj.getDouble("estimatedPrice")
                )
            )
        }
        appState.detectedDeviceName = defaultName
        appState.showResult = true
    }
}