package com.hua.abstractmusic.ui.home.local.album.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.abstractmusic.ui.utils.MusicItem
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.viewmodels.AlbumDetailViewModel


/**
 * @author : huaweikai
 * @Date   : 2022/01/13
 * @Desc   : detail
 */

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun LocalAlbumDetail(
    item: MediaItem,
    isLocal: Boolean = true,
    navHostController: NavHostController = LocalHomeNavController.current,
    detailViewModel: AlbumDetailViewModel = hiltViewModel()
) {

    DisposableEffect(Unit) {
        detailViewModel.id = item.mediaId
        detailViewModel.isLocal = isLocal
        detailViewModel.initializeController()
        this.onDispose {
            detailViewModel.releaseBrowser()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            Modifier.fillMaxSize(),
            topBar = {
                SmallTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navHostController.navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                        }
                    },
                    title = {
                        Text(text = "${item.mediaMetadata.title}")
                    },
                    modifier = Modifier.padding(top = 42.dp),
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) {
            if (isLocal) {
                Album_Success(item = item, detailViewModel = detailViewModel)
            } else {
                if (detailViewModel.screenState.value != LCE.Success && detailViewModel.albumDetail.value.isEmpty()) {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Album_Success(item = item, detailViewModel = detailViewModel)
                }
            }
        }
        Box(
            Modifier
                .fillMaxSize()
                .alpha(0.3f)
                .blur(35.dp, BlurredEdgeTreatment.Unbounded),
        ) {
            AsyncImage(
                model = item.mediaMetadata.artworkUri,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxHeight(0.4f)
                    .fillMaxWidth(),
            )
        }
    }
}


@Composable
fun Album_Success(
    item: MediaItem,
    detailViewModel: AlbumDetailViewModel
) {
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            AlbumDetailDesc(item = item)
        }
        itemsIndexed(detailViewModel.albumDetail.value) { index, item ->
            MusicItem(
                data = item,
                isDetail = true,
                index = index,
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
                .padding(start = 20.dp)
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
            if (item.mediaMetadata.subtitle?.isNotBlank() == true) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "${item.mediaMetadata.subtitle}", maxLines = 2)
            }
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