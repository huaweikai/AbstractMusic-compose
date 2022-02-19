package com.hua.abstractmusic.ui.home.local.album

import android.annotation.SuppressLint
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
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel


/**
 * @author : huaweikai
 * @Date   : 2022/01/12
 * @Desc   : localalbum
 */
@ExperimentalFoundationApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun LocalAlbum(
    viewModel: HomeViewModel = LocalHomeViewModel.current,
    onClick:(String)->Unit
) {
    AlbumLazyItem(viewModel.localAlbumList.value,onClick)
}
@ExperimentalFoundationApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun AlbumLazyItem(
    list: List<MediaData>,
    onClick: (String) -> Unit
){
    LazyVerticalGrid(
        cells = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(list) {item ->
            Column(
                modifier = Modifier
                    .padding(10.dp),
            ) {
                ArtImage(
                    modifier = Modifier
                        .size(190.dp)
                        .clickable {
                            onClick(item.mediaId)
                        },
                    uri = item.mediaItem.mediaMetadata.artworkUri,
                    transformation = RoundedCornersTransformation(40f),
                    contentScale = ContentScale.Crop,
                    desc = "专辑图"
                )
                TitleAndArtist(
                    title = "${item.mediaItem.mediaMetadata.title}",
                    subTitle = "${item.mediaItem.mediaMetadata.artist}",
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