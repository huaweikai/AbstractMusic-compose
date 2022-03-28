package com.hua.abstractmusic.ui.home.local

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.hua.abstractmusic.bean.toNavType
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.home.local.album.LocalAlbum
import com.hua.abstractmusic.ui.home.local.artist.LocalArtist
import com.hua.abstractmusic.ui.home.local.music.LocalMusic
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.indicatorOffset3
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import kotlinx.coroutines.launch


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 本地音乐screen
 */

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun LocalScreen(
    homeNavController: NavHostController = LocalAppNavController.current,
    localViewModel: HomeViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState()
    val tabTitles = listOf("音乐", "专辑", "歌手")
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
                SmallTopAppBar(
                    modifier = Modifier
                        .statusBarsPadding(),
                    title = { Text("本地音乐") },
                    actions = {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "",
                            modifier = Modifier.padding(end = 16.dp).clickable {
                                localViewModel.refresh(true)
                            })
                    }
                )
        },
    ) {
        Column(
            Modifier.padding(it)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = {
                    TabRowDefaults.Indicator(
                        Modifier.indicatorOffset3(pagerState,it,20.dp),
                    )
                },
                modifier = Modifier.height(50.dp)
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                    ) {
                        Text(
                            text = title,
                        )
                    }
                }
            }
            HorizontalPager(
                state = pagerState,
                count = 3,
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.Top,
            ) { page ->
                when (page) {
                    0 -> {
                        LocalMusic(localViewModel)
                    }
                    1 -> {
                        LocalAlbum(localViewModel){ mediaItem ->
//                            homeNavController.navigate("${Screen.NetSearchScreen.route}")
                            homeNavController.navigate("${Screen.AlbumDetailScreen.route}?mediaItem=${mediaItem.toNavType()}")
                        }
                    }
                    2 -> {
                        LocalArtist (localViewModel){ mediaItem ->
//                            homeNavController.navigate("${Screen.NetSearchScreen.route}")
                            homeNavController.navigate("${Screen.ArtistDetailScreen.route}?mediaItem=${mediaItem.toNavType()}")
                        }
                    }
                }
            }
        }
    }
}
