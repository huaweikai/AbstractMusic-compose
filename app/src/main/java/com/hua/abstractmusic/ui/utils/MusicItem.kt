package com.hua.abstractmusic.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
import com.hua.abstractmusic.ui.LocalPopWindow
import com.hua.abstractmusic.ui.LocalPopWindowItem


/**
 * @author : huaweikai
 * @Date   : 2022/01/11
 * @Desc   : item
 */
@androidx.media3.common.util.UnstableApi
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
                placeholder(R.drawable.ic_music_launcher)
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

@androidx.media3.common.util.UnstableApi
@Composable
fun PopupWindow(
    state: MutableState<Boolean> = LocalPopWindow.current,
    item: MediaItem = LocalPopWindowItem.current.value
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
            Text(text = "${item.mediaMetadata.title}", modifier = Modifier.clickable {
                state.value = false
                addMore.value = true
            })
        }
    }
    if(addMore.value){
        Dialog(onDismissRequest = {
            addMore.value = false
        }) {
            Text(text = "${item.mediaMetadata.title},小window")
        }
    }
}