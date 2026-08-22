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
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class UserData(
    val name: String,
    val id: String,
    val level: String = "Bronze",
    var status: String = "Active",
    val totalRevenue: String = "RM 1,250.00",
    var isBlacklisted: Boolean = false,
    var blacklistReason: String = ""
)

@Composable
fun AdminDataScreen(onNavigateToUserPage: (UserData) -> Unit = {}) {
    val userList = remember {
        mutableStateListOf(
            UserData(name = "Ling Yue", id = "1224", level = "Gold", status = "Active", totalRevenue = "RM 3,450.00"),
            UserData(name = "Alice Smith", id = "1001", level = "Silver", status = "Active", totalRevenue = "RM 890.00"),
            UserData(name = "Bob Johnson", id = "1002", level = "Bronze", status = "Active", totalRevenue = "RM 420.00"),
            UserData(name = "Charlie Brown", id = "1003", level = "Gold", status = "Active", totalRevenue = "RM 2,100.00"),
            UserData(name = "Diana Prince", id = "1004", level = "Platinum", status = "Active", totalRevenue = "RM 5,600.00"),
            UserData(name = "Evan Wright", id = "1005", level = "Bronze", status = "Inactive", totalRevenue = "RM 150.00")
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedUserId by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = selectedUserId != null) {
        selectedUserId = null
    }

    val filteredUsers = userList.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.id.contains(searchQuery, ignoreCase = true)
    }

    val currentUser = userList.find { it.id == selectedUserId }

    // Wrap the entire screen in a Surface to provide a solid white background
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Crossfade(targetState = currentUser, label = "ScreenTransition") { user ->
            if (user == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2D4A3E), shape = RoundedCornerShape(12.dp))
                            .border(width = 2.dp, Color(0xFFBDA55D), shape = RoundedCornerShape(12.dp))
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = "User Profile Management",
                            fontFamily = FontFamily.Default,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF4F9F4),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE1EAE5), shape = RoundedCornerShape(12.dp))
                            .border(width = 2.dp, Color(0xFFBDA55D), shape = RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search user...", color = Color.DarkGray) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = Color(0xFFFF94B8)
                                    )
                                },
                                shape = RoundedCornerShape(50.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF2D4A3E),
                                    unfocusedBorderColor = Color(0xFFFF94B8),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(filteredUsers, key = { it.id }) { item ->
                                    UserProfileBox(
                                        userName = item.name,
                                        userId = item.id,
                                        onMoreInfoClick = { selectedUserId = item.id }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                UserPage(
                    user = user,
                    onBackClick = { selectedUserId = null },
                    onSaveBlacklist = { newIsBlacklisted, newReason ->
                        val index = userList.indexOfFirst { it.id == user.id }
                        if (index != -1) {
                            userList[index] = userList[index].copy(
                                isBlacklisted = newIsBlacklisted,
                                blacklistReason = newReason,
                                status = if (newIsBlacklisted) "Blacklisted" else "Active"
                            )
                        }
                        selectedUserId = null
                    }
                )
            }
        }
    }
}

@Composable
fun UserProfileBox(
    userName: String,
    userId: String,
    onMoreInfoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .background(Color(0xFFFFB7D9), shape = CutCornerShape(12.dp))
            .border(width = 4.dp, color = Color(0xFFB1F0FF), shape = CutCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(50.dp)
                    .background(Color.White, shape = CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Name: $userName",
                    fontFamily = FontFamily.Default,
                    fontSize = 15.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "ID: $userId",
                    fontFamily = FontFamily.Default,
                    fontSize = 15.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onMoreInfoClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D4A3E)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "More Info",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFFF4F9F4)
                )
            }
        }
    }
}

@Composable
fun UserPage(
    user: UserData,
    onBackClick: () -> Unit,
    onSaveBlacklist: (Boolean, String) -> Unit
) {
    var isBlacklisted by remember(user.id) { mutableStateOf(user.isBlacklisted) }
    var blacklistReason by remember(user.id) { mutableStateOf(user.blacklistReason) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .background(Color(0xFFE1EAE5), shape = RoundedCornerShape(12.dp))
            .border(width = 2.dp, color = Color(0xFFBDA55D), shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
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
                        contentDescription = "Back",
                        tint = Color(0xFF2D4A3E)
                    )
                }
                Text(
                    text = "User Details",
                    fontFamily = FontFamily.Default,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D4A3E)
                )
            }

            Spacer(Modifier.height(8.dp))

            Box(
                Modifier
                    .size(90.dp)
                    .background(Color.White, shape = CircleShape)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Username: ${user.name}",
                fontFamily = FontFamily.Default,
                fontSize = 18.sp,
                color = Color.Black
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "ID: ${user.id}",
                fontFamily = FontFamily.Default,
                fontSize = 18.sp,
                color = Color.Black
            )

            HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Color.Black)

            Text(
                text = "Level: ${user.level}",
                fontFamily = FontFamily.Default,
                fontSize = 18.sp,
                color = Color.Black
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Status: ${if (isBlacklisted) "Blacklisted" else user.status}",
                fontFamily = FontFamily.Default,
                fontSize = 18.sp,
                color = if (isBlacklisted) Color(0xFFD9534F) else Color.Black
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2D4A3E), shape = RoundedCornerShape(12.dp))
                    .border(width = 2.dp, Color(0xFFBDA55D), shape = RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "Total Account Revenue",
                        fontFamily = FontFamily.Default,
                        fontSize = 14.sp,
                        color = Color(0xFFBDA55D)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user.totalRevenue,
                        fontFamily = FontFamily.Default,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF4F9F4)
                    )
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color.Black)

            Text(
                text = "Account Moderation",
                fontFamily = FontFamily.Default,
                fontSize = 18.sp,
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
                        selectedColor = Color(0xFFD9534F),
                        unselectedColor = Color.DarkGray
                    )
                )
                Text(
                    text = "Blacklist Account",
                    fontFamily = FontFamily.Default,
                    fontSize = 16.sp,
                    color = if (isBlacklisted) Color(0xFFD9534F) else Color.Black,
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
                        color = if (isBlacklisted) Color.DarkGray else Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD9534F),
                    unfocusedBorderColor = Color(0xFF2D4A3E),
                    disabledBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color(0xFFE0E0E0)
                )
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { onSaveBlacklist(isBlacklisted, blacklistReason) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D4A3E)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Save Changes",
                    fontFamily = FontFamily.Default,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFF4F9F4)
                )
            }
        }
    }
}