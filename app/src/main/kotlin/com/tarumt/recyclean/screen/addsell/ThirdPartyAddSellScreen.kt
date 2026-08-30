package com.tarumt.recyclean.screen.addsell

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.tarumt.recyclean.R
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.util.DrawTemplate

@Composable
fun ThirdPartyAddSellScreen() = DrawTemplate(Alignment.Center) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(ImageDecoderDecoder.Factory())
        }
        .build()

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.spinning_cat)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Spinning Cat Animation",
                modifier = Modifier.size(150.dp)
            )

            Spacer(Modifier.width(20.dp))

            Text(
                modifier = Modifier.weight(1f),
                text = "AddSell Function Is Not Available For ThirdParty Currently.",
                textAlign = TextAlign.Center,
                fontFamily = defaultBoldFont,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.width(20.dp))

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.popcat)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "PopCat Animation",
                modifier = Modifier.size(150.dp)
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.spinning_cat)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Spinning Cat Animation",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.fillMaxWidth(0.85f),
                text = "AddSell Function Is Not Available For ThirdParty Currently.",
                textAlign = TextAlign.Center,
                fontFamily = defaultBoldFont,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.popcat)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "PopCat Animation",
                modifier = Modifier.size(200.dp)
            )
        }
    }
}