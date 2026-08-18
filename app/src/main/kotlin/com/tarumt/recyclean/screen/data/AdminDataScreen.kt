package com.tarumt.recyclean.screen.data

import android.R
import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.styleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.type.content
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.util.DrawTemplate
import com.tarumt.recyclean.util.GlassBox

data class UserData(
    val name: String,
    val id: String
)

@Composable
@Preview
fun AdminDataScreen() = DrawTemplate {
    val dummyUsers = listOf(
        UserData(name = "Ling Yue", id = "1224"),
        UserData(name = "Alice Smith", id = "1001"),
        UserData(name = "Bob Johnson", id = "1002"),
        UserData(name = "Charlie Brown", id = "1003"),
        UserData(name = "Diana Prince", id = "1004"),
        UserData(name = "Evan Wright", id = "1005")
    )

    Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 16.dp)
                .background(Color(0xFF2D4A3E), shape = RoundedCornerShape(12.dp))
                .border(width = 2.dp, Color(0xFFBDA55D), shape = RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)) {
                Text(
                    text = "User Profile Management",
                    fontFamily = defaultBoldFont,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF4F9F4),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    Spacer(modifier = Modifier.padding(2.dp))

    Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 16.dp)
                .height(640.dp)
                .background(Color(0xFFE1EAE5), shape = RoundedCornerShape(12.dp))
                .border(width = 2.dp, Color(0xFFBDA55D), shape = RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = {
                        Text(
                            "Search user...",
                            color = Color.DarkGray
                        )
                    },
                    leadingIcon ={
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
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .height(50.dp)
                )
            }

            Box(modifier = Modifier.fillMaxWidth()
                .padding(top = 80.dp)
                .height(588.dp)
                .background(Color.Transparent, shape = RoundedCornerShape(12.dp))){
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(dummyUsers) {users ->
                        UserProfileBox(
                            userName = users.name,
                            userId = users.id
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun UserProfileBox( userName: String = "Linyue",
                    userId: String = "1234",
                    onUserClick: () -> Unit = {}) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(top = 16.dp)
            .height(100.dp)
            .background( Color(0xFFFFB7D9), shape = CutCornerShape(12.dp))
            .border(width = 4.dp, color = Color(0xFFB1F0FF), shape = CutCornerShape(12.dp))
            .clickable{ onUserClick()}
    ){
        Row(Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 12.dp)) {
            Box(Modifier
                .size(70.dp)
                .background(Color.White, shape = CircleShape)){
            }

            Column(Modifier.weight(1f).padding(horizontal = 4.dp, vertical = 5.dp)) {
                Text(
                    text = "Username : $userName",
                    fontFamily = defaultFont,
                    fontSize = 20.sp,
                    color = Color.Black,
                    maxLines = 1,        // Keeps it strictly on 1 line
                    overflow = TextOverflow.Ellipsis  //continues with ... when exceed 1 line
                )

                Spacer(Modifier.padding(2.dp))

                Text(
                    text = "ID: $userId",
                    fontFamily = defaultFont,
                    fontSize = 20.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
@Preview
fun UserPage(onUserClick: () -> Unit = {},
             userName: String = "Linyue",
             userId: String = "1234",
             level: String = "Bronze",status: String = "N/A") = DrawTemplate {
    Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .height(800.dp)
                .background(Color(0xFFE1EAE5), shape = RoundedCornerShape(12.dp))
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 12.dp).align(Alignment.TopCenter)
            ) {
                Box(
                    Modifier
                        .padding(horizontal = 70.dp, vertical = 20.dp)
                        .size(180.dp)
                        .background(Color.White, shape = CircleShape)
                ) {}

                Text(
                    text = "Username: $userName",
                    fontFamily = defaultFont,
                    fontSize = 30.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.padding(2.dp))

                Text(
                    text = "ID: $userId",
                    fontFamily = defaultFont,
                    fontSize = 30.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                HorizontalDivider(Modifier.padding(horizontal = 0.dp, vertical = 10.dp), color = Color.Black)

                Text(
                    text = "Level = $level",
                    fontFamily = defaultFont,
                    fontSize = 30.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Status: $status",
                    fontFamily = defaultFont,
                    fontSize = 30.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}