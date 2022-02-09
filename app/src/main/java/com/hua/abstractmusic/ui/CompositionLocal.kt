package com.hua.abstractmusic.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.home.viewmodels.AlbumDetailViewModel
import com.hua.abstractmusic.ui.home.viewmodels.ArtistDetailViewModel
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.home.viewmodels.UserViewModel


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

val LocalUserViewModel = staticCompositionLocalOf<UserViewModel>{
    error("LocalUserViewModel is NULL")
}

val LocalHomeNavController = staticCompositionLocalOf<NavHostController> {
    error("LocalHomeNavController is NULL")
}

val LocalAppNavController = staticCompositionLocalOf<NavHostController> {
    error("LocalAppNavController is NULL")
}