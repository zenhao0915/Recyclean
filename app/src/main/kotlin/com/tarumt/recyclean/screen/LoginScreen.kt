package com.tarumt.recyclean.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarumt.recyclean.R
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.skyBlueColor
import com.tarumt.recyclean.navigation.HomePageDestination
import com.tarumt.recyclean.navigation.navReveal
import com.tarumt.recyclean.util.GlassBox
import com.tarumt.recyclean.util.GlassLiquidSwitch

@Composable
@Preview
fun LoginScreen() = Box(contentAlignment = Alignment.Center) {
    Image(
        painter = painterResource(R.drawable.bg),
        contentScale = ContentScale.Crop,
        contentDescription = "Background",
        modifier = Modifier.fillMaxSize()
    )

    val width = 320.dp
    val height = 500.dp
    val loggedIn = remember { mutableStateOf(true) }
    GlassBox(
        modifier = Modifier
            .width(width)
            .height(height),
        borderWidth = 1.5.dp,
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.fillMaxSize(0.6f),
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                contentScale = ContentScale.Inside
            )
            Column(
                modifier = Modifier.offset(y = (-25).dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                GlassBox(
                    modifier = Modifier
                        .width(250.dp)
                        .height(45.dp),
                    RoundedCornerShape(8.dp),
                    isDarkTheme = true,
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        modifier = Modifier.offset(x = 8.dp),
                        text = "Username/Email",
                        fontFamily = defaultFont,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black.copy(alpha = 0.5f),
                        textAlign = TextAlign.Start
                    )
                }
                GlassBox(
                    modifier = Modifier
                        .width(250.dp)
                        .height(45.dp),
                    RoundedCornerShape(8.dp),
                    isDarkTheme = true,
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        modifier = Modifier.offset(x = 8.dp),
                        text = "Password",
                        fontFamily = defaultFont,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black.copy(alpha = 0.5f),
                        textAlign = TextAlign.Start
                    )
                }
                GlassBox(
                    modifier = Modifier
                        .width(120.dp)
                        .height(45.dp)
                        .navReveal(appState.navigator, HomePageDestination)
                        .background(color = skyBlueColor, shape = RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    isDarkTheme = true,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Login",
                        fontFamily = defaultFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DevMode",
                        fontFamily = defaultBoldFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.9f)
                    )
                    GlassLiquidSwitch(checked = loggedIn.value, onCheckedChange = {
                        appState.hasLoggedIn = !appState.hasLoggedIn
                        loggedIn.value = appState.hasLoggedIn
                    })
                }
            }
        }
    }
}