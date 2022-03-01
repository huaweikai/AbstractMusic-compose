package com.hua.abstractmusic.ui.home.local

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.home.local.album.LocalAlbum
import com.hua.abstractmusic.ui.home.local.artist.LocalArtist
import com.hua.abstractmusic.ui.home.local.music.LocalMusic
import com.hua.abstractmusic.ui.route.Screen
import kotlinx.coroutines.launch


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 本地音乐screen
 */

@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun LocalScreen(
    homeNavController: NavHostController = LocalHomeNavController.current
) {
    val pagerState = rememberPagerState()
    val tabTitles = listOf("音乐", "专辑", "歌手")
    val coroutineScope = rememberCoroutineScope()
    Column {
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
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.background
                    )
                ) {
                    Text(
                        text = title,
                        color =
                        if (pagerState.currentPage == index) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            count = 3,
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.Top,
        ) { page ->
            when (page) {
                0 -> {
                    LocalMusic()
                }
                1 -> {
                    LocalAlbum{ mediaId->
                        homeNavController.navigate("${Screen.LocalAlbumDetail.route}?albumId=${mediaId}")
                    }
                }
                2 -> {
                    LocalArtist{ index->
                        homeNavController.navigate("${Screen.LocalArtistDetail.route}?artistIndex=${index}")
                    }
                }
            }
        }
    }
}
