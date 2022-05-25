package com.hua.abstractmusic.ui.home.local.album

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.LocalScreenSize
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.utils.WindowSize
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel


/**
 * @author : huaweikai
 * @Date   : 2022/01/12
 * @Desc   : localalbum
 */
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun LocalAlbum(
    viewModel: HomeViewModel,
    onClick:(MediaItem)->Unit
) {
    val windowSize = LocalScreenSize.current
    val fixSize = remember {
        mutableStateOf(2)
    }
    if (windowSize == WindowSize.Expanded) {
        fixSize.value = 3
    } else if (windowSize == WindowSize.Compact) {
        fixSize.value = 2
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(fixSize.value),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = LocalBottomControllerHeight.current,
            top = 16.dp
        ),
    ) {
        items(viewModel.localAlbumList.value) { item ->
            AlbumItem(item = item.mediaItem, onClick = onClick)
        }
    }
}


@SuppressLint("UnsafeOptInUsageError")
@Composable
fun AlbumItem(
    item: MediaItem,
    onClick: (MediaItem) -> Unit
){
    Column(
        modifier = Modifier
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ArtImage(
            modifier = Modifier
                .size(190.dp)
                .clickable {
                    onClick(item)
                },
            uri = item.mediaMetadata.artworkUri,
            transformation = RoundedCornersTransformation(40f),
            contentScale = ContentScale.Crop,
            desc = "专辑图"
        )
        Spacer(modifier = Modifier.height(8.dp))
        TitleAndArtist(
            title = "${item.mediaMetadata.title}",
            subTitle = "${item.mediaMetadata.artist}",
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(),
            titleStyle = {
                this.copy(textAlign = TextAlign.Center)
            },
            subTitleStyle = {
                this.copy(textAlign = TextAlign.Center)
            }
        )
    }
}