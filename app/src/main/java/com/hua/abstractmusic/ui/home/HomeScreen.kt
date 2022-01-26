package com.hua.abstractmusic.ui.home

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.ui.home.playlist.HomePlayList
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.navigation.HomeNavigationNav
import com.hua.abstractmusic.ui.play.PlayScreen
import com.hua.abstractmusic.ui.route.Screen
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
    val playListState = viewModel.playListState.value
    val scope = rememberCoroutineScope()

    val routes = listOf(Screen.LocalScreen.route, Screen.NetScreen.route, Screen.MineScreen.route)

    LaunchedEffect(homeNavController.currentBackStackEntryAsState().value) {
        homeNavController.currentDestination?.route.let {
            when {
                it in routes -> {
                    viewModel.navigationState.value = true
                }
                it?.startsWith(Screen.LocalAlbumDetail.route) == true -> {
                    viewModel.navigationState.value = false
                }
                it?.startsWith(Screen.LocalArtistDetail.route) == true -> {
                    viewModel.navigationState.value = false
                }
                it == Screen.LoginScreen.route ->{
                    viewModel.navigationState.value = false
                }
            }
        }
    }

    LaunchedEffect(viewModel.playScreenBoolean.value) {
        if (!viewModel.playScreenBoolean.value) {
            viewModel.playScreenState.value.hide()
        }
    }

    LaunchedEffect(viewModel.playListBoolean.value) {
        if (!viewModel.playListBoolean.value) {
            viewModel.playListState.value.hide()
        }
    }

//    LaunchedEffect(viewModel.controllerTitleViewPageState.value){
//        viewModel.skipTo(viewModel.controllerTitleViewPageState.value.currentPage)
//    }

    val translationBottom by animateDpAsState(
        if (viewModel.navigationState.value) 120.dp else 60.dp,
        animationSpec = spring(1f, 100f)
    )
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
                    .background(
                        Color.White,
                        RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                    )
//                        .offset(0.dp, translationBottom)
                    .height(translationBottom)
            ) {
                scope.launch {
                    playListState.show()
                    viewModel.playListBoolean.value = true
                }
            }
        }
    )
    {
        HomeNavigationNav(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(bottom = (if (viewModel.navigationState.value) 120 else 60).dp),
            homeNavController = homeNavController,
            viewModel = viewModel
        )
    }
    HomePlayList(viewModel = viewModel)
    PlayScreen(homeViewModel = viewModel)
}
