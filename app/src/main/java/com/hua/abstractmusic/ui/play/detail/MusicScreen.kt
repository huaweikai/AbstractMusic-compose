package com.hua.abstractmusic.ui.play.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.utils.album
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title

/**
 * @author : huaweikai
 * @Date   : 2022/01/20
 * @Desc   : view
 */
@Composable
fun MusicScreen(
    viewModel: HomeViewModel
){
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (img,Title,mode,seekBar,controller) = createRefs()
        val imgStart = createGuidelineFromStart(0.1f)
        val imgEnd = createGuidelineFromStart(0.9f)
        val imgTop = createGuidelineFromTop(0.15f)
        val imgBottom = createGuidelineFromTop(0.5f)
        Image(
            painter = rememberImagePainter(data = viewModel.currentItem.value.metadata?.albumArtUri){
                this.transformations(RoundedCornersTransformation(30f))
                this.error(R.drawable.music)
            },
            contentDescription = null,
            modifier = Modifier
                .constrainAs(img) {
                    top.linkTo(imgTop)
                    start.linkTo(imgStart)
                    end.linkTo(imgEnd)
                    bottom.linkTo(imgBottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
//                .aspectRatio(1f)
        )
        Column(modifier = Modifier
            .constrainAs(Title){
                top.linkTo(img.bottom,50.dp)
                start.linkTo(img.start)
                end.linkTo(img.end)
                height = Dimension.preferredValue(40.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Title(
                title = "${viewModel.currentItem.value.metadata?.title}",
                desc = "${viewModel.currentItem.value.metadata?.artist} - ${viewModel.currentItem.value.metadata?.album}"
            )
        }

    }
}

@Composable
private fun Title(
    title:String,
    desc:String
){
    Text(text = title, textAlign = TextAlign.Start)
    Spacer(modifier = Modifier.padding(top = 8.dp))
    Text(text = desc ,textAlign = TextAlign.Start)
}