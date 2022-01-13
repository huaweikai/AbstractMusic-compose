package com.hua.abstractmusic.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.hua.abstractmusic.ui.home.HomeTopBar
import com.hua.abstractmusic.ui.home.local.LocalScreen
import com.hua.abstractmusic.ui.home.local.album.detail.LocalAlbumDetail
import com.hua.abstractmusic.ui.home.mine.MineScreen
import com.hua.abstractmusic.ui.home.net.NetScreen
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.route.Screen

/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 主页的小navigation，用于跳转在线音乐，本地音乐和我的界面
 */

@OptIn(ExperimentalPagerApi::class)
@Composable
fun BottomControllerNavigation(
    bottomNavController:NavHostController,
    homeNavHostController: NavHostController,
    viewModel: HomeViewModel
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ){
        val (topBar,navHost) = createRefs()
        HomeTopBar(
            navHostController = bottomNavController,
            viewModel = viewModel,
            modifier = Modifier
                //todo(后续要自动计算每个手机的状态栏高度)
//                .padding(top = 42.dp)
                .background(Color.White)
                .constrainAs(topBar){
                    start.linkTo(parent.start)
                    top.linkTo(parent.top,42.dp)
                    end.linkTo(parent.end)
                    bottom.linkTo(navHost.top)
                    height = Dimension.preferredValue(39.dp)
                    width = Dimension.fillToConstraints
                }
        )
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.NetScreen.route,
            modifier = Modifier.constrainAs(navHost){
                top.linkTo(topBar.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        ) {
            composable(route = Screen.NetScreen.route) {
                it.destination.label = "在线音乐"
                NetScreen(bottomNavController,viewModel)
            }
            composable(route = Screen.LocalScreen.route) {
                it.destination.label = "本地音乐"
                LocalScreen(homeNavHostController,viewModel)
            }
            composable(route = Screen.MineScreen.route) {
                it.destination.label = "我的"
                MineScreen(bottomNavController)
            }
        }
    }
}