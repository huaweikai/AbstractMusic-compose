package com.hua.abstractmusic.ui.utils

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation
import com.airbnb.lottie.compose.*
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.LOCAL_ALBUM_ID
import com.hua.abstractmusic.other.Constant.LOCAL_ARTIST_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ALBUM_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ARTIST_ID
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalPlayingViewModel
import com.hua.abstractmusic.ui.LocalPopWindow
import com.hua.abstractmusic.ui.LocalPopWindowItem
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.hua.abstractmusic.utils.isLocal
import kotlinx.coroutines.launch


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
    isDetail: Boolean = false,
    index: Int = 0,
    state: MutableState<Boolean> = LocalPopWindow.current,
    nowItem: MutableState<MediaItem> = LocalPopWindowItem.current,
    onMoreClick: () -> Unit = {
        state.value = true
        nowItem.value = data.mediaItem
    },
    onClick: () -> Unit
) {
    val playing by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.playing)
    )
    val process by animateLottieCompositionAsState(
        composition = playing,
        iterations = LottieConstants.IterateForever
    )
    Surface(
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
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
            val idEnd = createGuidelineFromStart(60.dp)
            Column(
                modifier = Modifier.constrainAs(image) {
                    start.linkTo(parent.start, 10.dp)
                    end.linkTo(idEnd)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (data.isPlaying) {
                    LottieAnimation(
                        composition = playing,
                        progress = process,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(60.dp),
                    )
                } else {
                    if (isDetail) {
                        Text(
                            text = "$index",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Thin,
//                            color = MaterialTheme.colorScheme.onBackground
                        )
                    } else {
                        ArtImage(
                            modifier = Modifier.size(50.dp),
                            uri = data.mediaItem.mediaMetadata.artworkUri!!,
                            transformation = RoundedCornersTransformation(5f),
                            desc = "专辑图"
                        )
                    }
                }

            }
            Column(
                modifier = Modifier
                    .constrainAs(title) {
                        start.linkTo(idEnd, 10.dp)
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
                        onMoreClick()
                    },
//                tint = MaterialTheme.colorScheme.onBackground
            )

        }
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

@Composable
fun CoilImage(
    url: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha,
    shape: Shape? = null,
    colorFilter: ColorFilter? = null,
    builder: ImageRequest.Builder.() -> Unit = {},
    onSuccess: (() -> Unit)? = null,
    onError: @Composable () -> Unit = {
    },
    onLoading: @Composable () -> Unit = {
        CircularProgressIndicator()
    }
) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        model =
        ImageRequest.Builder(context)
            .data(url)
            .apply {
                builder()
            }
            .build()
    )
    Box(
        modifier = modifier then if (shape != null) Modifier.clip(shape) else Modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter, contentDescription = contentDescription,
            modifier = Modifier.matchParentSize(),
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha, colorFilter = colorFilter
        )
        Crossfade(targetState = painter.state) { state ->
            when (state) {
                is AsyncImagePainter.State.Empty -> {

                }
                is AsyncImagePainter.State.Loading -> {
                    onLoading()
                }
                is AsyncImagePainter.State.Success -> {
                    onSuccess?.invoke()
                }
                is AsyncImagePainter.State.Error -> {
                    onError()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun PopupWindow(
    state: MutableState<Boolean> = LocalPopWindow.current,
    item: MediaItem = LocalPopWindowItem.current.value,
    config: Configuration = LocalConfiguration.current,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current,
    homeNavController: NavHostController = LocalHomeNavController.current
) {
    val addMore = remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    if (state.value) {
        Dialog(
            onDismissRequest = {
                state.value = false
            }
        ) {
            Column(
                modifier = Modifier
                    .width((config.screenWidthDp * 0.75).dp)
                    .heightIn(max = (config.screenHeightDp * 0.6).dp)
                    .padding(horizontal = 8.dp)
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    ArtImage(
                        modifier = Modifier.size(80.dp),
                        uri = item.mediaMetadata.artworkUri,
                        desc = "",
                        transformation = RoundedCornersTransformation(16f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                        TitleAndArtist(
                            title = "${item.mediaMetadata.title}",
                            subTitle = "${item.mediaMetadata.artist}",
                            height = 4.dp
                        )
                    }
                }
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                PopItem(desc = "添加到播放队列") {
                    viewModel.addQueue(item)
                }
                PopItem(desc = "添加到下一曲播放") {
                    viewModel.addQueue(item, true)
                }
                PopItem(desc = "添加到歌单") {
                    viewModel.refresh()
                    state.value = false
                    addMore.value = true
                }
                PopItem(desc = "歌手:${item.mediaMetadata.artist}") {
                    val artistId: Long = item.mediaMetadata.extras?.getLong("artistId") ?: 0L
                    val parentId = if (item.mediaId.isLocal()) {
                        Uri.parse(LOCAL_ARTIST_ID).buildUpon().appendPath(artistId.toString())
                    } else {
                        Uri.parse(NETWORK_ARTIST_ID).buildUpon().appendPath(artistId.toString())
                    }
                    homeNavController.navigate("${Screen.LocalArtistDetail.route}?artistId=${parentId}")
                    state.value = false
                }
                PopItem(desc = "专辑:${item.mediaMetadata.albumTitle}") {
                    val albumId = item.mediaMetadata.extras?.getLong("albumId") ?: 0L
                    val parentId = if (item.mediaId.isLocal()) {
                        Uri.parse(LOCAL_ALBUM_ID).buildUpon().appendPath(albumId.toString())
                    } else {
                        Uri.parse(NETWORK_ALBUM_ID).buildUpon().appendPath(albumId.toString())
                    }
                    homeNavController.navigate("${Screen.LocalAlbumDetail.route}?albumId=${parentId}")
                    state.value = false
                }
            }
        }
    }
    if (addMore.value) {
        Dialog(onDismissRequest = {
            addMore.value = false
        }) {
            val sheets = if (item.mediaId.isLocal()) {
                viewModel.localSheetList.value
            } else {
                viewModel.netSheetList.value
            }
            Column(
                modifier = Modifier
                    .width((config.screenWidthDp * 0.75).dp)
                    .heightIn(max = (config.screenHeightDp * 0.4).dp)
                    .padding(horizontal = 8.dp)
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "歌单", fontSize = 22.sp, modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(Modifier.fillMaxWidth()) {
                    items(sheets) { sheet ->
                        PopItem(desc = "${sheet.mediaItem.mediaMetadata.title}") {
                            scope.launch {
                                viewModel.insertMusicToSheet(
                                    mediaItem = item,
                                    parentId = sheet.mediaId
                                )
                                addMore.value = false
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopItem(
    desc: String,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(50.dp)
            .background(
                MaterialTheme.colorScheme.background,
                RoundedCornerShape(8.dp)
            )
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = desc,
            fontSize = 16.sp,
            maxLines = 1,
            textAlign = TextAlign.Start,
        )
    }
}