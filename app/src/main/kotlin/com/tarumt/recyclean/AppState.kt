package com.tarumt.recyclean

import com.tarumt.recyclean.navigation.NavigatorState
import kotlinx.coroutines.CoroutineScope

class AppState(val scope: CoroutineScope) {
    val navigator = NavigatorState()
}