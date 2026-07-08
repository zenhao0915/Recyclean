package com.tarumt.recyclean.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.tarumt.recyclean.R
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.creamColor
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.defaultFontSize
import com.tarumt.recyclean.common.vanillaColor
import com.tarumt.recyclean.navigation.AddSellPageDestination
import com.tarumt.recyclean.navigation.LoginPageDestination
import com.tarumt.recyclean.navigation.navReveal
import com.tarumt.recyclean.util.DrawResultBox
import com.tarumt.recyclean.util.GlassBox
import com.tarumt.recyclean.util.data.Grade
import com.tarumt.recyclean.util.data.ProductsCategory
import com.tarumt.recyclean.util.data.Sellers

@Composable
@Preview
fun HomeScreen() {
    val scrollableState = rememberScrollState()
    var drawPopup by remember { mutableStateOf(false) }
    var currentProductSelected by remember { mutableStateOf<ProductsCategory?>(null) }

    // Search
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .offset(y = 14.dp)
                .fillMaxHeight(0.86f)
                .verticalScroll(scrollableState),
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
                Icon(
                    modifier = Modifier.navReveal(appState.navigator, LoginPageDestination),
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
                Box(
                    modifier = Modifier
                        .width(290.dp)
                        .height(25.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 6.dp),
                            text = "Enter To Search..",
                            fontSize = defaultFontSize,
                            fontFamily = defaultFont
                        )
                        Spacer(Modifier.width(90.dp))
                        VerticalDivider(
                            modifier = Modifier.padding(vertical = 6.dp), thickness = 2.dp
                        )
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .offset(x = 4.dp),
                            text = "Search",
                            fontSize = defaultFontSize,
                            fontFamily = defaultBoldFont
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .border(0.5.dp, Color.Black, shape = RoundedCornerShape(4.dp))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "ChatButton"
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .border(0.5.dp, Color.Black, shape = RoundedCornerShape(4.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Wallet, contentDescription = "Wallet")
                    }
                }
            }

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

            // Goods Category
            Spacer(Modifier.height(24.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .border(1.dp, vanillaColor, RoundedCornerShape(6.dp))
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(
                            24.dp, Alignment.CenterHorizontally
                        ), verticalArrangement = Arrangement.spacedBy(
                            12.dp, Alignment.CenterVertically
                        ), itemVerticalAlignment = Alignment.CenterVertically, maxItemsInEachRow = 4
                    ) {
                        val goodsList = ProductsCategory.entries
                        repeat(goodsList.size) {
                            val currentProduct = goodsList.elementAt(it)
                            Column(
                                modifier = Modifier
                                    .width(64.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            /* Jump To Items Selection Here */
                                            drawPopup = true
                                            currentProductSelected = currentProduct
                                        }),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = currentProduct.icons, contentDescription = ""
                                )
                                Text(
                                    text = currentProduct.name,
                                    fontSize = defaultFontSize,
                                    fontFamily = defaultFont,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }


            // My Device
            Spacer(Modifier.height(15.dp))
            GlassBox(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(60.dp)
                    .border(1.dp, color = vanillaColor, CircleShape)
                    .background(color = Color.Transparent, CircleShape),
                borderWidth = 1.dp
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = null,
                        contentScale = ContentScale.Inside,
                        modifier = Modifier.scale(0.8f)
                    )
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "My Device",
                            fontFamily = defaultBoldFont,
                            fontSize = defaultFontSize
                        )
                        Text(
                            text = "RM 3770 (Estimated)",
                            fontFamily = defaultFont,
                            fontSize = defaultFontSize
                        )
                    }
                    GlassBox(
                        modifier = Modifier,
                        isDarkTheme = true,
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(modifier = Modifier.padding(4.dp), text = "Quote?", fontSize = 18.sp)
                    }
                }
            }

            // Vouchers
            Spacer(Modifier.height(16.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(60.dp)
                    .border(0.5.dp, Color.Red, RoundedCornerShape(12.dp))
                    .background(Color.Red.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                        text = "\u1D3f\u1D396767",
                        color = Color.White,
                        fontFamily = defaultBoldFont,
                        fontSize = 22.sp,
                    )
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .width(210.dp)
                            .height(40.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .offset(x = 15.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Test",
                                fontFamily = defaultFont,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            VerticalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .offset(x = 60.dp), thickness = 1.dp, color = Color.Gray
                            )
                            Text(
                                modifier = Modifier.offset(x = 60.dp),
                                text = "Redeem",
                                fontFamily = defaultFont,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Recommend Result
            FlowRow(
                modifier = Modifier.padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                itemVerticalAlignment = Alignment.CenterVertically,
                maxItemsInEachRow = 2
            ) {
                val sellers = Sellers.entries.toTypedArray()
                repeat(sellers.size) {
                    val seller = sellers[it]
                    DrawResultBox(
                        topicText = seller.sellerName,
                        sellerGrade = seller.gradeDetails,
                        image = painterResource(seller.sellerLogo)
                    )
                }
            }
        }

        if (drawPopup) {
            currentProductSelected?.let {
                Popup(
                    offset = IntOffset(0, -320), alignment = Alignment.Center, onDismissRequest = {
                        drawPopup = false
                        currentProductSelected = null
                    }) {

                    GlassBox(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        isDarkTheme = true,
                        isHighAlpha = true
                    ) {
                        FlowRow(
                            modifier = Modifier.padding(4.dp),
                            itemVerticalAlignment = Alignment.CenterVertically,
                            maxItemsInEachRow = 3,
                            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                            horizontalArrangement = Arrangement.spacedBy(
                                16.dp, Alignment.CenterHorizontally
                            )
                        ) {
                            it.devices.forEach { device ->
                                DrawResultBox(
                                    topicText = device.deviceName,
                                    sellerGrade = Grade.GG,
                                    image = painterResource(device.icon),
                                    maxWidth = 100,
                                    maxHeight = 160,
                                    textmaxWidth = 60
                                ) {
                                    device.deviceName.convertToPart()
                                    appState.navigator.navigateTo(AddSellPageDestination, Offset.Zero)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}