package com.hua.abstractmusic.ui.viewmodels

import android.os.Build
import androidx.annotation.ColorInt
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hua.abstractmusic.preference.PreferenceManager
import com.hua.abstractmusic.ui.utils.Monet
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.kdrag0n.monet.theme.ColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author: Chen
 * @createTime: 2021/12/10 5:08 下午
 * @description:
 **/
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    private val _isReady = mutableStateOf(Build.VERSION.SDK_INT < Build.VERSION_CODES.O && preferenceManager.themeColor == Int.MIN_VALUE)
    val isReady: State<Boolean> get() = _isReady
    private val _color = mutableStateOf<ColorScheme?>(null)
    val monetColor: State<ColorScheme?> get() = _color
    fun init() {
        if (preferenceManager.themeColor != Int.MIN_VALUE) {
            getMonetColor(Color(preferenceManager.themeColor))
        } else {
            _isReady.value = true
        }
    }

    fun getMonetColor(seed: Color) {
        viewModelScope.launch(Dispatchers.Default) {
            val new = Monet.getMonetColor(seed.toArgb())
            _isReady.value = true
            _color.value = new
        }
    }

    fun setCustomThemeColor(@ColorInt color: Int) {
        preferenceManager.themeColor = color
        getMonetColor(Color(preferenceManager.themeColor))
    }

    fun closeCustomThemeColor() {
        preferenceManager.themeColor = Int.MIN_VALUE
        _color.value = null
    }
}