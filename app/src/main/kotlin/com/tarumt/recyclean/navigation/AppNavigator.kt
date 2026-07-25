package com.tarumt.recyclean.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.tarumt.recyclean.common.appState
import kotlin.math.sqrt

sealed class NavDestination

class NavigatorState {
    var current by mutableStateOf<NavDestination?>(null)
        private set

    var revealOrigin by mutableStateOf(Offset.Zero)
        private set

    fun navigateTo(destination: NavDestination, tapPosition: Offset) {
        revealOrigin = tapPosition
        current = destination
    }

    fun navigateHome() {
        current = null
        revealOrigin = appState.lastTouchOffset
    }
}

@Composable
fun AppNavigator(
    state: NavigatorState,
    modifier: Modifier = Modifier,
    homeContent: @Composable () -> Unit,
    destinationContent: @Composable (NavDestination) -> Unit,
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val revealProgress = remember { Animatable(0f) }
    var animatingOrigin by remember { mutableStateOf(Offset.Zero) }
    var lastDestination by remember { mutableStateOf<NavDestination?>(null) }

    val currentDestination = state.current
    if (currentDestination != null) lastDestination = currentDestination

    LaunchedEffect(currentDestination) {
        if (currentDestination != null) {
            animatingOrigin = state.revealOrigin
            revealProgress.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.72f, stiffness = 300f)
            )
        } else {
            revealProgress.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f)
            )
            lastDestination = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
    ) {
        homeContent()

        val activeDestination = lastDestination

        if (activeDestination != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent()
                            }
                        }
                    }
            )
        }

        if (activeDestination != null && containerSize != IntSize.Zero) {
            val maxRadius = sqrt(
                containerSize.width.toFloat().let { it * it } +
                        containerSize.height.toFloat().let { it * it }
            )
            val currentRadius = (revealProgress.value * maxRadius).coerceAtLeast(0f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        val path = Path().apply {
                            addOval(Rect(center = animatingOrigin, radius = currentRadius))
                        }
                        clipPath(path) {
                            this@drawWithContent.drawContent()
                        }
                    }
            ) {
                destinationContent(activeDestination)
            }
        }
    }
}