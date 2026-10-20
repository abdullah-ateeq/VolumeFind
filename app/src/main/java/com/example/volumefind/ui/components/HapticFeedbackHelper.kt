package com.example.volumefind.ui.components

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

class HapticHelper(private val view: View, private val enabled: Boolean) {
    fun lightClick() {
        if (!enabled) return
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    fun confirm() {
        if (!enabled) return
        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
    }

    fun error() {
        if (!enabled) return
        view.performHapticFeedback(HapticFeedbackConstants.REJECT)
    }
}

@Composable
fun rememberHapticHelper(enabled: Boolean): HapticHelper {
    val view = LocalView.current
    return remember(view, enabled) {
        HapticHelper(view, enabled)
    }
}
