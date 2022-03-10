package com.hua.abstractmusic.ui.home.local.music

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.LocalHomeViewModel
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
    bottomControllerHeight: Dp = LocalBottomControllerHeight.current,
    onclick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = bottomControllerHeight)
    ) {
        items(
            count = list.size,
            itemContent = { index->
                MusicItem(
                    data = list[index],
                    onClick = {
                        onclick(index)
                    }
                )
            }
        )
    }
}