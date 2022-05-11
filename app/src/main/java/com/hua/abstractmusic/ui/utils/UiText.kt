package com.hua.abstractmusic.ui.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

/**
 * @author : huaweikai
 * @Date   : 2022/05/11
 * @Desc   :
 */
sealed class UiText {
    data class DynamicString(val value: String): UiText()
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ): UiText()

    @Composable
    fun asString(): String {
        return when(this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args)
        }
    }

    fun asString(context: Context): String {
        return when(this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}