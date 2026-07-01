package com.tarumt.recyclean.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tarumt.recyclean.R
import com.tarumt.recyclean.common.defaultBoldFont
import com.tarumt.recyclean.common.defaultFont
import com.tarumt.recyclean.common.defaultFontSize
import com.tarumt.recyclean.common.orangeCreamColor
import com.tarumt.recyclean.navigation.Navigations
import kotlin.math.abs

@Composable
@Preview
fun DrawNavigator() =
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.height(120.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(4.dp)
                .background(color = Color.Transparent, shape = RoundedCornerShape(30.dp))
                .border(
                    width = 0.5.dp, color = Color.Gray.copy(0.4f), shape = RoundedCornerShape(30.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(Navigations.entries.size) { i ->
                    val isCenterElement = i == 2
                    val currentNav = Navigations.entries[i]
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            modifier = Modifier
                                .offset(y = if (isCenterElement) (-25).dp else 0.dp)
                                .size(if (isCenterElement) 36.dp else 24.dp),
                            imageVector = currentNav.icons,
                            contentDescription = currentNav.name
                        )
                        Text(
                            modifier = Modifier.offset(y = if (isCenterElement) (-25).dp else 0.dp),
                            text = currentNav.name,
                            fontFamily = defaultFont,
                            fontSize = defaultFontSize
                        )
                    }
                }
            }
        }
    }

@Composable
fun DrawResultBox(
    image: Painter = painterResource(R.drawable.ic_launcher_background),
    topicText: String = "iPhone 67 Pro",
    productGrade: ProductGrade = ProductGrade.S,
    maxWidth: Int = 170,
    maxHeight: Int = 250
) = Box(
    modifier = Modifier
        .width(maxWidth.dp)
        .height(maxHeight.dp)
        .background(color = Color.White.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp))
        .border(
            width = 0.5.dp, color = Color.Gray.copy(alpha = 0.75f), shape = RoundedCornerShape(8.dp)
        )
        .clip(shape = RoundedCornerShape(8.dp)), contentAlignment = Alignment.TopCenter
) {
    Column(
        modifier = Modifier.offset(y = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            modifier = Modifier
                .width(160.dp)
                .height(200.dp),
            painter = image,
            contentScale = ContentScale.Fit,
            contentDescription = null
        )
        // Grade
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .background(color = orangeCreamColor, shape = RoundedCornerShape(4.dp))
                    .border(
                        width = 0.5.dp, color = orangeCreamColor, shape = RoundedCornerShape(4.dp)
                    ), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = productGrade.grade,
                    fontFamily = defaultBoldFont,
                    fontSize = defaultFontSize,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.width(6.dp))
            Text(
                text = topicText,
                fontFamily = defaultFont,
                fontSize = defaultFontSize,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun GlassLiquidSwitch(
    checked: Boolean, onCheckedChange: (Boolean) -> Unit, scale: Float = 1.0f
) {
    var isPressed by remember { mutableStateOf(false) }

    val trackWidth = 52.dp * scale
    val trackHeight = 32.dp * scale
    val padding = 3.dp * scale
    val normalThumbSize = 26.dp * scale
    val pressedThumbWidth = 33.dp * scale

    var currentTrackingOffsetPx by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    val offsetOffDp = padding
    val offsetOnDp = trackWidth - padding - (if (isPressed) pressedThumbWidth else normalThumbSize)

    val thumbWidth by animateDpAsState(
        targetValue = if (isPressed) pressedThumbWidth else normalThumbSize,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = 450f)
    )

    val targetOffsetDp by animateDpAsState(
        targetValue = if (checked) (trackWidth - padding - thumbWidth) else padding,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 320f)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (checked) Color(0xFF76ff8e).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f),
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )
    val borderColor by animateColorAsState(
        targetValue = if (checked) Color(0xFF76ff8e).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f),
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    Box(
        modifier = Modifier
            .size(width = trackWidth, height = trackHeight)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, CircleShape)
            .pointerInput(checked, scale) {
                val minOffsetPx = density.run { offsetOffDp.toPx() }
                val normalMaxOffsetPx =
                    density.run { (trackWidth - padding - normalThumbSize).toPx() }
                val swipeThresholdPx = 15f * scale

                awaitPointerEventScope {
                    while (true) {
                        val down = awaitPointerEvent(PointerEventPass.Initial)
                        val downChange =
                            down.changes.firstOrNull()?.takeIf { it.pressed } ?: continue
                        isPressed = true

                        currentTrackingOffsetPx = if (checked) normalMaxOffsetPx else minOffsetPx

                        val startX = downChange.position.x
                        val pointerId = downChange.id
                        var hasSwipedAction = false

                        while (true) {
                            val nextEvent = awaitPointerEvent(PointerEventPass.Initial)
                            val change = nextEvent.changes.firstOrNull { it.id == pointerId }
                            if (change == null || change.isConsumed) {
                                isPressed = false
                                break
                            }

                            val deltaX = change.position.x - startX
                            val currentThumbWidthPx = density.run { thumbWidth.toPx() }
                            val trackWidthPx = density.run { trackWidth.toPx() }
                            val paddingPx = density.run { padding.toPx() }
                            val currentMaxOffsetPx = trackWidthPx - paddingPx - currentThumbWidthPx

                            val trackingStartPx = if (checked) normalMaxOffsetPx else minOffsetPx
                            currentTrackingOffsetPx =
                                (trackingStartPx + deltaX).coerceIn(minOffsetPx, currentMaxOffsetPx)

                            if (!hasSwipedAction && abs(deltaX) > swipeThresholdPx) {
                                hasSwipedAction = true
                            }

                            if (!change.pressed) {
                                isPressed = false
                                val isReleasedInside =
                                    change.position.x in 0f..size.width.toFloat() && change.position.y in 0f..size.height.toFloat()

                                if (hasSwipedAction) {
                                    val directionOn = deltaX > 0
                                    val isStateChanging = directionOn != checked

                                    if (isStateChanging) {
                                        onCheckedChange(directionOn)
                                    }
                                } else {
                                    if (isReleasedInside) {
                                        onCheckedChange(!checked)
                                    }
                                }
                                break
                            }
                        }
                    }
                }
            }) {
        Box(modifier = Modifier
            .offset(x = if (isPressed) density.run { currentTrackingOffsetPx.toDp() } else targetOffsetDp)
            .padding(vertical = padding)
            .size(width = thumbWidth, height = normalThumbSize)
            .clip(CircleShape)
            .background(
                if (isPressed) Color.Transparent.copy(alpha = 0.3f) else Color.White.copy(
                    alpha = 0.9f
                )
            ))
    }
}