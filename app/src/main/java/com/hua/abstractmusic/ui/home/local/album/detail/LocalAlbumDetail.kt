package com.hua.abstractmusic.ui.home.local.album.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.MusicItem
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.viewmodels.AlbumDetailViewModel


/**
 * @author : huaweikai
 * @Date   : 2022/01/13
 * @Desc   : detail
 */

@ExperimentalFoundationApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun LocalAlbumDetail(
    item: MediaItem,
    detailViewModel: AlbumDetailViewModel = hiltViewModel()
) {

    DisposableEffect(Unit) {
        detailViewModel.initializeController()
        detailViewModel.id = item.mediaId
        this.onDispose {
            detailViewModel.releaseBrowser()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            AlbumDetailDesc(item = item)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PlayIcon(
                    modifier = Modifier
                        .weight(1f),
                    desc = "播放全部"
                ) {
                    detailViewModel.setPlaylist(0, detailViewModel.albumDetail.value)
                }
                PlayIcon(
                    modifier = Modifier
                        .weight(1f),
                    desc = "随机播放"
                ) {
                    //todo: 随机播放
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
        itemsIndexed(detailViewModel.albumDetail.value) { index, item ->
            MusicItem(
                data = item,
                onClick = {
                    detailViewModel.setPlaylist(index, detailViewModel.albumDetail.value)
                }
            )
        }
        item {
            AlbumDetailTail(item = item)
        }
    }

}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun AlbumDetailDesc(
    item: MediaItem
) {
    Spacer(modifier = Modifier.height(20.dp))
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(top = 20.dp, end = 20.dp)
    ) {
        ArtImage(
            modifier = Modifier
                .padding(start = 10.dp)
                .size(120.dp),
            uri = item.mediaMetadata.artworkUri,
            desc = "",
            transformation = RoundedCornersTransformation(10f)
        )
        Column(
            modifier = Modifier
                .padding(start = 10.dp, end = 5.dp)
                .fillMaxHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            TitleAndArtist(
                title = "${item.mediaMetadata.title}",
                subTitle = "${item.mediaMetadata.artist}",
                titleStyle = {
                    this.copy(fontSize = 22.sp)
                },
                subTitleStyle = {
                    this.copy(fontSize = 16.sp)
                },
                height = 5.dp
            )
        }
    }
    Spacer(modifier = Modifier.height(40.dp))
}

@Composable
fun PlayIcon(
    modifier: Modifier = Modifier,
    desc: String,
    onclick: () -> Unit,
) {
    IconButton(
        onClick = {
            onclick()
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .fillMaxHeight()
            .background(
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mode_play),
                contentDescription = "",
                tint = Color.White,
            )
            Text(text = desc, color = MaterialTheme.colorScheme.background)
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun AlbumDetailTail(
    item: MediaItem
) {
    Column(
        modifier = Modifier.padding(start = 10.dp)
    ) {
        val year = item.mediaMetadata.releaseYear
        val yearText = if (year != null && year > 0L) {
            "$year"
        } else {
            "-"
        }
        TitleAndArtist(
            title = "发行年份: $yearText",
            subTitle = "歌曲数量: ${item.mediaMetadata.trackNumber}",
            subTitleStyle = {
                this.copy(fontWeight = W400, fontSize = 14.sp)
            },
            height = 5.dp
        )
    }
}