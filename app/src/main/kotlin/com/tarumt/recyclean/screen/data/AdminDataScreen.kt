package com.tarumt.recyclean.screen.data

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tarumt.recyclean.common.bronzeColor
import com.tarumt.recyclean.common.creamColor
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.goldColor
import com.tarumt.recyclean.common.lightBlueColor
import com.tarumt.recyclean.common.orangeCreamColor
import com.tarumt.recyclean.common.silverColor
import com.tarumt.recyclean.common.skyBlueColor
import com.tarumt.recyclean.common.vanillaColor
import com.tarumt.recyclean.util.data.Appointment

@Composable
fun AdminDataScreen(
    viewModel: AdminDataViewModel = viewModel()
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // 🌟 进入页面时清空搜索内容并重新抓取数据
    LaunchedEffect(Unit) {
        viewModel.searchQuery = ""
        viewModel.selectedUserId = null
        viewModel.fetchUsers()
    }

    BackHandler(enabled = viewModel.selectedUserId != null) {
        viewModel.selectedUserId = null
    }

    val currentUser = viewModel.selectedUser
    val transactions = viewModel.completedTransactions

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = currentUser, label = "ScreenTransition") { user ->
            if (user == null) {
                if (isLandscape) {
                    AdminDashboardLandscape(
                        viewModel = viewModel,
                        transactions = transactions
                    )
                } else {
                    AdminDashboardPortrait(
                        viewModel = viewModel,
                        transactions = transactions
                    )
                }
            } else {
                UserPage(
                    user = user,
                    isLandscape = isLandscape,
                    onBackClick = { viewModel.selectedUserId = null },
                    onSaveBlacklist = { newIsBlacklisted, newReason ->
                        viewModel.saveBlacklist(user, newIsBlacklisted, newReason)
                    }
                )
            }
        }
    }
}

