package com.hua.abstractmusic.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.utils.WindowSize
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.hua.abstractmusic.ui.viewmodels.ThemeViewModel
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
import com.hua.abstractmusic.utils.ComposeUtils



/**
 * @author : huaweikai
 * @Date   : 2022/02/09
 * @Desc   :
 */

val LocalUserViewModel = staticCompositionLocalOf<UserViewModel>{
    error("LocalUserViewModel is NULL")
}
val LocalPlayingViewModel = staticCompositionLocalOf<PlayingViewModel>{
    error("LocalPlayingViewModel is NULL")
}
val LocalThemeViewModel =
    staticCompositionLocalOf<ThemeViewModel> { error("Can't get theme view model") }

val LocalHomeViewModel = staticCompositionLocalOf<HomeViewModel> {
    error("LocalHomeViewModel is NULL")
}

val LocalAppNavController = staticCompositionLocalOf<NavHostController> {
    error("LocalAppNavController is NULL")
}

val LocalMusicScreenSecondColor = staticCompositionLocalOf<Color>{
    error("color is null")
}

val LocalMusicScreenFirstColor = staticCompositionLocalOf<Color>{
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