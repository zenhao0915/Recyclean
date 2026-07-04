package com.tarumt.recyclean.util.data

import androidx.compose.ui.graphics.Color
import com.tarumt.recyclean.common.bronzeColor
import com.tarumt.recyclean.common.creamColor
import com.tarumt.recyclean.common.goldColor
import com.tarumt.recyclean.common.metallicCyanColor
import com.tarumt.recyclean.common.silverColor

enum class UserTier(val color: Color) {
    SuperFans(metallicCyanColor), Gold(goldColor), Silver(silverColor), Bronze(bronzeColor)
}