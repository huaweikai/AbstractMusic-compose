package com.hua.abstractmusic.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
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
    viewModel:HomeViewModel = LocalHomeViewModel.current,
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
            animationSpec = tween(1000),
            skipHalfExpanded = false,
            confirmStateChange = { true }
        )
    ) {
        ModalBottomSheetState(ModalBottomSheetValue.Hidden)
    }

    val sheetPlayState = rememberSaveable(
        saver = ModalBottomSheetState.Saver(
            animationSpec = tween(1000),
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

//    val translationBottom by animateDpAsState(
//        if (navToDetailState.value) 120.dp else 60.dp,
//        animationSpec = spring(1f, 100f)
//    )


    PlayScreen(state = sheetPlayState) {
        HomePlayList(sheetListState) {
            Scaffold(
                topBar = {
                    HomeTopBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 42.dp)
                            .height(50.dp),
                        label = label.value,
                        navToDetail = navToDetailState.value,
                        onPreviewClick = {
                            homeNavController.navigateUp()
                            navToDetailState.value = true
                        }
                    )
                },
                bottomBar = {
                    HomeController(
                        Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .height(if (navToDetailState.value) 60.dp else 120.dp)
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
                }
            )
            {
                HomeNavigationNav(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
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
