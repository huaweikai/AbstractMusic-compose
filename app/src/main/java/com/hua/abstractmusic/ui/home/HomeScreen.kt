package com.hua.abstractmusic.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.LocalNetViewModel
import com.hua.abstractmusic.ui.home.playlist.HomePlayList
import com.hua.abstractmusic.ui.navigation.HomeNavigationNav
import com.hua.abstractmusic.ui.play.PlayScreen
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.PopupWindow
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.viewmodels.NetViewModel
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 主界面，可以看作是activity
 */
@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    homeNavController: NavHostController = LocalHomeNavController.current,
    viewModel: HomeViewModel = LocalHomeViewModel.current,
    netViewModel: NetViewModel = LocalNetViewModel.current
) {

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

    LaunchedEffect(homeNavController.currentBackStackEntryAsState().value) {
        homeNavController.currentDestination?.route.let {
            when (it) {
                in routes -> {
                    navToDetailState.value = false
                    label.value = "${homeNavController.currentDestination?.label}"
                }
                else -> {
                    navToDetailState.value = true
                    label.value = ""
                }
            }
        }
    }

    val translationBottom by animateDpAsState(
        if (navToDetailState.value) 60.dp else 120.dp,
        animationSpec = TweenSpec(500)
    )

    PlayScreen(state = sheetPlayState) {
        HomePlayList(sheetListState) {
            Scaffold(
                bottomBar = {
                    HomeController(
                        Modifier
                            .fillMaxWidth()
                            .height(translationBottom)
                            .background(MaterialTheme.colorScheme.background),
                        {
                            scope.launch {
                                sheetListState.animateTo(ModalBottomSheetValue.Expanded)
                            }
                        }, {
                            scope.launch {
                                sheetPlayState.animateTo(ModalBottomSheetValue.Expanded)
                            }
                        }
                    )
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            )
            {
                HomeNavigationNav(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                )
                BackHandler(
                    sheetIsVisible.value
                ) {
                    val state = if (sheetListState.isVisible) sheetListState else sheetPlayState
                    scope.launch {
                        state.animateTo(ModalBottomSheetValue.Hidden)
                    }
                }
            }
        }
    }
    PopupWindow()
}
