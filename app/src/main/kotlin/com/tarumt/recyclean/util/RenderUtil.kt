package com.tarumt.recyclean.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.defaultFontSize
import com.tarumt.recyclean.navigation.Navigations

@Composable
@Preview
fun drawNavigator() = Box(contentAlignment = Alignment.Center, modifier = Modifier.height(150.dp)) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(4.dp)
            .background(color = Color.Transparent, shape = RoundedCornerShape(30.dp))
            .border(
                width = 0.5.dp, color = Color.Gray.copy(0.4f), shape = RoundedCornerShape(30.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(Navigations.values().size) { i ->
                val isCenterElement = i == 2
                val currentNav = Navigations.values()[i]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .offset(y = if (isCenterElement) (-25).dp else 0.dp)
                            .size(if (isCenterElement) 36.dp else 24.dp),
                        imageVector = currentNav.icons,
                        contentDescription = "${currentNav.name}"
                    )
                    Text(
                        modifier = Modifier
                            .offset(y = if (isCenterElement) (-25).dp else 0.dp),
                        text = currentNav.name,
                        fontFamily = defaultFont,
                        fontSize = defaultFontSize
                    )
                }
            }
        }
    }
}