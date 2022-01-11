package com.hua.abstractmusic.ui.home.local

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.*
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import kotlinx.coroutines.launch


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 本地音乐screen
 */

@OptIn(ExperimentalPagerApi::class,ExperimentalFoundationApi::class)
@Composable
fun LocalScreen(
    navHostController: NavHostController,
    viewModel: HomeViewModel
//    pagerState:PagerState
) {
    val tabTitles = listOf("音乐", "专辑", "歌手")
    val pagerState = viewModel.horViewPagerState.value
    val coroutineScope = rememberCoroutineScope()
    Column(
    ){
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = {
                TabRowDefaults.Indicator(Modifier.pagerTabIndicatorOffset(pagerState, it))
            },
            modifier = Modifier.height(50.dp)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    modifier = Modifier.background(Color.White)
                ) {
                    Text(text = title, color = if(pagerState.currentPage == index) Color.Blue else Color.Black)
                }
            }
        }
        HorizontalPager(state = pagerState, count = 3, reverseLayout = false){ page ->
            LazyVerticalGrid(
                cells = GridCells.Fixed(2),
                contentPadding = PaddingValues(4.dp),
                modifier = Modifier.background(Color.Blue)
                    .fillMaxSize()
            ){
//                items(3){ index ->
//
//                }
                item {
                    Text(text = tabTitles[page], color = Color.White)
                }
            }
        }
    }
}
