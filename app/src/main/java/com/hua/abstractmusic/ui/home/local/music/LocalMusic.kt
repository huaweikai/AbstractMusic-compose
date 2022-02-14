package com.hua.abstractmusic.ui.home.local.music

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media2.common.MediaMetadata
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.utils.MusicItem
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.utils.browserType
import com.hua.abstractmusic.utils.isPlayable


/**
 * @author : huaweikai
 * @Date   : 2022/01/11
 * @Desc   : localmusic
 */
@Composable
fun LocalMusic(
    viewModel: HomeViewModel = LocalHomeViewModel.current
) {
    MusicLazyItems(list = viewModel.localMusicList.value) {
        viewModel.setPlaylist(it, viewModel.localMusicList.value)
    }
}

@Composable
fun MusicLazyItems(
    list: List<MediaData>,
    onclick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(list, key = { _, item ->
            item.mediaId
        }) { index, item ->
            MusicItem(
                data = item
            ) {
                if (item.mediaItem.metadata?.isPlayable == true &&
                    item.mediaItem.metadata?.browserType == MediaMetadata.BROWSABLE_TYPE_NONE
                ) {
                    onclick(index)
//                    viewModel.setPlaylist(index, viewModel.localMusicList.value)
                }
            }
        }
    }
}