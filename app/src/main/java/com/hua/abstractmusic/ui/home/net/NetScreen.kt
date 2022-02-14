package com.hua.abstractmusic.ui.home.net

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.ALL_MUSIC_TYPE
import com.hua.abstractmusic.other.Constant.NET_ALBUM_TYPE
import com.hua.abstractmusic.other.Constant.TYPE_ALBUM
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalNetViewModel
import com.hua.abstractmusic.ui.home.local.artist.detail.interval
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.AlbumArtImage
import com.hua.abstractmusic.ui.utils.HorizontalBanner
import com.hua.abstractmusic.ui.utils.TitleAndArtist
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.viewmodels.NetViewModel
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.art
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
    if (netViewModel.state.value) {
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
            glide("大家都在听"){
                navHostController.navigate("${Screen.NetDetailScreen.route}?type=$ALL_MUSIC_TYPE")
            }
            item {
                if (netViewModel.musicList.value.isNotEmpty()) {
                    HorizontalPager(
                        count = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp),
                        contentPadding = PaddingValues(end = 32.dp)
                    ) { page ->
                        RecommendItem(
                            netViewModel.musicList.value,
                            page
                        )
                    }
                }
            }
            glide("推荐歌单", {})
            item {
                NewItems(list = netViewModel.recommendList.value)
            }
            glide("最新专辑"){
                navHostController.navigate("${Screen.NetDetailScreen.route}?type=$NET_ALBUM_TYPE")
            }
            item {
                if (netViewModel.albumList.value.isNotEmpty()) {
                    NewItems(list = netViewModel.albumList.value)
                }
            }
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
    list: List<MediaData>
) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        repeat(5) {
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                Modifier.height(IntrinsicSize.Min),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    AlbumArtImage(
                        modifier = Modifier.size(100.dp),
                        uri = list[it].mediaItem.metadata?.albumArtUri,
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
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = "${list[it].mediaItem.metadata?.title}")
                Spacer(modifier = Modifier.height(3.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}