// =============================================================================
// 📱 1. Portrait Dashboard
// =============================================================================
@Composable
private fun AdminDashboardPortrait(
    viewModel: AdminDataViewModel,
    transactions: List<Appointment>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(orangeCreamColor, shape = RoundedCornerShape(12.dp))
                .border(width = 1.dp, vanillaColor, shape = RoundedCornerShape(12.dp))
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = "User Profile Management",
                fontFamily = defaultBoldFont,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    creamColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(width = 1.dp, vanillaColor, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            UserSearchAndListContent(viewModel = viewModel)
        }

        HorizontalDivider(color = vanillaColor, thickness = 1.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Completed Transactions (${transactions.size})",
                fontFamily = defaultBoldFont,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        if (transactions.isEmpty() && !viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No completed transactions yet.\nCompleted orders will show up here!",
                    fontFamily = defaultFont,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp
                )
            }
        } else {
            transactions.forEach { item ->
                TransactionCard(appointment = item)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// =============================================================================
// 🔄 2. Landscape Dashboard
// =============================================================================
@Composable
private fun AdminDashboardLandscape(
    viewModel: AdminDataViewModel,
    transactions: List<Appointment>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(orangeCreamColor, shape = RoundedCornerShape(10.dp))
                .border(width = 1.dp, vanillaColor, shape = RoundedCornerShape(10.dp))
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "User Profile Management",
                fontFamily = defaultBoldFont,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        creamColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(width = 1.dp, vanillaColor, shape = RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                UserSearchAndListContent(viewModel = viewModel)
            }

            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
                    .background(
                        creamColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(width = 1.dp, vanillaColor, shape = RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Completed Transactions (${transactions.size})",
                        fontFamily = defaultBoldFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (transactions.isEmpty() && !viewModel.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No completed transactions yet.",
                                fontFamily = defaultFont,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(transactions, key = { it.appointmentId }) { item ->
                                TransactionCard(appointment = item)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 🌟 用户搜索框与列表公共组件 (带一键清空按钮)
 */
@Composable
private fun UserSearchAndListContent(viewModel: AdminDataViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.searchQuery = it },
            singleLine = true,
            placeholder = {
                Text(
                    text = "Search user by name or ID...",
                    fontFamily = defaultFont,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = skyBlueColor,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (viewModel.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear Search",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(50.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = skyBlueColor,
                unfocusedBorderColor = vanillaColor,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = skyBlueColor, modifier = Modifier.size(28.dp))
            }
        } else if (viewModel.filteredUsers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No matching users found.",
                    fontFamily = defaultFont,
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(
                    viewModel.filteredUsers,
                    key = { it.fullId.ifEmpty { it.id } }) { item ->
                    UserProfileBox(
                        userName = item.name,
                        userId = item.id,
                        onMoreInfoClick = { viewModel.selectedUserId = item.id }
                    )
                }
            }
        }
    }
}

/**
 * 🌟 用户信息简卡
 */
@Composable
fun UserProfileBox(
    userName: String,
    userId: String,
    onMoreInfoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp))
            .border(width = 1.dp, color = vanillaColor, shape = RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(lightBlueColor.copy(alpha = 0.4f), shape = CircleShape)
                    .border(0.5.dp, skyBlueColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = skyBlueColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Name: $userName",
                    fontFamily = defaultBoldFont,
                    fontSize = 13.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "ID: $userId",
                    fontFamily = defaultFont,
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Button(
                onClick = onMoreInfoClick,
                colors = ButtonDefaults.buttonColors(containerColor = skyBlueColor.copy(alpha = 0.85f)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "More Info",
                    fontFamily = defaultBoldFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * 🌟 用户详情与风控封禁页
 */
@Composable
fun UserPage(
    user: UserData,
    isLandscape: Boolean = false,
    onBackClick: () -> Unit,
    onSaveBlacklist: (Boolean, String) -> Unit
) {
    var isBlacklisted by remember(user.id) { mutableStateOf(user.isBlacklisted) }
    var blacklistReason by remember(user.id) { mutableStateOf(user.blacklistReason) }

    val levelBadgeColor = when (user.level.lowercase()) {
        "gold" -> goldColor
        "silver" -> silverColor
        "bronze" -> bronzeColor
        else -> skyBlueColor
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .background(creamColor.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = vanillaColor, shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = "User Details",
                    fontFamily = defaultBoldFont,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            Modifier
                                .size(64.dp)
                                .background(lightBlueColor.copy(alpha = 0.3f), shape = CircleShape)
                                .border(1.dp, skyBlueColor.copy(alpha = 0.4f), CircleShape)
                                .align(Alignment.CenterHorizontally),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = skyBlueColor,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Text(
                            text = "Username: ${user.name}",
                            fontFamily = defaultBoldFont,
                            fontSize = 15.sp,
                            color = Color.Black
                        )

                        Text(
                            text = "ID: ${user.id}",
                            fontFamily = defaultFont,
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Level: ",
                                fontFamily = defaultFont,
                                fontSize = 13.sp,
                                color = Color.Black
                            )
                            Text(
                                text = user.level,
                                fontFamily = defaultBoldFont,
                                fontSize = 14.sp,
                                color = levelBadgeColor
                            )
                        }

                        Text(
                            text = "Status: ${if (isBlacklisted) "Blacklisted" else user.status}",
                            fontFamily = defaultFont,
                            fontSize = 13.sp,
                            color = if (isBlacklisted) orangeCreamColor else Color(0xFF2E7D32)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(skyBlueColor, shape = RoundedCornerShape(10.dp))
                                .border(width = 1.dp, vanillaColor, shape = RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Total Account Revenue",
                                    fontFamily = defaultFont,
                                    fontSize = 11.sp,
                                    color = creamColor
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = user.totalRevenue,
                                    fontFamily = defaultBoldFont,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = goldColor
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Account Moderation",
                            fontFamily = defaultBoldFont,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = isBlacklisted,
                                onClick = { isBlacklisted = !isBlacklisted },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = orangeCreamColor,
                                    unselectedColor = Color.DarkGray
                                )
                            )
                            Text(
                                text = "Blacklist Account",
                                fontFamily = defaultFont,
                                fontSize = 14.sp,
                                color = if (isBlacklisted) orangeCreamColor else Color.Black,
                                fontWeight = if (isBlacklisted) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        OutlinedTextField(
                            value = blacklistReason,
                            onValueChange = { blacklistReason = it },
                            enabled = isBlacklisted,
                            label = {
                                Text(
                                    text = if (isBlacklisted) "Reason for Blacklisting" else "Enable blacklist to enter reason",
                                    fontFamily = defaultFont,
                                    fontSize = 12.sp,
                                    color = if (isBlacklisted) Color.DarkGray else Color.Gray
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = orangeCreamColor,
                                unfocusedBorderColor = vanillaColor,
                                disabledBorderColor = Color.LightGray.copy(alpha = 0.5f),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color(0xFFF9F9F9)
                            )
                        )

                        Spacer(Modifier.height(4.dp))

                        Button(
                            onClick = { onSaveBlacklist(isBlacklisted, blacklistReason) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = skyBlueColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Save Changes",
                                fontFamily = defaultBoldFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        Modifier
                            .size(80.dp)
                            .background(lightBlueColor.copy(alpha = 0.3f), shape = CircleShape)
                            .border(1.dp, skyBlueColor.copy(alpha = 0.4f), CircleShape)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = skyBlueColor,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Username: ${user.name}",
                        fontFamily = defaultBoldFont,
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "ID: ${user.id}",
                        fontFamily = defaultFont,
                        fontSize = 15.sp,
                        color = Color.DarkGray
                    )

                    HorizontalDivider(
                        Modifier.padding(vertical = 10.dp),
                        color = vanillaColor,
                        thickness = 1.dp
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Level: ",
                            fontFamily = defaultFont,
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                        Text(
                            text = user.level,
                            fontFamily = defaultBoldFont,
                            fontSize = 16.sp,
                            color = levelBadgeColor
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Status: ${if (isBlacklisted) "Blacklisted" else user.status}",
                        fontFamily = defaultFont,
                        fontSize = 15.sp,
                        color = if (isBlacklisted) orangeCreamColor else Color(0xFF2E7D32)
                    )

                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(skyBlueColor, shape = RoundedCornerShape(12.dp))
                            .border(width = 1.dp, vanillaColor, shape = RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "Total Account Revenue",
                                fontFamily = defaultFont,
                                fontSize = 13.sp,
                                color = creamColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = user.totalRevenue,
                                fontFamily = defaultBoldFont,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = goldColor
                            )
                        }
                    }

                    HorizontalDivider(
                        Modifier.padding(vertical = 12.dp),
                        color = vanillaColor,
                        thickness = 1.dp
                    )

                    Text(
                        text = "Account Moderation",
                        fontFamily = defaultBoldFont,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = isBlacklisted,
                            onClick = { isBlacklisted = !isBlacklisted },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = orangeCreamColor,
                                unselectedColor = Color.DarkGray
                            )
                        )
                        Text(
                            text = "Blacklist Account",
                            fontFamily = defaultFont,
                            fontSize = 15.sp,
                            color = if (isBlacklisted) orangeCreamColor else Color.Black,
                            fontWeight = if (isBlacklisted) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    OutlinedTextField(
                        value = blacklistReason,
                        onValueChange = { blacklistReason = it },
                        enabled = isBlacklisted,
                        label = {
                            Text(
                                text = if (isBlacklisted) "Reason for Blacklisting" else "Enable blacklist to enter reason",
                                fontFamily = defaultFont,
                                fontSize = 13.sp,
                                color = if (isBlacklisted) Color.DarkGray else Color.Gray
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = orangeCreamColor,
                            unfocusedBorderColor = vanillaColor,
                            disabledBorderColor = Color.LightGray.copy(alpha = 0.5f),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color(0xFFF9F9F9)
                        )
                    )

                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = { onSaveBlacklist(isBlacklisted, blacklistReason) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = skyBlueColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Save Changes",
                            fontFamily = defaultBoldFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


@SuppressLint("DefaultLocale")
@Composable
fun TransactionCard(appointment: Appointment) {
    var isExpanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "ArrowRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isExpanded = !isExpanded
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 头部：设备名称 + COMPLETED 标签 + 展开箭头
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.deviceName.ifBlank { "Unknown Device" },
                    fontFamily = defaultBoldFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "COMPLETED",
                                fontSize = 9.sp,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(arrowRotation)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = "Seller",
                    modifier = Modifier.size(13.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sold to: ${appointment.targetSeller.ifBlank { "Merchant" }}",
                    fontFamily = defaultFont,
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date",
                    modifier = Modifier.size(13.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Date: ${appointment.scheduledDate}",
                    fontFamily = defaultFont,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // 🌟 核心：仅在展开时显示的零件明细部分
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

                    Text(
                        text = "Sold Parts Breakdown:",
                        fontFamily = defaultFont,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )

                    appointment.selectedParts.forEach { part ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• ${part.name}",
                                fontFamily = defaultFont,
                                fontSize = 11.sp,
                                color = Color.DarkGray,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = String.format("RM %.2f", part.estimatedPrice),
                                fontFamily = defaultFont,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

            // 底部：实收总额
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = "Paid",
                        tint = orangeCreamColor,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Payout Received",
                        fontFamily = defaultFont,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }

                Text(
                    text = String.format("RM %.2f", appointment.estimatedValue),
                    fontFamily = defaultBoldFont,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = orangeCreamColor
                )
            }
        }
    }
}