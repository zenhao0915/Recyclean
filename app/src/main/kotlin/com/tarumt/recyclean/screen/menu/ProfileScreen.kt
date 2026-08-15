package com.tarumt.recyclean.screen.menu

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarumt.recyclean.R
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.creamColor
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.defaultFontSize
import com.tarumt.recyclean.common.vanillaColor
import com.tarumt.recyclean.util.DrawGradeBox
import com.tarumt.recyclean.util.DrawTemplate
import com.tarumt.recyclean.util.data.Grade
import com.tarumt.recyclean.util.data.UserState
import com.tarumt.recyclean.util.data.UserTier

@Composable
@Preview
fun ProfileScreen() = DrawTemplate {
    Row(
        modifier = Modifier.padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
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
        val username =
            if (appState.currentUserState == UserState.ThirdParty) appState.currentMerchantName else appState.currentUser?.userName
                ?: "NULL"
        Text(
            modifier = Modifier.offset(y = 12.dp),
            text = username,
            fontFamily = defaultBoldFont,
            fontSize = 24.sp
        )

        Icon(
            modifier = Modifier.offset(y = 16.dp),
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = null
        )
        Icon(
            modifier = Modifier.offset(y = 16.dp),
            imageVector = Icons.Default.Settings,
            contentDescription = null
        )
    }

    HorizontalDivider(modifier = Modifier.scale(1.25f), thickness = 1.5.dp, color = Color.Black)

    // User Grade
    Spacer(Modifier.height(36.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(color = creamColor, shape = RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = vanillaColor, shape = RoundedCornerShape(12.dp)),
        //contentAlignment = Alignment.TopCenter
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.offset(y = 10.dp),
                    text = "Welcome, ${appState.currentUser?.userName} !",
                    fontFamily = defaultBoldFont,
                    fontSize = 18.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    22.dp, Alignment.CenterHorizontally
                )
            ) {
                DrawGradeBox(Grade.S_Plus, size = 48.dp, fontSize = 24.sp)

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val currentTier = UserTier.Gold
                    Text(
                        text = "Current Tier Status",
                        fontFamily = defaultFont,
                        fontSize = defaultFontSize
                    )
                    Text(
                        text = currentTier.name,
                        fontFamily = defaultFont,
                        fontSize = 28.sp,
                        color = currentTier.color
                    )
                }
            }
        }
    }

    // Transaction History
    Spacer(Modifier.height(30.dp))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(width = 0.5.dp, color = Color.Black)
    ) {
        Text("No Result Currently")
    }

    DrawAdminProfile()
}

@Composable
private fun DrawAdminProfile() {
}