package com.hua.abstractmusic.ui.home.local.music

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media2.common.MediaMetadata
import com.hua.abstractmusic.ui.home.MusicItem
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.utils.browserType
import com.hua.abstractmusic.utils.isPlayable


/**
 * @author : huaweikai
 * @Date   : 2022/01/11
 * @Desc   : localmusic
 */
@Composable
fun LocalMusic(
    viewModel: HomeViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(viewModel.localMusicList.value) { index, item ->
            Log.d("TAG", "LocalMusic: ${viewModel.localMusicList.value}")
            MusicItem(
                data = item
            ){
                if (item.mediaItem.metadata?.isPlayable == true &&
                    item.mediaItem.metadata?.browserType == MediaMetadata.BROWSABLE_TYPE_NONE
                ) {
                    viewModel.setPlaylist(index, viewModel.localMusicList.value)
                } else {
                    viewModel.init(item.mediaId)
                }
            }
        }
    }
}