package com.tarumt.recyclean.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.creamColor
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.defaultFontSize
import com.tarumt.recyclean.common.vanillaColor
import com.tarumt.recyclean.navigation.LoginPageDestination
import com.tarumt.recyclean.navigation.navReveal
import com.tarumt.recyclean.util.DrawResultBox

@Composable
@Preview
fun HomeScreen() {
    val productsCategory = remember {
        linkedMapOf(
            ("Recommend" to Icons.Default.Recommend),
            ("Luxury Goods" to Icons.Default.AccountBalance),
            ("Electronics" to Icons.Default.Phone)
        )
    }
    val goodsList = remember {
        linkedMapOf(
            ("Mobile" to Icons.Default.PhoneIphone),
            ("Tablet" to Icons.Default.Tablet),
            ("Laptop" to Icons.Default.Laptop),
            ("Watch" to Icons.Default.Watch),
            ("Bicycle" to Icons.Default.Motorcycle),
            ("Bags" to Icons.Default.ShoppingBag),
            ("Monitor" to Icons.Default.Monitor),
            ("Camera" to Icons.Default.CameraAlt)
        )
    }
    val scrollableState = rememberScrollState()

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

            // Products Category
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
            Spacer(Modifier.height(30.dp))
            Box(
                modifier = Modifier.border(
                    color = Color.Transparent, width = 0.5.dp, shape = RoundedCornerShape(6.dp)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(productsCategory.size) { i ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = productsCategory.values.elementAt(i),
                                    contentDescription = ""
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = productsCategory.keys.elementAt(i),
                                    fontFamily = defaultFont,
                                    fontSize = defaultFontSize
                                )
                            }
                        }
                    }
                }
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
                        repeat(goodsList.size) {
                            Column(
                                modifier = Modifier.width(64.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = goodsList.values.elementAt(it),
                                    contentDescription = ""
                                )
                                Text(
                                    text = goodsList.keys.elementAt(it),
                                    fontSize = defaultFontSize,
                                    fontFamily = defaultFont,
                                    maxLines = 1
                                )
                            }
                        }
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
                repeat(3) {
                    DrawResultBox()
                }
            }
        }
    }
}