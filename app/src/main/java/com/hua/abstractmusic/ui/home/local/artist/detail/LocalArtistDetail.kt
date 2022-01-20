package com.hua.abstractmusic.ui.home.local.artist.detail

import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.media2.common.MediaItem
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.ui.home.MusicItem
import com.hua.abstractmusic.ui.home.local.album.LocalAlbum
import com.hua.abstractmusic.ui.home.local.album.detail.LocalAlbumDetail
import com.hua.abstractmusic.ui.home.viewmodels.ArtistDetailViewModel
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.AlbumItem
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title

/**
 * @author : huaweikai
 * @Date   : 2022/01/19
 * @Desc   : detail
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocalArtistDetail(
    item: MediaItem,
    homeNavController: NavHostController,
    viewModel: ArtistDetailViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current
    DisposableEffect(Unit) {
        val observer = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
                viewModel.initializeController()
                viewModel.artistId = item.metadata?.mediaId ?: ""
            }
        }
        lifecycle.lifecycle.addObserver(observer)
        this.onDispose {
            lifecycle.lifecycle.removeObserver(observer)
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
                Image(
                    painter = rememberImagePainter(
                        data = item.metadata?.albumArtUri
                    ){
                        this.transformations(CircleCropTransformation())
                     this.error(R.drawable.music)
                    },
                    contentDescription = "",
                    modifier = Modifier
                        .size(100.dp)
                )
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Text(text = "${item.metadata?.title}")
            }
        }
        item {
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Text(text = "歌曲",Modifier.padding(start = 10.dp))
            Spacer(modifier = Modifier.padding(top = 10.dp))
        }
        item {
            if(!viewModel.state.value){
                Row(
                    modifier = Modifier
                        .height(70.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.CenterVertically
                        )
                    )
                }
            }
        }
        itemsIndexed(viewModel.artistDetail.value){index, item ->
            MusicItem(data = item) {
                viewModel.setPlaylist(index,viewModel.artistDetail.value)
            }
        }
        item {
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Text(text = "专辑",Modifier.padding(start = 10.dp))
            Spacer(modifier = Modifier.padding(top = 10.dp))
        }
        item {
            if(!viewModel.state.value){
                Row(
                    modifier = Modifier
                        .height(70.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.CenterVertically
                        )
                    )
                }
            }
        }
        itemsIndexed(viewModel.artistAlbumDetail.value){index, item ->
            AlbumItem(
                item = item.mediaItem,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, bottom = 12.dp)
            ){
                homeNavController.navigate("${Screen.LocalAlbumDetail.route}?albumId=${item.mediaId}")
            }
        }
    }
}