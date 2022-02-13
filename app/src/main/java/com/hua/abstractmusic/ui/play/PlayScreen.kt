package com.hua.abstractmusic.ui.play

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.play.detail.ListScreen
import com.hua.abstractmusic.ui.play.detail.MusicScreen
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/20
 * @Desc   :
 */
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun PlayScreen(
    state:ModalBottomSheetState,
    homeViewModel: HomeViewModel = LocalHomeViewModel.current,
) {
    val scope = rememberCoroutineScope()
    val viewPageState = rememberPagerState(1)
    val tabTitles = listOf("列表","歌曲","歌词")


    ModalBottomSheetLayout(
        sheetState = state ,
        sheetContent = {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White, RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp))
            ) {
                val (tab,viewPager) = createRefs()
                val tabStart = createGuidelineFromStart(0.3f)
                val tabEnd = createGuidelineFromStart(0.7f)
                TabRow(
                    selectedTabIndex = viewPageState.currentPage,
                    modifier = Modifier
                        .constrainAs(tab){
                            start.linkTo(tabStart)
                            end.linkTo(tabEnd)
                            top.linkTo(parent.top,50.dp)
                            width = Dimension.fillToConstraints
                            height = Dimension.preferredValue(40.dp)
                        }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = viewPageState.currentPage == index,
                            onClick = {
                                scope.launch { viewPageState.animateScrollToPage(index) }
                            },
                            modifier = Modifier.background(Color.White)
                        ) {
                            Text(
                                text = title,
                                color = if (viewPageState.currentPage == index) Color.Blue else Color.Black
                            )
                        }
                    }
                }
                HorizontalPager(
                    state = viewPageState,
                    count = 3,
                    reverseLayout = false,
                    modifier = Modifier
                        .constrainAs(viewPager){
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        },
                ) { page ->
                    when (page) {
                        0->{
                           ListScreen(viewModel = homeViewModel)
                        }
                        1->{
                            MusicScreen(viewModel = homeViewModel)
                        }
                        2->{
                            Text(text = tabTitles[page])
                        }
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ){}
}