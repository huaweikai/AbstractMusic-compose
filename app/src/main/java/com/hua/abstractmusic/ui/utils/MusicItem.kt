package com.hua.abstractmusic.ui.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.media3.common.MediaItem
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation
import com.airbnb.lottie.compose.*
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.LocalPopWindow
import com.hua.abstractmusic.ui.LocalPopWindowItem


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
        LottieCompositionSpec.RawRes(R.raw.playing2)
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
                            .padding(8.dp)
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
                        CoilImage(
                            modifier = Modifier.size(50.dp),
                            url = data.mediaItem.mediaMetadata.artworkUri,
                            builder = {
                                transformations(RoundedCornersTransformation(5f))
                            },
                            contentDescription = "专辑图",
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
