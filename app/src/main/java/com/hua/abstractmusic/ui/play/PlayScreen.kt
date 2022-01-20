package com.hua.abstractmusic.ui.play

import android.util.Log
import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_BACK
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.hua.abstractmusic.ui.home.local.album.LocalAlbum
import com.hua.abstractmusic.ui.home.local.artist.LocalArtist
import com.hua.abstractmusic.ui.home.local.music.LocalMusic
import com.hua.abstractmusic.ui.home.playlist.HomePlayList
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.play.detail.MusicScreen
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/20
 * @Desc   :
 */
@OptIn(ExperimentalMaterialApi::class,ExperimentalPagerApi::class)
@Composable
fun PlayScreen(
    homeViewModel: HomeViewModel
) {
    val state = homeViewModel.playScreenState.value
    val scope = rememberCoroutineScope()
    val viewPageState =homeViewModel.playScreenViewPageState.value
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
                           Text(text = "${tabTitles[page]}")
                        }
                        1->{
                            MusicScreen(viewModel = homeViewModel)
                        }
                        2->{
                            Text(text = "${tabTitles[page]}")
                        }
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ){}
}