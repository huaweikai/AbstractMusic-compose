package com.hua.abstractmusic.ui.home.local.music

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.utils.MusicItem
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.model.music.MediaData


/**
 * @author : huaweikai
 * @Date   : 2022/01/11
 * @Desc   : localmusic
 */
@Composable
fun LocalMusic(
    modifier: Modifier,
    viewModel: HomeViewModel,
    bottomControllerHeight: Dp = LocalBottomControllerHeight.current,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = bottomControllerHeight)
    ) {
        items(
            count = viewModel.localMusicList.value.size,
            itemContent = { index->
                MusicItem(
                    data = viewModel.localMusicList.value[index],
                    onClick = {
                        viewModel.setPlayList(index, viewModel.localMusicList.value.map { it.mediaItem })
                    }
                )
            }
        )
    }
}