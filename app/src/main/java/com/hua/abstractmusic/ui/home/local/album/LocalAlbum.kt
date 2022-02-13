package com.hua.abstractmusic.ui.home.local.album

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.utils.AlbumArtImage
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title


/**
 * @author : huaweikai
 * @Date   : 2022/01/12
 * @Desc   : localalbum
 */
@ExperimentalFoundationApi
@Composable
fun LocalAlbum(
    viewModel: HomeViewModel = LocalHomeViewModel.current,
    onClick:(String)->Unit
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(viewModel.localAlbumList.value) {item ->
            Column(
                modifier = Modifier
                    .padding(10.dp),
            ) {
                AlbumArtImage(
                    modifier = Modifier
                        .size(190.dp)
                        .clickable {
                            onClick(item.mediaId)
                        },
                    uri = item.mediaItem.metadata?.albumArtUri,
                    transformation = RoundedCornersTransformation(40f),
                    contentScale = ContentScale.Crop,
                    desc = "专辑图"
                )
                TitleAndArtist(
                    title = "${item.mediaItem.metadata?.title}",
                    subTitle = "${item.mediaItem.metadata?.artist}",
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth(),
                    titleStyle = {
                        this.copy(textAlign = TextAlign.Center)
                    },
                    subTitleStyle = {
                        this.copy(textAlign = TextAlign.Center)
                    }
                )
            }
        }
    }
}