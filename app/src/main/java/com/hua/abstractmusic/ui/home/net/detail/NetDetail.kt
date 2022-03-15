package com.hua.abstractmusic.ui.home.net.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.hua.abstractmusic.other.Constant.ALL_MUSIC_TYPE
import com.hua.abstractmusic.other.Constant.NET_ALBUM_TYPE
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalNetViewModel
import com.hua.abstractmusic.ui.home.local.album.AlbumLazyItem
import com.hua.abstractmusic.ui.home.local.music.MusicLazyItems
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.viewmodels.NetViewModel

/**
 * @author : huaweikai
 * @Date   : 2022/02/14
 * @Desc   :
 */
@ExperimentalFoundationApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun NetDetail(
    type: String,
    netViewModel: NetViewModel = LocalNetViewModel.current,
    navHostController: NavHostController = LocalHomeNavController.current
) {
    when (type) {
        ALL_MUSIC_TYPE -> {
            MusicLazyItems(
                list = netViewModel.musicList.value
            ) {
                netViewModel.setPlaylist(it, netViewModel.musicList.value)
            }
        }
        NET_ALBUM_TYPE -> {
            AlbumLazyItem(
                list = netViewModel.albumList.value
            ) {
               navHostController.navigate("${Screen.LocalAlbumDetail.route}?albumId=$it&isLocal=false")
            }
        }
    }
}
