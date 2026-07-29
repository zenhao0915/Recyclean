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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tarumt.recyclean.R
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.defaultFontSize
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.PriorityQueue
import kotlin.time.Duration.Companion.milliseconds

object NotificationManager {
    data class Notification(
        val message: String,
        val isSuccess: Boolean,
        val priorityLevel: Int,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val notificationQueue = PriorityQueue<Notification>(
        compareByDescending<Notification> { it.priorityLevel }.thenBy { it.timestamp }
    )

    var currentNotification by mutableStateOf<Notification?>(null)
        private set
    var lastNotification by mutableStateOf<Notification?>(null)
        private set

    private var displayJob: Job? = null

    fun addToast(message: String, isSuccess: Boolean = true, isPriority: Boolean = false) {
        val priority = if (isPriority) Int.MAX_VALUE else 0
        val newNotification = Notification(message, isSuccess, priority)

        synchronized(notificationQueue) {
            notificationQueue.add(newNotification)
        }

        if (isPriority) {
            val curr = currentNotification
            if (curr != null && curr.priorityLevel < Int.MAX_VALUE) {
                displayJob?.cancel() // 强制中断 2500ms 的 delay
            }
        }
    }

    @Composable
    fun UpdateNotification() {
        LaunchedEffect(Unit) {
            while (true) {
                val nextNotification = synchronized(notificationQueue) {
                    notificationQueue.poll()
                }

                if (nextNotification != null) {
                    Log.d("Notification", nextNotification.message)
                    currentNotification = nextNotification

                    val job = launch {
                        delay(2500.milliseconds)
                    }
                    displayJob = job

                    try {
                        job.join()
                    } catch (_: CancellationException) {
                        Log.d("Notification", "Interrupted by higher priority toast!")
                    } finally {
                        lastNotification = currentNotification
                        currentNotification = null
                        displayJob = null
                    }
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
                        .background(color = Color.Black.copy(alpha = 0.9f), shape = CircleShape)
                        .border(
                            width = 0.5.dp,
                            color = Color.Black.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            4.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        displayNotification?.let { notification ->
                            Icon(
                                painterResource(if (notification.isSuccess) R.drawable.check_circle else R.drawable.error),
                                contentDescription = null,
                                tint = if (notification.isSuccess) Color.Green else Color.Red
                            )
                            Text(
                                text = notification.message,
                                fontFamily = defaultFont,
                                fontSize = defaultFontSize,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}