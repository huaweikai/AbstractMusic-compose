package com.hua.abstractmusic.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
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
fun HomeNavigationNav(
    homeNavController:NavHostController,
    modifier: Modifier,
    viewModel: HomeViewModel
) {
    val pagerState = rememberPagerState(initialPage = 0)
    NavHost(
        navController = homeNavController,
        startDestination = Screen.NetScreen.route,
        modifier = modifier
    ) {
        composable(route = Screen.NetScreen.route) {
            it.destination.label = "在线音乐"
            NetScreen(homeNavController,viewModel)
        }
        composable(route = Screen.LocalScreen.route) {
            it.destination.label = "本地音乐"
            LocalScreen(homeNavController,viewModel)
        }
        composable(route = Screen.MineScreen.route) {
            it.destination.label = "我的"
            MineScreen(homeNavController)
        }
        composable(
            route = "${Screen.LocalAlbumDetail.route}?albumIndex={albumIndex}",
            arguments = listOf(
                navArgument(
                    name = "albumIndex"
                ){
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ){
            val index =it.arguments?.getInt("albumIndex",-1)
            LocalAlbumDetail(
                homeNavController,
                item = viewModel.localAlbumList.value[index!!].mediaItem,
                viewModel
            )
        }
    }

}