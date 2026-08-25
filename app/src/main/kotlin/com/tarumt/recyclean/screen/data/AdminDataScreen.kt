package com.tarumt.recyclean.screen.data

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
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
import com.tarumt.recyclean.common.defaultFontSize
import com.tarumt.recyclean.common.goldColor
import com.tarumt.recyclean.common.lightBlueColor
import com.tarumt.recyclean.common.orangeCreamColor
import com.tarumt.recyclean.common.silverColor
import com.tarumt.recyclean.common.skyBlueColor
import com.tarumt.recyclean.common.vanillaColor

@Composable
fun AdminDataScreen(
    viewModel: AdminDataViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 头部标题栏
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

                    // 🌟 1. 用户管理与搜索区（高度短一半：260dp）
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
                        Column(modifier = Modifier.fillMaxSize()) {
                            OutlinedTextField(
                                value = viewModel.searchQuery,
                                onValueChange = { viewModel.searchQuery = it },
                                placeholder = {
                                    Text(
                                        text = "Search user by name or ID...",
                                        fontFamily = defaultFont,
                                        fontSize = defaultFontSize,
                                        color = Color.Gray
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = skyBlueColor
                                    )
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
                                    .height(52.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (viewModel.isLoading) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = skyBlueColor)
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
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
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

                    HorizontalDivider(color = vanillaColor, thickness = 1.dp)

                    // 🌟 2. 底部个人交易记录（与 DefaultDataScreen 一致）
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
            } else {
                UserPage(
                    user = user,
                    onBackClick = { viewModel.selectedUserId = null },
                    onSaveBlacklist = { newIsBlacklisted, newReason ->
                        viewModel.saveBlacklist(user, newIsBlacklisted, newReason)
                    }
                )
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
            .height(82.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = vanillaColor, shape = RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(lightBlueColor.copy(alpha = 0.4f), shape = CircleShape)
                    .border(0.5.dp, skyBlueColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = skyBlueColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

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
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onMoreInfoClick,
                colors = ButtonDefaults.buttonColors(containerColor = skyBlueColor.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
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
            .padding(14.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(Modifier.height(8.dp))

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