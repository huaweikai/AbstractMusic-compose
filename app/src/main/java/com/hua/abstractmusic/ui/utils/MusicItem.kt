package com.hua.abstractmusic.ui.utils

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.utils.*


/**
 * @author : huaweikai
 * @Date   : 2022/01/11
 * @Desc   : item
 */
@Composable
fun MusicItem(
    data:MediaData,
    modifier: Modifier = Modifier,
    onClick:()->Unit
){
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable {
                onClick()
            }
    ) {
        val (image,title) = createRefs()
        AlbumArtImage(
            modifier = Modifier
                .constrainAs(image) {
                    start.linkTo(parent.start, 10.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .size(50.dp),
            uri = data.mediaItem.metadata?.albumArtUri!!,
            transformation = RoundedCornersTransformation(5f),
            desc = "专辑图"
        )
        Column(
            modifier = Modifier
                .constrainAs(title){
                    start.linkTo(image.end,10.dp)
                    top.linkTo(image.top)
                    bottom.linkTo(image.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        ) {
            TitleAndArtist(
                title = data.mediaItem.metadata?.title?:"",
                subTitle = data.mediaItem.metadata?.artist?:"<unknown>",
                color =
                if(data.isPlaying) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun AlbumArtImage(
    modifier: Modifier,
    uri:Any?,
    desc:String,
    transformation: Transformation,
    contentScale:ContentScale = ContentScale.Fit,
){
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