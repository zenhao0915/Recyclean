package com.tarumt.recyclean.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tarumt.recyclean.AppState
import com.tarumt.recyclean.R

const val appTitle = "Recyclean"
var appState = AppState()

val defaultFont = FontFamily(Font(R.font.opensans_regular, FontWeight.Normal))
val defaultBoldFont = FontFamily(Font(R.font.opensans_bold, FontWeight.Bold))
val defaultFontSize = 14.sp

val creamColor = Color(0xFFFFFDD0)
val vanillaColor = Color(0xFFF3E5AB)
val orangeCreamColor = Color(0xFFFFAC71)
val lightBlueColor = Color(0xFFADD8E6)
val skyBlueColor = Color(0xFF466EF2)