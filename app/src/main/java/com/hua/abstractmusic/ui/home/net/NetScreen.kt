package com.hua.abstractmusic.ui.home.net

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.navigation.NavHostController
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.net.HomeBean
import com.hua.abstractmusic.bean.toNavType
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.LocalComposeUtils
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.*
import com.hua.abstractmusic.ui.viewmodels.NetViewModel
import com.hua.abstractmusic.utils.PaletteUtils


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 在线音乐的screen
 */
@OptIn(ExperimentalPagerApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun NetScreen(
    netViewModel: NetViewModel = hiltViewModel(),
    navHostController: NavHostController = LocalAppNavController.current
) {

    val appBarColors = TopAppBarDefaults.smallTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface
    )

    val state = netViewModel.screenState.collectAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(text = "在线音乐")
                },
                modifier = Modifier.statusBarsPadding(),
                colors = appBarColors,
                actions = {
                    IconButton(onClick = {
                        navHostController.navigate(Screen.NetSearchScreen.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = ""
                        )
                    }
                },
            )
        },
    ) {
        val homeBean = netViewModel.homeData.collectAsState()
        SwipeRefresh(
            state = rememberSwipeRefreshState(state.value == LCE.Loading),
            onRefresh = {
                netViewModel.refresh()
            },
            modifier = Modifier
                .padding(it)
        ) {
            when (state.value) {
                LCE.Loading -> {
                    if (homeBean.value.isEmpty()) {
                        Loading()
                    } else {
                        SuccessContent(netViewModel = netViewModel)
                    }
                }
                LCE.Error -> {
                    Error {
                        netViewModel.refresh()
                    }
                }
                LCE.Success -> {
                    SuccessContent(netViewModel)
                }
            }
        }
    }

}

@ExperimentalPagerApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun SuccessContent(
    netViewModel: NetViewModel,
    navHostController: NavHostController = LocalAppNavController.current,
) {
    val height = LocalBottomControllerHeight.current
    val data = netViewModel.homeData.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = LocalBottomControllerHeight.current.coerceAtLeast(16.dp)
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HorizontalBanner(data.value.banners?.map { it.mediaMetadata.artworkUri }
                ?: emptyList()) {
                navHostController.navigate(
                    "${Screen.AlbumDetailScreen.route}?mediaItem=${
                        data.value.banners?.get(it)?.toNavType()
                    }"
                )
            }
        }
        item {
            RecommendSongList(data.value)
        }
        item {
            RecommendSheetList(
                netViewModel = netViewModel, data = data.value,
                navHostController = navHostController
            )
        }
        item {
            RecommendAlbum(
                netViewModel = netViewModel,
                data = data.value,
                navHostController = navHostController
            )
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
private fun RecommendSongList(
    data: HomeBean
) {
    RecommendTitle(
        recommendTitle = "大家都在听~",
        button = {
            IconButton(
                onClick = { },
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .height(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "",
                        modifier = Modifier.size(16.dp)
                    )
                    Text(text = "播放", fontSize = 10.sp)
                }
            }
        }
    ) {
        HorizontalPager(
            count = 2,
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 32.dp)
        ) { page ->
            if (data.songs?.isNotEmpty() == true) {
                SongItems(
                    data.songs,
                    page
                )
            }
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun RecommendSheetList(
    data: HomeBean,
    netViewModel: NetViewModel,
    navHostController: NavHostController
) {
    RecommendTitle(recommendTitle = "最新歌单请查收~") {
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
            if (data.sheets?.isNotEmpty() == true) {
                items(6) { index ->
                    Column(Modifier.padding(end = 16.dp)) {
                        repeat(2) {
                            val i = if (it == 0) index else index + 6
                            val item = data.sheets[i]
                            RecommendItem(item = item, onclick = {
                                navHostController.navigate("${Screen.SheetDetailScreen.route}?mediaItem=${it.toNavType()}")
                            }) {
//                                netViewModel.recommendId = it.mediaId
                                netViewModel.listPlay(it.mediaId)
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun RecommendAlbum(
    data: HomeBean,
    netViewModel: NetViewModel,
    navHostController: NavHostController
) {
    RecommendTitle(recommendTitle = "最新专辑") {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(data.albums ?: emptyList()) {
                Column(modifier = Modifier.padding(end = 16.dp)) {
                    RecommendItem(item = it, onclick = {
                        navHostController.navigate(
                            "${Screen.AlbumDetailScreen.route}?mediaItem=${
                                it.toNavType()
                            }"
                        )
                    }, onPlay = {
                        netViewModel.listPlay(it.mediaId)
                    })
                }
            }
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun RecommendItem(
    item: MediaItem,
    onclick: (MediaItem) -> Unit,
    onPlay: (MediaItem) -> Unit
) {
    val composeUtils = LocalComposeUtils.current
    val context = LocalContext.current
    val background = remember(item) {
        mutableStateOf(Color.Gray)
    }
    LaunchedEffect(Unit) {
        val bitmap =
            composeUtils.coilToBitmap(item.mediaMetadata.artworkUri)
        val pair =
            PaletteUtils.resolveBitmap(
                false,
                bitmap,
                context.getColor(R.color.black)
            )
        background.value = Color(pair.first)
    }
    Column(
        Modifier
            .width(120.dp)
            .height(180.dp)
            .clickable {
                onclick(item)
            },
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .height(130.dp),
        ) {
            CoilImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                url = item.mediaMetadata.artworkUri,
                contentDescription = "",
                builder = { transformations(RoundedCornersTransformation(20f)) }
            )
            ItemPlayButton(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(
                        Alignment.BottomStart
                    )
                    .background(
                        background.value, RoundedCornerShape(50f)
                    )
            ) {
                onPlay(item)
            }
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "${item.mediaMetadata.title}",
            maxLines = 2,
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
private fun RecommendTitle(
    recommendTitle: String,
    button: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            Modifier
                .height(IntrinsicSize.Min)
                .padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = recommendTitle,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            button()
        }
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }

}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun SongItems(list: List<MediaItem>, page: Int) {
    Column(
        Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        repeat(3) {
            val data = list[page * 3 + it].mediaMetadata
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                CoilImage(
                    modifier = Modifier.size(56.dp),
                    url = data.artworkUri,
                    contentDescription = "",
                    builder = {
                        transformations(RoundedCornersTransformation(20f))
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    TitleAndArtist(
                        title = "${data.title}",
                        subTitle = "${data.artist}",
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ItemPlayButton(
    modifier: Modifier,
    onclick: () -> Unit
) {
    IconButton(
        modifier = modifier
            .height(26.dp),
        onClick = {
            onclick()
        }
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = "播放", fontSize = 12.sp)
            }
        }
    }
}