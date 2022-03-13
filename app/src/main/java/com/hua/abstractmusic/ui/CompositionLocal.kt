package com.hua.abstractmusic.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.utils.WindowSize
import com.hua.abstractmusic.ui.viewmodels.*
import com.hua.abstractmusic.utils.ComposeUtils


/**
 * @author : huaweikai
 * @Date   : 2022/02/09
 * @Desc   :
 */

val LocalHomeViewModel = staticCompositionLocalOf<HomeViewModel>{
    error("LocalMusicViewModel is NULL")
}

val LocalNetViewModel = staticCompositionLocalOf<NetViewModel>{
    error("LocalNetViewModel is Null")
}

val LocalUserViewModel = staticCompositionLocalOf<UserViewModel>{
    error("LocalUserViewModel is NULL")
}
val LocalPlayingViewModel = staticCompositionLocalOf<PlayingViewModel>{
    error("LocalPlayingViewModel is NULL")
}

val LocalThemeViewModel =
    staticCompositionLocalOf<ThemeViewModel> { error("Can't get theme view model") }

val LocalHomeNavController = staticCompositionLocalOf<NavHostController> {
    error("LocalHomeNavController is NULL")
}

val LocalAppNavController = staticCompositionLocalOf<NavHostController> {
    error("LocalAppNavController is NULL")
}

val LocalMusicScreenSecondColor = staticCompositionLocalOf<Color>{
    error("color is null")
}
val LocalScreenSize = compositionLocalOf<WindowSize> {
    error("")
}

val LocalComposeUtils = staticCompositionLocalOf<ComposeUtils> {
    error("")
}

val LocalPopWindow = staticCompositionLocalOf<MutableState<Boolean>>{
    error("")
}

val LocalPopWindowItem = staticCompositionLocalOf<MutableState<MediaItem>>{
    error("")
}

val LocalBottomControllerHeight = staticCompositionLocalOf<Dp>{
    error("")
}