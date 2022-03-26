package com.hua.abstractmusic.ui.play

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.hua.abstractmusic.ui.LocalMusicScreenSecondColor
import com.hua.abstractmusic.ui.LocalPlayingViewModel
import com.hua.abstractmusic.ui.play.detail.ListScreen
import com.hua.abstractmusic.ui.play.detail.LyricsScreen
import com.hua.abstractmusic.ui.play.detail.MusicScreen
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/20
 * @Desc   :
 */
@SuppressLint("UnsafeOptInUsageError")
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun PlayScreen(
    state: ModalBottomSheetState,
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = state,
        modifier = Modifier
            .fillMaxSize(),
        sheetContent = {
//            PlayScreen()
        },
        sheetShape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
        sheetBackgroundColor = if (isDark) Color.Black else Color.White
    ) {
        val scope = rememberCoroutineScope()
        content()
    }
}

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalPagerApi::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun PlayScreen(
    viewPageState: PagerState,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current,
) {
    val itemColor = viewModel.itemColor.collectAsState().value

    CompositionLocalProvider(
        LocalContentColor provides animateColorAsState(
            targetValue = itemColor.first,
            tween(600)
        ).value,
        androidx.compose.material.LocalContentColor provides animateColorAsState(
            targetValue = itemColor.first,
            tween(600)
        ).value,
        LocalMusicScreenSecondColor provides animateColorAsState(
            targetValue = itemColor.second,
            tween(600)
        ).value
    ) {
        Box(
            Modifier
                .fillMaxSize(),
        ) {
            val scope = rememberCoroutineScope()
            PlayScreenContent(viewPageState)
            PlayScreenTab(viewPageState)
        }
    }
}

@ExperimentalPagerApi
@Composable
private fun PlayScreenTab(
    viewPageState: PagerState,
) {
    val tabTitles = listOf("列表", "歌曲", "歌词")
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        horizontalAlignment = CenterHorizontally
    ) {
        TabRow(
            selectedTabIndex = viewPageState.currentPage,
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth(0.4f)
                .align(CenterHorizontally),
            containerColor = Color.Transparent,
            divider = {},
            indicator = {}
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = viewPageState.currentPage == index,
                    onClick = {
                        scope.launch { viewPageState.animateScrollToPage(index) }
                    },
                    modifier = Modifier
                ) {
                    Text(
                        text = title,
                        color = if (viewPageState.currentPage == index)
                            LocalContentColor.current else LocalMusicScreenSecondColor.current
                    )
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
private fun PlayScreenContent(
    viewPageState: PagerState
) {
    HorizontalPager(
        state = viewPageState,
        count = 3,
        reverseLayout = false,
        modifier = Modifier
            .clip(RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp))
            .background(if (isSystemInDarkTheme()) Color.Black else Color.White)
            .fillMaxSize(),
    ) { page ->
        when (page) {
            0 -> {
                ListScreen()
            }
            1 -> {
                MusicScreen()
            }
            2 -> {
                LyricsScreen()
            }
        }
    }
}