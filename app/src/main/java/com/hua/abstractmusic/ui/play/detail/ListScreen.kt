package com.hua.abstractmusic.ui.play.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title


/**
 * @author : huaweikai
 * @Date   : 2022/01/22
 * @Desc   :
 */
@Composable
fun ListScreen(
    viewModel: HomeViewModel = LocalHomeViewModel.current
) {
    Column(
        modifier = Modifier
            .padding(top = 90.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            itemsIndexed(viewModel.currentPlayList.value) { index, item ->
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .background(
                            if(item.isPlaying) Color(0xfff2f2f2) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            viewModel.skipTo(index)
                        }
                        .height(80.dp)
                ) {
                    val (img, title) = createRefs()
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .apply {
                                data( item.mediaItem.metadata?.albumArtUri)
                                error(R.drawable.music)
                                transformations(RoundedCornersTransformation(30f))
                            }
                            .build(),
                        contentDescription = "",
                        modifier = Modifier
                            .constrainAs(img) {
                                start.linkTo(parent.start, 8.dp)
                                bottom.linkTo(parent.bottom)
                                top.linkTo(parent.top)
                            }
                            .size(70.dp)
                    )
                    Column(
                        modifier = Modifier
                            .constrainAs(title) {
                                start.linkTo(img.end, 8.dp)
                                top.linkTo(img.top)
                                bottom.linkTo(img.bottom)
                                end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                                height = Dimension.fillToConstraints
                            },
                        verticalArrangement = Arrangement.Center
                    ) {
                        TitleAndArtist(
                            title = "${item.mediaItem.metadata?.title}",
                            subTitle = "${item.mediaItem.metadata?.artist}"
                        )
                    }
                }
            }
        }
    }

}