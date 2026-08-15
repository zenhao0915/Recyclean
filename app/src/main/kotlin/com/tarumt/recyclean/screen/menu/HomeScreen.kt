package com.tarumt.recyclean.screen.menu

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.recyclean.R
import com.tarumt.recyclean.common.appState
import com.tarumt.recyclean.common.creamColor
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.defaultFontSize
import com.tarumt.recyclean.common.vanillaColor
import com.tarumt.recyclean.notification.NotificationManager
import com.tarumt.recyclean.screen.addsell.convertToPart
import com.tarumt.recyclean.util.DrawResultBox
import com.tarumt.recyclean.util.GlassBox
import com.tarumt.recyclean.util.data.Grade
import com.tarumt.recyclean.util.data.ProductsCategory
import com.tarumt.recyclean.util.data.Sellers
import com.tarumt.recyclean.util.data.UserState
import com.tarumt.recyclean.util.openGoogleMap
import kotlin.random.Random

@Composable
@Preview
fun HomeScreen(viewModel: HomeScreenViewModel = viewModel()) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        HomeScreenLandscape(viewModel)
    } else {
        HomeScreenPortrait(viewModel)
    }
}

@Composable
fun HomeScreenPortrait(viewModel: HomeScreenViewModel) {
    val scrollableState = rememberScrollState()
    var drawPopup by remember { mutableStateOf(false) }
    var currentProductSelected by remember { mutableStateOf<ProductsCategory?>(null) }
    var currentSellersSelected by remember { mutableStateOf<Sellers?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.98f)
                .verticalScroll(scrollableState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Search Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.clickable(
                        enabled = true,
                        onClick = { viewModel.processUserLogout() }
                    ),
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout"
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
                            modifier = Modifier.padding(vertical = 6.dp),
                            thickness = 2.dp
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
                        ),
                        verticalArrangement = Arrangement.spacedBy(
                            12.dp, Alignment.CenterVertically
                        ),
                        itemVerticalAlignment = Alignment.CenterVertically,
                        maxItemsInEachRow = 4
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
                                            if (appState.currentUser?.currentUserState != UserState.ThirdParty) {
                                                drawPopup = true
                                                currentProductSelected = currentProduct
                                            }
                                        }
                                    ),
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
                                    .offset(x = 60.dp),
                                thickness = 1.dp,
                                color = Color.Gray
                            )
                            Text(
                                modifier = Modifier
                                    .offset(x = 60.dp)
                                    .clickable(true, onClick = {
                                        NotificationManager.addToast(
                                            "WALAO EH JOIS",
                                            isSuccess = Random.nextBoolean()
                                        )
                                    }),
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
                        image = painterResource(seller.sellerLogo),
                        onClick = {
                            drawPopup = true
                            currentSellersSelected = seller
                        }
                    )
                }
            }
        }

        // Popup Layers
        if (drawPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        drawPopup = false
                        currentSellersSelected = null
                        currentProductSelected = null
                    }
            )

            currentSellersSelected?.let { currentSeller ->
                val context = LocalContext.current
                Popup(
                    offset = IntOffset(
                        appState.lastTouchOffset.x.toInt() - 60,
                        appState.lastTouchOffset.y.toInt() - 80
                    ),
                    onDismissRequest = {
                        drawPopup = false
                        currentSellersSelected = null
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                            .border(
                                color = Color.Black,
                                width = 0.5.dp,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(
                                    modifier = Modifier
                                        .width(180.dp)
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(
                                        8.dp, Alignment.Top
                                    )
                                ) {
                                    Text(
                                        "Address: ",
                                        fontSize = 16.sp,
                                        fontFamily = defaultBoldFont
                                    )
                                    Text(
                                        currentSeller.address,
                                        fontSize = defaultFontSize,
                                        fontFamily = defaultFont
                                    )

                                    Text("Phone: ", fontSize = 16.sp, fontFamily = defaultBoldFont)
                                    Text(
                                        currentSeller.phoneNumber,
                                        fontSize = defaultFontSize,
                                        fontFamily = defaultFont
                                    )

                                    Text(
                                        "Operation Time: ",
                                        fontSize = 16.sp,
                                        fontFamily = defaultBoldFont
                                    )
                                    Text(
                                        currentSeller.operationTime,
                                        fontSize = defaultFontSize,
                                        fontFamily = defaultFont
                                    )
                                }
                                Image(
                                    modifier = Modifier
                                        .width(160.dp)
                                        .height(160.dp)
                                        .padding(4.dp),
                                    painter = painterResource(currentSeller.sellerLogo),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .height(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4285F4), shape = CircleShape)
                                    .clickable {
                                        openGoogleMap(
                                            context = context,
                                            address = currentSeller.address,
                                            latitude = currentSeller.latitude,
                                            longitude = currentSeller.longitude
                                        )
                                        drawPopup = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Map Pin",
                                        tint = Color.White
                                    )
                                    Text(
                                        text = "Open in Google Maps",
                                        color = Color.White,
                                        fontSize = defaultFontSize,
                                        fontFamily = defaultBoldFont
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            } ?: run {
                currentProductSelected?.let {
                    Popup(
                        offset = IntOffset(0, -320),
                        alignment = Alignment.Center,
                        onDismissRequest = {
                            drawPopup = false
                            currentProductSelected = null
                        }
                    ) {
                        GlassBox(
                            modifier = Modifier.fillMaxWidth(0.85f),
                            isDarkTheme = true,
                            isHighAlpha = true
                        ) {
                            FlowRow(
                                modifier = Modifier.padding(4.dp),
                                itemVerticalAlignment = Alignment.CenterVertically,
                                maxItemsInEachRow = 3,
                                verticalArrangement = Arrangement.spacedBy(
                                    16.dp, Alignment.CenterVertically
                                ),
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
                                        textmaxWidth = 80
                                    ) {
                                        device.deviceName.convertToPart()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenLandscape(viewModel: HomeScreenViewModel) {
    val scrollableState = rememberScrollState()
    var drawPopup by remember { mutableStateOf(false) }
    var currentProductSelected by remember { mutableStateOf<ProductsCategory?>(null) }
    var currentSellersSelected by remember { mutableStateOf<Sellers?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollableState)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.clickable(
                        enabled = true,
                        onClick = { viewModel.processUserLogout() }
                    ),
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(6.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Enter To Search..",
                            fontSize = defaultFontSize,
                            fontFamily = defaultFont
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            VerticalDivider(
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp),
                                thickness = 1.5.dp,
                                color = Color.Gray.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Search",
                                fontSize = defaultFontSize,
                                fontFamily = defaultBoldFont
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .border(0.5.dp, Color.Black, shape = RoundedCornerShape(6.dp))
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = "ChatButton",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .border(0.5.dp, Color.Black, shape = RoundedCornerShape(6.dp))
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wallet,
                            contentDescription = "Wallet",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .background(color = creamColor, shape = RoundedCornerShape(8.dp))
                    .border(0.5.dp, color = creamColor, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Products Category",
                    fontFamily = defaultBoldFont,
                    fontSize = defaultFontSize
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .border(1.dp, vanillaColor, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                    itemVerticalAlignment = Alignment.CenterVertically,
                    maxItemsInEachRow = 8
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
                                        if (appState.currentUser?.currentUserState != UserState.ThirdParty) {
                                            drawPopup = true
                                            currentProductSelected = currentProduct
                                        }
                                    }
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = currentProduct.icons, contentDescription = currentProduct.name)
                            Text(text = currentProduct.name, fontSize = defaultFontSize, fontFamily = defaultFont, maxLines = 1)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(0.95f),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    GlassBox(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .border(1.dp, color = vanillaColor, CircleShape)
                            .background(color = Color.Transparent, CircleShape),
                        borderWidth = 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.logo),
                                    contentDescription = null,
                                    contentScale = ContentScale.Inside,
                                    modifier = Modifier.size(36.dp)
                                )
                                Column {
                                    Text(text = "My Device", fontFamily = defaultBoldFont, fontSize = defaultFontSize)
                                    Text(text = "RM 3770 (Estimated)", fontFamily = defaultFont, fontSize = 11.sp, color = Color.DarkGray)
                                }
                            }
                            GlassBox(
                                modifier = Modifier,
                                isDarkTheme = true,
                                contentAlignment = Alignment.Center
                            ) {
                                Text(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), text = "Quote?", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .border(0.5.dp, Color.Red, RoundedCornerShape(12.dp))
                            .background(Color.Red.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "\u1D3f\u1D396767", color = Color.White, fontFamily = defaultBoldFont, fontSize = 18.sp)
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .padding(start = 12.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Test", fontFamily = defaultFont, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    VerticalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 1.dp, color = Color.LightGray)
                                    Text(
                                        modifier = Modifier.clickable {
                                            NotificationManager.addToast("WALAO EH JOIS", isSuccess = Random.nextBoolean())
                                        },
                                        text = "Redeem",
                                        fontFamily = defaultFont,
                                        fontSize = 14.sp,
                                        color = Color.Red,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                itemVerticalAlignment = Alignment.CenterVertically,
                maxItemsInEachRow = 4
            ) {
                val sellers = Sellers.entries.toTypedArray()
                repeat(sellers.size) {
                    val seller = sellers[it]
                    DrawResultBox(
                        topicText = seller.sellerName,
                        sellerGrade = seller.gradeDetails,
                        image = painterResource(seller.sellerLogo),
                        onClick = {
                            drawPopup = true
                            currentSellersSelected = seller
                        }
                    )
                }
            }
        }

        if (drawPopup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        drawPopup = false
                        currentSellersSelected = null
                        currentProductSelected = null
                    }
            )

            currentSellersSelected?.let { currentSeller ->
                val context = LocalContext.current
                val popupScrollState = rememberScrollState()

                Popup(
                    alignment = Alignment.Center,
                    onDismissRequest = {
                        drawPopup = false
                        currentSellersSelected = null
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 480.dp)
                            .heightIn(max = 280.dp)
                            .padding(16.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                            .border(color = Color.Black.copy(alpha = 0.2f), width = 0.5.dp, shape = RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .verticalScroll(popupScrollState)
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("Address: ", fontSize = 14.sp, fontFamily = defaultBoldFont)
                                    Text(currentSeller.address, fontSize = 12.sp, fontFamily = defaultFont)

                                    Text("Phone: ", fontSize = 14.sp, fontFamily = defaultBoldFont)
                                    Text(currentSeller.phoneNumber, fontSize = 12.sp, fontFamily = defaultFont)

                                    Text("Operation Time: ", fontSize = 14.sp, fontFamily = defaultBoldFont)
                                    Text(currentSeller.operationTime, fontSize = 12.sp, fontFamily = defaultFont)
                                }

                                Image(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .padding(4.dp),
                                    painter = painterResource(currentSeller.sellerLogo),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4285F4), shape = CircleShape)
                                    .clickable {
                                        openGoogleMap(
                                            context = context,
                                            address = currentSeller.address,
                                            latitude = currentSeller.latitude,
                                            longitude = currentSeller.longitude
                                        )
                                        drawPopup = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Map Pin",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Open in Google Maps",
                                        color = Color.White,
                                        fontSize = defaultFontSize,
                                        fontFamily = defaultBoldFont
                                    )
                                }
                            }
                        }
                    }
                }
            } ?: run {
                currentProductSelected?.let { product ->
                    val productScrollState = rememberScrollState()
                    Popup(
                        alignment = Alignment.Center,
                        onDismissRequest = {
                            drawPopup = false
                            currentProductSelected = null
                        }
                    ) {
                        GlassBox(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .heightIn(max = 280.dp),
                            isDarkTheme = true,
                            isHighAlpha = true
                        ) {
                            Column(
                                modifier = Modifier
                                    .verticalScroll(productScrollState)
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    itemVerticalAlignment = Alignment.CenterVertically,
                                    maxItemsInEachRow = 5,
                                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                                ) {
                                    product.devices.forEach { device ->
                                        DrawResultBox(
                                            topicText = device.deviceName,
                                            sellerGrade = Grade.GG,
                                            image = painterResource(device.icon),
                                            maxWidth = 100,
                                            maxHeight = 150,
                                            textmaxWidth = 80
                                        ) {
                                            device.deviceName.convertToPart()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}