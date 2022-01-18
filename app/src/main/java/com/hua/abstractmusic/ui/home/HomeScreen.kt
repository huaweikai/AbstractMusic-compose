package com.hua.abstractmusic.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*


import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.home.playlist.HomePlayList
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.navigation.HomeNavigationNav
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 主界面，可以看作是activity
 */


@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    appNaviController: NavHostController,
    viewModel: HomeViewModel,
    homeNavController: NavHostController
) {
//    viewModel.initializeController()
    val playListState = viewModel.playListState.value
    val scope = rememberCoroutineScope()

    val translationBottom by animateDpAsState(
        if (viewModel.navigationState.value) 0.dp else 60.dp,
        animationSpec = spring(1f, 100f)
    )
    HomePlayList(viewModel = viewModel) {
        Scaffold(
            topBar = {
                HomeTopBar(
                    navHostController = homeNavController,
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 42.dp)
                        .height(42.dp)
                )
            },
            bottomBar = {
                HomeController(
                    homeNavController, viewModel,
                    Modifier
                        .fillMaxWidth()
                        .offset(0.dp, translationBottom)
                        .height(120.dp)
                ) {
                    scope.launch { playListState.show() }
                }
            }
        )
        {
            HomeNavigationNav(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = (if (viewModel.navigationState.value) 120 else 60).dp),
                homeNavController = homeNavController,
                viewModel = viewModel
            )
        }
    }
}
