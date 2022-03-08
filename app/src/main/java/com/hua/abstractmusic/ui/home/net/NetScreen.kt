package com.hua.abstractmusic.ui.home.net

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.ALL_MUSIC_TYPE
import com.hua.abstractmusic.other.Constant.NET_ALBUM_TYPE
import com.hua.abstractmusic.ui.LocalComposeUtils
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalNetViewModel
import com.hua.abstractmusic.ui.home.HomeTopBar
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.*
import com.hua.abstractmusic.ui.viewmodels.NetViewModel
import com.hua.abstractmusic.utils.ComposeUtils
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
    navHostController: NavHostController = LocalHomeNavController.current,
    netViewModel: NetViewModel = LocalNetViewModel.current
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                label = "在线音乐",
                actionOnclick = {},
                imageVector = Icons.Default.Search,
                desc = ""
            )
        }
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(netViewModel.screenState.value == LCE.Loading),
            onRefresh = {
                netViewModel.refresh()
            },
            modifier = Modifier.padding(it)
        ) {
            when (netViewModel.screenState.value) {
                LCE.Loading -> {
                    Loading()
                }
                LCE.Error -> {
                    Error {
                        netViewModel.refresh()
                    }
                }
                LCE.Success -> {
                    SuccessContent()
                }
            }
        }
    }

}

@ExperimentalPagerApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun SuccessContent(
    navHostController: NavHostController = LocalHomeNavController.current,
    netViewModel: NetViewModel = LocalNetViewModel.current
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        HorizontalBanner(
            netViewModel.bannerList.value.map {
                it.mediaItem.mediaMetadata.artworkUri
            }
        ) {
            navHostController.navigate(
                "${Screen.LocalAlbumDetail.route}?albumId=${
                    netViewModel.bannerList.value[it].mediaId
                }&isLocal=false"
            )
        }
        Glide("大家都在听") {
            navHostController.navigate("${Screen.NetDetailScreen.route}?type=$ALL_MUSIC_TYPE")
        }
        HorizontalPager(
            count = 2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp),
            contentPadding = PaddingValues(end = 32.dp)
        ) { page ->
            if (netViewModel.musicList.value.isNotEmpty()) {
                RecommendItem(
                    netViewModel.musicList.value,
                    page
                )
            }
        }
        Glide("推荐歌单", {})
        NewItems(list = netViewModel.recommendList.value, {

        }, {
            netViewModel.recommendId = it.mediaId
            netViewModel.listInit(it.mediaId)
        })
        Glide("最新专辑") {
            navHostController.navigate("${Screen.NetDetailScreen.route}?type=$NET_ALBUM_TYPE")
        }
        NewItems(list = netViewModel.albumList.value, {
            navHostController.navigate(
                "${Screen.LocalAlbumDetail.route}?albumId=${
                    it.mediaId
                }&isLocal=false"
            )
        }, {
            netViewModel.albumId = it.mediaId
            netViewModel.listInit(it.mediaId)
        })
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun RecommendItem(list: List<MediaData>, page: Int) {
    Column(
        Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        repeat(3) {
            val data = list[page * 3 + it].mediaItem.mediaMetadata
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                ArtImage(
                    modifier = Modifier.size(60.dp),
                    uri = data.artworkUri,
                    desc = "歌单图",
                    transformation = RoundedCornersTransformation(10f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    TitleAndArtist(title = "${data?.title}", subTitle = "${data?.artist}")
                }
            }
        }
    }
}

@Composable
private fun Glide(
    title: String,
    onclick: () -> Unit
) {
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 18.sp)
        Text(text = "更多 >", fontSize = 18.sp, modifier = Modifier.clickable { onclick() })
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun NewItems(
    list: List<MediaData>,
    onclick: (MediaData) -> Unit,
    onPlay: (MediaData) -> Unit,
    composeUtils: ComposeUtils = LocalComposeUtils.current
) {
    val context = LocalContext.current

    LazyRow {
        items(list) { item ->
            val background = remember(item) {
                mutableStateOf(Color.Gray)
            }
            LaunchedEffect(Unit) {
                val bitmap = composeUtils.coilToBitmap(item.mediaItem.mediaMetadata.artworkUri)
                val pair =
                    PaletteUtils.resolveBitmap(false, bitmap, context.getColor(R.color.black))
                background.value = Color(pair.first)
            }
            Spacer(modifier = Modifier.width(5.dp))
            Column(
                Modifier
                    .height(IntrinsicSize.Min)
                    .clickable {
                        onclick(item)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    ArtImage(
                        modifier = Modifier
                            .fillMaxHeight(0.9f)
                            .aspectRatio(1f),
                        uri = item.mediaItem.mediaMetadata.artworkUri,
                        desc = "",
                        transformation = RoundedCornersTransformation(10f)
                    )
                    ItemPlayButton(
                        modifier = Modifier
                            .padding(start = 5.dp)
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
                Text(text = "${item.mediaItem.mediaMetadata.title}")
            }
            Spacer(modifier = Modifier.width(5.dp))
        }
    }
}

@Composable
private fun ItemPlayButton(
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
                Spacer(modifier = Modifier.width(8.dp))
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