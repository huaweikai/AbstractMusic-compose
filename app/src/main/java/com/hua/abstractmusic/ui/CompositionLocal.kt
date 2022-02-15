package com.hua.abstractmusic.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.utils.WindowSize
import com.hua.abstractmusic.ui.viewmodels.*


/**
 * @author : huaweikai
 * @Date   : 2022/02/09
 * @Desc   :
 */

val LocalHomeViewModel = staticCompositionLocalOf<HomeViewModel>{
    error("LocalMusicViewModel is NULL")
}

val LocalAlbumViewModel = staticCompositionLocalOf<AlbumDetailViewModel>{
    error("LocalAlbumViewModel is NULL")
}

val LocalArtistViewModel = staticCompositionLocalOf<ArtistDetailViewModel>{
    error("LocalArtistViewModel is NULL")
}

val LocalNetViewModel = staticCompositionLocalOf<NetViewModel>{
    error("LocalNetViewModel is Null")
}

val LocalUserViewModel = staticCompositionLocalOf<UserViewModel>{
    error("LocalUserViewModel is NULL")
}

val LocalHomeNavController = staticCompositionLocalOf<NavHostController> {
    error("LocalHomeNavController is NULL")
}

val LocalAppNavController = staticCompositionLocalOf<NavHostController> {
    error("LocalAppNavController is NULL")
}

val LocalMusicScreenFirstColor = staticCompositionLocalOf<Color>{
    error("color is null")
}

val LocalMusicScreenSecondColor = staticCompositionLocalOf<Color>{
    error("color is null")
}
val LocalScreenSize = compositionLocalOf<WindowSize> {
    error("")
}