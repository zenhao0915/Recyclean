package com.tarumt.recyclean.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

fun openGoogleMap(
    context: Context,
    address: String,
    latitude: Double? = null,
    longitude: Double? = null
) {
    val gmmIntentUri = if (latitude != null && longitude != null) {
        "geo:$latitude,$longitude?q=${Uri.encode(address)}".toUri()
    } else {
        "geo:0,0?q=${Uri.encode(address)}".toUri()
    }

    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
        setPackage("com.google.android.apps.maps")
    }

    try {
        context.startActivity(mapIntent)
    } catch (_: Exception) {
        val browserUri =
            "https://www.google.com/maps/search/?api=1&query=${Uri.encode(address)}".toUri()
        context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
    }
}