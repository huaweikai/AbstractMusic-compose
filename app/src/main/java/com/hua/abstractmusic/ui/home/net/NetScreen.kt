package com.hua.abstractmusic.ui.home.net

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.ALL_MUSIC_TYPE
import com.hua.abstractmusic.other.Constant.NET_ALBUM_TYPE
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalNetViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.*
import com.hua.abstractmusic.ui.viewmodels.NetViewModel
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 在线音乐的screen
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun NetScreen(
    navHostController: NavHostController = LocalHomeNavController.current,
    netViewModel: NetViewModel = LocalNetViewModel.current
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

@ExperimentalPagerApi
@Composable
private fun SuccessContent(
    navHostController: NavHostController = LocalHomeNavController.current,
    netViewModel: NetViewModel = LocalNetViewModel.current
) {
    LazyColumn(
        verticalArrangement = Arrangement.Top
    ) {
        item {
            HorizontalBanner(
                netViewModel.bannerList.value.map {
                    it.mediaItem.metadata?.albumArtUri
                }
            ) {

            }
        }
        glide("大家都在听") {
            navHostController.navigate("${Screen.NetDetailScreen.route}?type=$ALL_MUSIC_TYPE")
        }
        item {
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
        }
        glide("推荐歌单", {})
        item {
            NewItems(list = netViewModel.recommendList.value, {

            }, {
                netViewModel.recommendId = it.mediaId
                netViewModel.listInit(it.mediaId)
            })
        }
        glide("最新专辑") {
            navHostController.navigate("${Screen.NetDetailScreen.route}?type=$NET_ALBUM_TYPE")
        }
        item {
            NewItems(list = netViewModel.albumList.value, {

            }, {
                netViewModel.albumId = it.mediaId
                Log.d("TAG", "SuccessContent: ${it.mediaId}")
                netViewModel.listInit(it.mediaId)
            })
        }
    }
}


@Composable
private fun RecommendItem(list: List<MediaData>, page: Int) {
    Column(
        Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        repeat(3) {
            val data = list[page * 3 + it].mediaItem.metadata
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                AlbumArtImage(
                    modifier = Modifier.size(60.dp),
                    uri = data?.albumArtUri,
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

private fun LazyListScope.glide(
    title: String,
    onclick: () -> Unit
) {
    item {
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
}

@Composable
private fun NewItems(
    list: List<MediaData>,
    onclick: (MediaData) -> Unit,
    onPlay: (MediaData) -> Unit
) {
    LazyRow {
        items(list) { item ->
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier
                    .height(IntrinsicSize.Min)
                    .clickable {
                        onclick(item)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    AlbumArtImage(
                        modifier = Modifier.size(100.dp),
                        uri = item.mediaItem.metadata?.albumArtUri,
                        desc = "",
                        transformation = RoundedCornersTransformation(10f)
                    )
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "",
                        modifier = Modifier
                            .align(
                                Alignment.BottomEnd
                            )
                            .size(22.dp)
                            .clickable {
                                onPlay(item)
                            }
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = "${item.mediaItem.metadata?.title}")
                Spacer(modifier = Modifier.height(3.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}