package com.hua.abstractmusic.ui.home.net.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hua.abstractmusic.other.Constant.ALL_MUSIC_TYPE
import com.hua.abstractmusic.other.Constant.NET_ALBUM_TYPE
import com.hua.abstractmusic.ui.LocalNetViewModel
import com.hua.abstractmusic.ui.home.local.album.AlbumLazyItem
import com.hua.abstractmusic.ui.home.local.music.MusicLazyItems
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
    netViewModel: NetViewModel = LocalNetViewModel.current
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
                //todo:跳转到专辑详情
            }
        }
    }
}
