package com.tarumt.recyclean.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned

fun Modifier.navReveal(
    navigator: NavigatorState,
    destination: NavDestination,
): Modifier = composed {
    val cardTopLeft = remember { mutableStateOf(Offset.Zero) }

    this
        .onGloballyPositioned { coords ->
            cardTopLeft.value = coords.boundsInWindow().topLeft
        }
        .pointerInput(destination) {
            awaitPointerEventScope {
                while (true) {
                    val downEvent = awaitPointerEvent(PointerEventPass.Initial)
                    val downChange =
                        downEvent.changes.firstOrNull()?.takeIf { it.pressed } ?: continue

                    val startPosition = downChange.position
                    val pointerId = downChange.id
                    var hasMoved = false

                    while (true) {
                        val nextEvent = awaitPointerEvent(PointerEventPass.Initial)
                        val change = nextEvent.changes.firstOrNull { it.id == pointerId } ?: break

                        if ((change.position - startPosition).getDistance() > 8f) {
                            hasMoved = true
                        }

                        if (!change.pressed) {
                            if (!hasMoved) {
                                val screenPos = cardTopLeft.value + startPosition
                                navigator.navigateTo(destination, screenPos)
                            }
                            break
                        }
                    }
                }
            }
        }
}

fun Modifier.swipeToBack(navigator: NavigatorState): Modifier = this.pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val downEvent = awaitPointerEvent(PointerEventPass.Initial)
            val downChange = downEvent.changes.firstOrNull()?.takeIf { it.pressed } ?: continue
            if (downChange.position.x > 120f) continue

            val pointerId = downChange.id
            var totalX = 0f

            while (true) {
                val nextEvent = awaitPointerEvent(PointerEventPass.Initial)
                val change = nextEvent.changes.firstOrNull { it.id == pointerId }
                if (change == null || !change.pressed) break

                val dragX = change.position.x - change.previousPosition.x
                totalX += dragX

                if (totalX > 180f) {
                    navigator.navigateHome()
                    break
                }
            }
        }
    }
}