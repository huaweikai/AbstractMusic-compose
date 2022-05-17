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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.hua.abstractmusic.ui.*
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
@OptIn(
    ExperimentalPagerApi::class, ExperimentalMaterialApi::class,
    ExperimentalMaterial3Api::class
)
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
        LocalMusicScreenFirstColor provides animateColorAsState(
            targetValue = itemColor.first,
            tween(600)
        ).value,
        LocalMusicScreenSecondColor provides animateColorAsState(
            targetValue = itemColor.second,
            tween(600)
        ).value
    ) {
        val snackBarHostState = remember { SnackbarHostState() }
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState) {
                    Snackbar(
                        modifier = Modifier.padding(
                            bottom = LocalBottomControllerHeight.current,
                            start = 16.dp,
                            end = 16.dp
                        )
                    ) {
                        Text(text = it.visuals.message)
                    }
                }
            }
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(it),
            ) {
                PlayScreenContent(viewPageState, snackBarHostState)
                PlayScreenTab(viewPageState)
            }
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
                            LocalMusicScreenFirstColor.current else LocalMusicScreenSecondColor.current
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
    viewPageState: PagerState,
    snackbarHostState: SnackbarHostState,
    viewModel: PlayingViewModel = LocalPlayingViewModel.current
) {
    val playerState = viewModel.playerState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current


    CompositionLocalProvider(
        LocalContentColor provides LocalMusicScreenFirstColor.current,
        androidx.compose.material.LocalContentColor provides LocalMusicScreenFirstColor.current
    ) {
        HorizontalPager(
            state = viewPageState,
            count = 3,
            reverseLayout = false,
            modifier = Modifier
                .background(if (isSystemInDarkTheme()) Color.Black else Color.White)
                .fillMaxSize(),
        ) { page ->
            when (page) {
                0 -> {
                    ListScreen()
                }
                1 -> {
                    MusicScreen(snackbarHostState)
                }
                2 -> {
                    LyricsScreen()
                }
            }
        }
    }
    DisposableEffect(Unit){
        val observer = LifecycleEventObserver{_,event->
            if(event == Lifecycle.Event.ON_RESUME){
                if (playerState.value) {
                    viewModel.startUpdatePosition()
                    viewModel.startUpdateLyrics()
                }
            }else if(event == Lifecycle.Event.ON_PAUSE){
                viewModel.cancelUpdateLyrics()
                viewModel.cancelUpdatePosition()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        this.onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(playerState.value){
        if(playerState.value){
            viewModel.startUpdateLyrics()
            viewModel.startUpdatePosition()
        }else{
            viewModel.cancelUpdateLyrics()
            viewModel.cancelUpdatePosition()
        }
    }
}