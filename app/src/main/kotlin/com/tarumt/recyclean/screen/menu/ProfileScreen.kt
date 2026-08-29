package com.tarumt.recyclean.screen.menu

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        ProfileScreenLandscape(viewModel)
    } else {
        ProfileScreenPortrait(viewModel)
    }
}

@Composable
fun ProfileScreenPortrait(viewModel: ProfileViewModel) = DrawTemplate {
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
                    text = "Welcome, ${appState.currentUser?.userName ?: "NULL"} !",
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
    Spacer(Modifier.height(24.dp))

    Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable(enabled = true, onClick = {
                    viewModel.processUserLogout()
                }),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null
                )
                Text(
                    "Logout",
                    fontFamily = defaultBoldFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = defaultFontSize
                )
            }
        }
    }
}

@Composable
fun ProfileScreenLandscape(viewModel: ProfileViewModel) = DrawTemplate {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .border(width = 1.5.dp, color = Color.Black, shape = CircleShape)
                    .padding(3.dp)
            ) {
                Image(
                    modifier = Modifier.size(56.dp),
                    painter = painterResource(R.drawable.profile),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            val username =
                if (appState.currentUserState == UserState.ThirdParty) appState.currentMerchantName
                else appState.currentUser?.userName ?: "NULL"

            Text(
                text = username,
                fontFamily = defaultBoldFont,
                fontSize = 20.sp
            )
        }

        HorizontalDivider(thickness = 1.dp, color = Color.Black.copy(alpha = 0.3f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .background(color = creamColor, shape = RoundedCornerShape(12.dp))
                    .border(width = 1.dp, color = vanillaColor, shape = RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome, ${appState.currentUser?.userName ?: "NULL"} !",
                        fontFamily = defaultBoldFont,
                        fontSize = 15.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            16.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        DrawGradeBox(Grade.S_Plus, size = 42.dp, fontSize = 20.sp)

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
                                fontSize = 22.sp,
                                color = currentTier.color
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(width = 0.5.dp, color = Color.Black.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier.clickable(enabled = true, onClick = {
                            viewModel.processUserLogout()
                        }),
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout"
                        )
                        Text(
                            "Logout",
                            fontFamily = defaultBoldFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = defaultFontSize
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                        Text(
                            "Settings",
                            fontFamily = defaultBoldFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = defaultFontSize
                        )
                    }
                }
            }
        }
    }
}