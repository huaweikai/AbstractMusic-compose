package com.hua.abstractmusic.ui.utils

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.media3.common.MediaItem
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.LocalPlayingViewModel
import com.hua.abstractmusic.ui.LocalPopWindow
import com.hua.abstractmusic.ui.LocalPopWindowItem
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel


/**
 * @author : huaweikai
 * @Date   : 2022/01/11
 * @Desc   : item
 */

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun MusicItem(
    data: MediaData,
    modifier: Modifier = Modifier,
    state: MutableState<Boolean> = LocalPopWindow.current,
    nowItem: MutableState<MediaItem> = LocalPopWindowItem.current,
    onClick: () -> Unit,
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable {
                onClick()
            }
    ) {
        val (image, title, more) = createRefs()
        ArtImage(
            modifier = Modifier
                .constrainAs(image) {
                    start.linkTo(parent.start, 10.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .size(50.dp),
            uri = data.mediaItem.mediaMetadata.artworkUri!!,
            transformation = RoundedCornersTransformation(5f),
            desc = "专辑图"
        )
        Column(
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(image.end, 10.dp)
                    top.linkTo(image.top)
                    bottom.linkTo(image.bottom)
                    end.linkTo(more.start, 8.dp)
                    width = Dimension.fillToConstraints
                }
        ) {
            TitleAndArtist(
                title = "${data.mediaItem.mediaMetadata.title}",
                subTitle = "${data.mediaItem.mediaMetadata.artist}",
                color =
                if (data.isPlaying) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_more),
            contentDescription = "",
            modifier = Modifier
                .constrainAs(more) {
                    end.linkTo(parent.end, 8.dp)
                    top.linkTo(parent.top, 3.dp)
                    bottom.linkTo(parent.bottom, 3.dp)
                }
                .clickable {
                    state.value = true
                    nowItem.value = data.mediaItem
                }
        )

    }
}

@Composable
fun ArtImage(
    modifier: Modifier,
    uri: Any?,
    desc: String,
    transformation: Transformation,
    contentScale: ContentScale = ContentScale.Fit,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .apply {
//                placeholder(R.drawable.ic_music_launcher)
                data(uri)
                error(R.drawable.music)
                transformations(transformation)
            }
            .build(),
        contentDescription = desc,
        modifier = modifier,
        contentScale = contentScale
    )
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun PopupWindow(
    state: MutableState<Boolean> = LocalPopWindow.current,
    item: MediaItem = LocalPopWindowItem.current.value,
    config: Configuration = LocalConfiguration.current,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current
) {
    val addMore = remember {
        mutableStateOf(false)
    }
    if (state.value) {
        Dialog(
            onDismissRequest = {
                state.value = false
            }
        ) {
            Column(
                modifier = Modifier
                    .width((config.screenWidthDp * 0.75).dp)
                    .height((config.screenHeightDp * 0.6).dp)
                    .padding(horizontal = 8.dp)
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ArtImage(
                        modifier = Modifier.size(50.dp),
                        uri = item.mediaMetadata.artworkUri,
                        desc = "",
                        transformation = RoundedCornersTransformation(16f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                        TitleAndArtist(
                            title = "${item.mediaMetadata.title}",
                            subTitle = "${item.mediaMetadata.artist}",
                        )
                    }
                }
                Text(text = "添加到歌单")
                Text(text = "添加到下一曲播放",modifier = Modifier.clickable {
                    viewModel.addQueue(item)
                })
            }
        }
    }
    if (addMore.value) {
        Dialog(onDismissRequest = {
            addMore.value = false
        }) {
            Text(text = "${item.mediaMetadata.title},小window")
        }
    }
}