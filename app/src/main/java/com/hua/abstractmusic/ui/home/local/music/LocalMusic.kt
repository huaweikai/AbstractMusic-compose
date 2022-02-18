package com.hua.abstractmusic.ui.home.local.music

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.LocalPopWindow
import com.hua.abstractmusic.ui.utils.MusicItem
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel


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
    state: MutableState<Boolean> = LocalPopWindow.current,
    onclick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(list, key = { _, item ->
            item.mediaId
        }) { index, item ->
            MusicItem(
                data = item,
                onClick = {
                    onclick(index)
                }
            )
        }
    }
}