package com.hua.abstractmusic.ui.utils

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

/**
 * @author : huaweikai
 * @Date   : 2022/03/24
 * @Desc   :
 */
class SnackBarDataWithError(
    override val message: String,
    val isError: Boolean,
    val label:String,
    val action: (suspend ()->Unit)?
) : SnackbarVisuals {
    override val actionLabel: String
        get() = label
    override val withDismissAction: Boolean
        get() = false
    override val duration: SnackbarDuration
        get() = SnackbarDuration.Short
}