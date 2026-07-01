package com.tarumt.recyclean.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.tarumt.recyclean.R
import com.tarumt.recyclean.common.defaultBoldFont

@Composable
@Preview
fun LoginScreen() = Box(
    modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White),
    contentAlignment = Alignment.Center
) {
    Column(
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(painter = painterResource(R.drawable.logo), contentDescription = null)
        Text(text = "User Login", fontFamily = defaultBoldFont, fontSize = 32.sp)
        Row() { }
    }
}