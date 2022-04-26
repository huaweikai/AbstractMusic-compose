package com.hua.abstractmusic.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * @author : huaweikai
 * @Date   : 2022/04/16
 * @Desc   :
 */
@Composable
fun LifecycleFocusClearUtils(){
    val focus = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { source, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                focus.clearFocus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        this.onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}