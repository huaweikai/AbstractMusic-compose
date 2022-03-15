package com.hua.abstractmusic.ui.home.detail.artistdetail


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.AlbumItem
import com.hua.abstractmusic.ui.utils.MusicItem
import com.hua.abstractmusic.ui.utils.indicatorOffset
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

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
    homeNavController: NavHostController = LocalHomeNavController.current,
    viewModel: ArtistDetailViewModel = hiltViewModel()
) {
    val state = rememberCollapsingToolbarScaffoldState()
    DisposableEffect(Unit) {
        viewModel.artistId = item.mediaId
        viewModel.initializeController()
        this.onDispose {
            viewModel.releaseBrowser()
        }
    }
    CollapsingToolbarScaffold(
        modifier = Modifier.fillMaxSize(),
        state = state,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbar = {
            val textSize = (18 + (30 - 18) * state.toolbarState.progress).sp
            val titlePadding = (50 * (1 - state.toolbarState.progress)).dp
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                AsyncImage(
                    model = item.mediaMetadata.artworkUri,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    contentDescription = ""
                )
            }
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(bottom = 8.dp)
                    .height(38.dp)
                    .width(38.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        homeNavController.navigateUp()
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
            Text(
                text = "${item.mediaMetadata.title}",
                color = Color.White,
                modifier = Modifier
                    .padding(
                        start = titlePadding,
                        top = 8.dp + rememberInsetsPaddingValues(insets = LocalWindowInsets.current.statusBars).calculateTopPadding(),
                        bottom = 16.dp,
                        end = 16.dp
                    )
                    .road(Alignment.CenterStart, Alignment.BottomEnd),
                fontSize = textSize,
                textAlign = TextAlign.Center
            )
        }
    ) {
        Column {
            ArtistHorizontalPager(
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}

@ExperimentalPagerApi
@androidx.media3.common.util.UnstableApi
@Composable
private fun ArtistHorizontalPager(
    viewModel: ArtistDetailViewModel,
    modifier: Modifier,
    bottomBarHeight: Dp = LocalBottomControllerHeight.current,
    homeNavController: NavHostController = LocalHomeNavController.current
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = {
            TabRowDefaults.Indicator(
                modifier = Modifier.indicatorOffset(pagerState, it, 50.dp),
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
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.background
                    ),
            ) {
                Text(text = tabTitles[index], color = MaterialTheme.colorScheme.onBackground)
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
                LazyColumn(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = bottomBarHeight)
                ) {
                    itemsIndexed(viewModel.artistDetail.value, key = {_, item -> item.mediaId }) { index, item ->
                        MusicItem(data = item,
                            isDetail = true,
                            index = index,
                            onClick = {
                                viewModel.setPlaylist(index, viewModel.artistDetail.value)
                            }
                        )
                    }
                }
            }
            1 -> {
                LazyColumn(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = bottomBarHeight)
                ) {
                    items(viewModel.artistAlbumDetail.value,key = {item -> item.mediaId }) { item ->
                        Spacer(modifier = Modifier.height(8.dp))
                        AlbumItem(
                            item = item.mediaItem,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp)
                        ) {
                            homeNavController.navigate("${Screen.LocalAlbumDetail.route}?albumId=${item.mediaId}")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}