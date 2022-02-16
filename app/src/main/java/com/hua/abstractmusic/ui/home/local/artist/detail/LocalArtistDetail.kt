package com.hua.abstractmusic.ui.home.local.artist.detail


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media2.common.MediaItem
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.viewmodels.ArtistDetailViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.*
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.title
import com.hua.abstractmusic.utils.trackCount
import com.hua.abstractmusic.utils.trackNumber

/**
 * @author : huaweikai
 * @Date   : 2022/01/19
 * @Desc   : detail
 */

@Composable
fun LocalArtistDetail(
    item: MediaItem,
    homeNavController: NavHostController = LocalHomeNavController.current,
    viewModel: ArtistDetailViewModel = hiltViewModel()
) {
    DisposableEffect(Unit) {
//        viewModel.initializeController()
        viewModel.artistId = item.metadata?.mediaId ?: ""
        this.onDispose {
            viewModel.releaseBrowser()
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.padding(top = 20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AlbumArtImage(
                    modifier = Modifier
                        .size(100.dp),
                    uri = item.metadata?.albumArtUri,
                    desc = "",
                    transformation = CircleCropTransformation()
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text(text = "${item.metadata?.title}")
            }
        }
        interval(desc = "歌曲")
        artItem(
            viewModel.screenState.value,
            item.metadata?.trackCount!!.toInt(),
            viewModel.artistDetail.value,
            {
                AnimateAlbumEmptyItem()
            }, { index, item ->
                MusicItem(data = item) {
                    viewModel.setPlaylist(index, viewModel.artistDetail.value)
                }
            }
        )
        interval(desc = "专辑")
        artItem(
            viewModel.screenState.value,
            item.metadata?.trackNumber!!.toInt(),
            viewModel.artistAlbumDetail.value,
            {
                AnimateAlbumItem(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 12.dp)
                )
            },
            { _, item ->
                AlbumItem(
                    item = item.mediaItem,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 12.dp)
                ) {
                    homeNavController.navigate("${Screen.LocalAlbumDetail.route}?albumId=${item.mediaId}")
                }
            }
        )
    }
}

fun LazyListScope.interval(
    desc: String
) {
    item {
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Text(text = desc, Modifier.padding(start = 10.dp))
        Spacer(modifier = Modifier.padding(top = 10.dp))
    }
}

fun LazyListScope.artItem(
    state: LCE,
    loadingCount: Int,
    items: List<MediaData>,
    loadingItem: @Composable () -> Unit,
    content: @Composable LazyItemScope.(Int, MediaData) -> Unit
) {
    if(state == LCE.Loading){
        items(loadingCount) {
            loadingItem()
        }
    }else if(state == LCE.Success){
        itemsIndexed(items) { index, item ->
            content(index, item)
        }
    }
}