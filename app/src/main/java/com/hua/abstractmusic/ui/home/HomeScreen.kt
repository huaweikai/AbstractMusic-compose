package com.hua.abstractmusic.ui.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalPlayingViewModel
import com.hua.abstractmusic.ui.home.playlist.HomePlayList
import com.hua.abstractmusic.ui.navigation.HomeNavigationNav
import com.hua.abstractmusic.ui.play.PlayScreen
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.PopupWindow
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 主界面，可以看作是activity
 */
@SuppressLint("UnsafeOptInUsageError")
@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    homeNavController: NavHostController = LocalHomeNavController.current,
    playingViewModel: PlayingViewModel = LocalPlayingViewModel.current
) {
    var bottomControllerHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val navToDetailState = rememberSaveable {
        mutableStateOf(false)
    }

    val sheetIsVisible = rememberSaveable {
        mutableStateOf(false)
    }

    val sheetListState = rememberSaveable(
        saver = ModalBottomSheetState.Saver(
            animationSpec = SwipeableDefaults.AnimationSpec,
            skipHalfExpanded = false,
            confirmStateChange = { true }
        )
    ) {
        ModalBottomSheetState(ModalBottomSheetValue.Hidden)
    }

    val sheetPlayState = rememberSaveable(
        saver = ModalBottomSheetState.Saver(
            animationSpec = SwipeableDefaults.AnimationSpec,
            skipHalfExpanded = true,
            confirmStateChange = { true }
        )
    ) {
        ModalBottomSheetState(ModalBottomSheetValue.Hidden, isSkipHalfExpanded = true)
    }

    LaunchedEffect(sheetListState.currentValue, sheetPlayState.currentValue) {
        sheetIsVisible.value = sheetPlayState.isVisible || sheetListState.isVisible
    }

    val label = remember {
        mutableStateOf("")
    }

    val routes = listOf(Screen.LocalScreen.route, Screen.NetScreen.route, Screen.MineScreen.route)
    val backState = homeNavController.currentBackStackEntryAsState()

//    val translationBottom by animateDpAsState(
//        if (navToDetailState.value) 60.dp else 120.dp
//    )

    PlayScreen(state = sheetPlayState) {
        HomePlayList(sheetListState) {
            Scaffold(
                bottomBar = {
                    AnimatedVisibility(
                        visible = backState.value?.destination?.route in routes,
                        enter = slideInVertically() + fadeIn(),
                        exit = slideOutVertically() + fadeOut()
                    ) {
                        HomeBottomBar()
                    }
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            )
            {
                val bottomPadding = animateDpAsState(
                    it.calculateBottomPadding()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = bottomPadding.value)
                ) {
                    CompositionLocalProvider(LocalBottomControllerHeight provides bottomControllerHeight) {
                        HomeNavigationNav(Modifier)
                    }
                    BackHandler(
                        sheetIsVisible.value
                    ) {
                        val state = if (sheetListState.isVisible) sheetListState else sheetPlayState
                        scope.launch {
                            state.animateTo(ModalBottomSheetValue.Hidden)
                        }
                    }
                    AnimatedVisibility(
                        visible = playingViewModel.currentPlayItem.value != Constant.NULL_MEDIA_ITEM,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Surface(
                            modifier = Modifier
                                .onSizeChanged {
                                    bottomControllerHeight = with(density) { it.height.toDp() }
                                }
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            tonalElevation = 3.dp
                        ) {
                            Controller(
                                playListClick = {
                                    scope.launch { sheetListState.animateTo(ModalBottomSheetValue.Expanded) }
                                },
                                playScreenClick = {
                                    scope.launch {
                                        sheetPlayState.animateTo(ModalBottomSheetValue.Expanded)
                                    }
                                })
                        }

                    }
                }
            }
        }
    }
    PopupWindow()
}
