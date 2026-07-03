package com.tarumt.recyclean.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarumt.recyclean.R
import com.tarumt.recyclean.common.defaultBoldFont

@Composable
@Preview
fun ProfileScreen() = Box(
    modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White),
    contentAlignment = Alignment.Center
) {
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 36.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .border(width = 1.5.dp, color = Color.Black, shape = CircleShape)
                .padding(4.dp)
        ) {
            Image(
                modifier = Modifier.size(80.dp),
                painter = painterResource(R.drawable.profile),
                contentDescription = null
            )
        }

        Text(
            modifier = Modifier.offset(y = 12.dp),
            text = "Ling Yue",
            fontFamily = defaultBoldFont,
            fontSize = 24.sp
        )

        Icon(imageVector = Icons.Default.Chat, contentDescription = null)
        Icon(imageVector = Icons.Default.Settings, contentDescription = null)
    }

    Text(text = "Test")
    DrawAdminProfile()
}

@Composable
private fun DrawAdminProfile() {
}