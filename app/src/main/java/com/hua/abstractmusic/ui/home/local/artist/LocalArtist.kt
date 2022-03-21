package com.hua.abstractmusic.ui.home.local.artist

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import coil.transform.CircleCropTransformation
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel

/**
 * @author : huaweikai
 * @Date   : 2022/01/18
 * @Desc   :
 */
@Composable
fun LocalArtist(
    homeViewModel: HomeViewModel,
    bottomControllerHeight: Dp = LocalBottomControllerHeight.current,
    onClick: (MediaItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(bottom = bottomControllerHeight)
    ) {
        items(homeViewModel.localArtistList.value,
            key = { item -> item.mediaId }
        ) { mediaData->
            ArtistLazyItem(item = mediaData.mediaItem, onClick = onClick)
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun ArtistLazyItem(
    item: MediaItem,
    onClick: (MediaItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, bottom = 20.dp)
            .clickable {
                onClick(item)
            }
    ) {
        ArtImage(
            modifier = Modifier
                .size(45.dp),
            uri = item.mediaMetadata.artworkUri,
            transformation = CircleCropTransformation(),
            desc = "歌手图像"
        )
        Spacer(modifier = Modifier.padding(horizontal = 5.dp))
        Column(
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            TitleAndArtist(
                title = "${item.mediaMetadata.title}",
                subTitle = "${item.mediaMetadata.trackNumber} 首"
            )
        }
    }
}