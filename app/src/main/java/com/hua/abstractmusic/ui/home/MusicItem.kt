package com.hua.abstractmusic.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.W200
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.utils.TitleAndArtist
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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .apply {
                    data(data.mediaItem.metadata?.albumArtUri)
                    error(R.drawable.music)
                    transformations(RoundedCornersTransformation(5f))
                }
                .build(),
            contentDescription = "专辑图",
            modifier = Modifier
                .constrainAs(image) {
                    start.linkTo(parent.start, 10.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .size(50.dp)
        )
/*        Image(
            painter = rememberImagePainter(
                data = data.mediaItem.metadata?.albumArtUri
            ) {
                this.transformations(
                    RoundedCornersTransformation(5f)
                )
                this.error(R.drawable.music)
            },
            contentDescription = "专辑图",
            modifier = Modifier
                .constrainAs(image) {
                    start.linkTo(parent.start, 10.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .size(50.dp)
        )*/
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
                artist = data.mediaItem.metadata?.artist?:"<unknown>",
                color = if(data.isPlaying) Color(0xff77D3D0) else Color.Black
            )
        }
    }
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(80.dp)
//            .padding(start = 20.dp)
//            .clickable {
//                onClick()
//            }
//    ) {
//        Image(
//            painter = rememberImagePainter(
//                data = data.mediaItem.metadata?.albumArtUri
//            ) {
//                this.transformations(
//                    RoundedCornersTransformation(5f)
//                )
//                this.error(R.drawable.music)
//            },
//            contentDescription = "专辑图",
//            modifier = Modifier
//                .size(50.dp)
//                .fillMaxHeight()
//        )
//        Text(
//            text = "${data.mediaItem.metadata?.title}",
//            modifier = Modifier
//                .fillMaxHeight()
//                .padding(start = 8.dp),
//            color = if(data.isPlaying) Color(0xff77D3D0) else Color.Black
//        )
//    }
}