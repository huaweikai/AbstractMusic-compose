package com.hua.abstractmusic.ui.home.local.artist.detail


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.AlbumItem
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.MusicItem
import com.hua.abstractmusic.ui.viewmodels.ArtistDetailViewModel
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/19
 * @Desc   : detail
 */
@SuppressLint("UnsafeOptInUsageError")
@ExperimentalPagerApi
@Composable
fun LocalArtistDetail(
    item: MediaItem,
    viewModel: ArtistDetailViewModel = hiltViewModel()
) {

    DisposableEffect(Unit) {
        viewModel.initializeController()
        viewModel.artistId = item.mediaId
        this.onDispose {
            viewModel.releaseBrowser()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.padding(top = 20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ArtImage(
                modifier = Modifier
                    .size(100.dp),
                uri = item.mediaMetadata.artworkUri,
                desc = "",
                transformation = CircleCropTransformation()
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Text(text = "${item.mediaMetadata.title}")
        }
        ArtistHorizontalPager(viewModel = viewModel, modifier = Modifier.fillMaxSize())
    }
}

@ExperimentalPagerApi
@androidx.media3.common.util.UnstableApi
@Composable
private fun ArtistHorizontalPager(
    viewModel: ArtistDetailViewModel,
    modifier:Modifier,
    homeNavController: NavHostController = LocalHomeNavController.current
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = {
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, it),
                color = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.height(50.dp)
    ) {
        val tabTitles = listOf("音乐", "专辑")
        tabTitles.forEachIndexed { index, s ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.background
                ),
            ) {
                Text(text = tabTitles[index])
            }
        }
    }
    HorizontalPager(
        count = 2,
        state = pagerState,
        verticalAlignment = Alignment.Top,
        modifier = modifier
    ) { index ->
        when (index) {
            0 -> {
                LazyColumn(Modifier.fillMaxHeight().fillMaxWidth()) {
                    itemsIndexed(viewModel.artistDetail.value) { index, item ->
                        MusicItem(data = item,
                            onClick = {
                                viewModel.setPlaylist(index, viewModel.artistDetail.value)
                            }
                        )
                    }
                }
            }
            1 -> {
                LazyColumn(Modifier.fillMaxHeight().fillMaxWidth()) {
                    items(viewModel.artistAlbumDetail.value) { item ->
                        AlbumItem(item = item.mediaItem, modifier = Modifier.fillMaxWidth()) {
                            homeNavController.navigate("${Screen.LocalAlbumDetail.route}?albumId=${item.mediaId}")
                        }
                    }
                }
            }
        }
    }
}