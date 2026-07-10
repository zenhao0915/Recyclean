package com.tarumt.recyclean.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tarumt.recyclean.util.DrawTemplate
import com.tarumt.recyclean.util.GlassBox

@Composable
@Preview
fun AdminDataSceen() = DrawTemplate {

    GlassBox(modifier = Modifier.fillMaxWidth()) {

        Column {

                Text(
                    text = "User Ling Yue",
                    textAlign = TextAlign.Center
                )

                Text(
                     text = "Request Item : Qing Che",
                    modifier = Modifier.offset(x=(-100).dp)

                )

        }



    }


}
