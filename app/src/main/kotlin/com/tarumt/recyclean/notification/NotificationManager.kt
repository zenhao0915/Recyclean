package com.tarumt.recyclean.notification

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tarumt.recyclean.R
import kotlinx.coroutines.delay
import java.util.PriorityQueue
import kotlin.time.Duration.Companion.milliseconds

object NotificationManager {
    class Notification(val message: String, val isSuccess: Boolean, val priorityLevel: Int)

    private val notificationQueue = PriorityQueue<Notification>(compareBy { it.priorityLevel })
    var currentNotification by mutableStateOf<Notification?>(null)
        private set
    var lastNotification by mutableStateOf<Notification?>(null)
        private set
    private var hasInit = false

    fun addToast(message: String, isSuccess: Boolean = false, prio: Boolean = false) {
        notificationQueue.add(Notification(message, isSuccess, if (prio) Int.MAX_VALUE else 0))
    }

    @Composable
    fun UpdateNotification() {
        LaunchedEffect(Unit) {
            if (hasInit) return@LaunchedEffect
            hasInit = true
            while (true) {
                val nextNotification = synchronized(notificationQueue) {
                    notificationQueue.poll()
                }
                if (nextNotification != null) {
                    Log.d("Notification", nextNotification.message)
                    currentNotification = nextNotification
                    delay(2500.milliseconds)
                    lastNotification = currentNotification
                    currentNotification = null
                }
                delay(250.milliseconds)
            }
        }
    }

    @Composable
    fun CallToast() {
        val displayNotification = currentNotification ?: lastNotification
        AnimatedVisibility(
            currentNotification != null,
            enter = scaleIn(spring()),
            exit = scaleOut(spring())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 30.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(color = Color.Transparent, shape = CircleShape)
                        .border(
                            width = 0.5.dp,
                            color = Color.Black.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        displayNotification?.let { notification ->
                            Icon(
                                painterResource(if (notification.isSuccess) R.drawable.check_circle else R.drawable.error),
                                contentDescription = null
                            )
                            Text(text = notification.message)
                        }
                    }
                }
            }
        }
    }
}