package com.hua.abstractmusic.ui.home.local.album

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.model.music.MediaData


/**
 * @author : huaweikai
 * @Date   : 2022/01/12
 * @Desc   : localalbum
 */
@ExperimentalFoundationApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun LocalAlbum(
    viewModel: HomeViewModel,
    onClick:(MediaItem)->Unit
) {
    AlbumLazyGrid(viewModel.localAlbumList.value, onClick = onClick)
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun AlbumLazyGrid(
    list: List<MediaData>,
    bottomControllerHeight: Dp = LocalBottomControllerHeight.current,
    onClick: (MediaItem) -> Unit,
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            bottom = bottomControllerHeight,
            top = 16.dp
        ),
    ) {
        items(list) { item ->
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