package com.hua.abstractmusic.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.other.Constant.ALL_MUSIC_TYPE
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.home.local.LocalScreen
import com.hua.abstractmusic.ui.home.local.album.detail.LocalAlbumDetail
import com.hua.abstractmusic.ui.home.local.artist.detail.LocalArtistDetail
import com.hua.abstractmusic.ui.home.mine.MineScreen
import com.hua.abstractmusic.ui.home.mine.register.LoginScreen
import com.hua.abstractmusic.ui.home.mine.register.RegisterScreen
import com.hua.abstractmusic.ui.home.net.NetScreen
import com.hua.abstractmusic.ui.home.net.detail.NetDetail
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
import com.hua.abstractmusic.ui.route.Screen

/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 主页的小navigation，用于跳转在线音乐，本地音乐和我的界面
 */

@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun HomeNavigationNav(
    modifier: Modifier,
    viewModel: HomeViewModel = LocalHomeViewModel.current,
    homeNavController:NavHostController = LocalHomeNavController.current,
) {
    val userViewModel: UserViewModel = hiltViewModel()
    NavHost(
        navController = homeNavController,
        startDestination = Screen.NetScreen.route,
        modifier = modifier
    ) {
        composable(route = Screen.NetScreen.route) {
            it.destination.label = "在线音乐"
            NetScreen()
        }
        composable(route = Screen.LocalScreen.route) {
            it.destination.label = "本地音乐"
            LocalScreen()
        }
        composable(route = Screen.MineScreen.route) {
            it.destination.label = "我的"
            MineScreen()
        }
        composable(
            route = "${Screen.LocalAlbumDetail.route}?albumId={albumId}",
            arguments = listOf(
                navArgument(
                    name = "albumId"
                ){
                    type = NavType.StringType
                    defaultValue = ""
                }
            ),
        ){
            val albumId =it.arguments?.getString("albumId","")
            val item = viewModel.localAlbumList.value.find { it.mediaId == albumId }!!.mediaItem
            LocalAlbumDetail(
                item = item
            )
        }
        composable(
            route = "${Screen.LocalArtistDetail.route}?artistIndex={artistIndex}",
            arguments = listOf(
                navArgument(
                    name = "artistIndex"
                ){
                    type = NavType.IntType
                    defaultValue = -1
                }
            ),
        ){
            val index =it.arguments?.getInt("artistIndex",-1)
            val item = viewModel.localArtistList.value[index!!].mediaItem
            LocalArtistDetail(
                item = item,
                homeNavController
            )
        }

        composable(
            route = Screen.RegisterScreen.route
        ){
            RegisterScreen(userViewModel,homeNavController)
        }

        composable(
            route = Screen.LoginScreen.route
        ){
            LoginScreen(homeNavController,userViewModel)
        }

        composable(
            route = "${Screen.NetDetailScreen.route}?type={type}",
            arguments = arrayListOf(
                navArgument(
                    name = "type"
                ){
                    type = NavType.StringType
                    defaultValue = ALL_MUSIC_TYPE
                }
            )
        ){
            val type = it.arguments?.getString("type")
            NetDetail(type!!)
        }
    }

}