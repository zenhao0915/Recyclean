package com.tarumt.recyclean.screen

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tarumt.recyclean.common.creamColor
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.defaultFontSize

@Composable
@Preview
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .background(color = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(25.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp))
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        text = "Enter To Search..",
                        fontSize = defaultFontSize,
                        fontFamily = defaultFont
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .border(0.5.dp, Color.Black, shape = RoundedCornerShape(4.dp))
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                }
            }

            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .background(color = creamColor, shape = RoundedCornerShape(8.dp))
                    .border(0.5.dp, color = creamColor, shape = RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                Text(
                    text = "Products Category",
                    fontFamily = defaultBoldFont,
                    fontSize = defaultFontSize
                )
            }

        }
    }
}