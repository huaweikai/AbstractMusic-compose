package com.hua.abstractmusic.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.bean.NavTypeMediaItem
import com.hua.abstractmusic.bean.defaultParcelizeMediaItem
import com.hua.abstractmusic.preference.getValue
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.home.detail.albumdetail.LocalAlbumDetail
import com.hua.abstractmusic.ui.home.detail.artistdetail.LocalArtistDetail
import com.hua.abstractmusic.ui.home.local.LocalScreen
import com.hua.abstractmusic.ui.home.mine.MineScreen
import com.hua.abstractmusic.ui.home.mine.register.LoginScreen
import com.hua.abstractmusic.ui.home.mine.register.RegisterScreen
import com.hua.abstractmusic.ui.home.mine.sheetdetail.SheetDetail
import com.hua.abstractmusic.ui.home.net.NetScreen
import com.hua.abstractmusic.ui.home.net.detail.NetSearchScreen
import com.hua.abstractmusic.ui.route.Screen

/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 主页的小navigation，用于跳转在线音乐，本地音乐和我的界面
 */

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalAnimationApi::class)
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun HomeNavigationNav(
    modifier: Modifier,
) {
//    val homeViewModel: HomeViewModel = LocalHomeViewModel.current
//    val netViewModel: NetViewModel = LocalNetViewModel.current
//    val userViewModel: UserViewModel = LocalUserViewModel.current
    val homeNavController: NavHostController = LocalHomeNavController.current
//    val searchViewModel = LocalSearchViewModel.current
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
            route = "${Screen.LocalAlbumDetail.route}?mediaItem={mediaItem}",
            arguments = listOf(
                navArgument(
                    name = "mediaItem"
                ) {
                    type = NavTypeMediaItem()
                    defaultValue = defaultParcelizeMediaItem
                },
                navArgument(
                    name = "isSearch"
                ) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            ),
        ) {
            val item = it.getValue("mediaItem", defaultParcelizeMediaItem)
            LocalAlbumDetail(
                item
            )
        }
        composable(
            route = "${Screen.LocalArtistDetail.route}?mediaItem={mediaItem}",
            arguments = listOf(
                navArgument(
                    name = "mediaItem"
                ) {
                    type = NavTypeMediaItem()
                    defaultValue = defaultParcelizeMediaItem
                }
            ),
        ) {
            val item = it.getValue("mediaItem", defaultParcelizeMediaItem)
            LocalArtistDetail(
                item = item
            )
        }

        composable(
            route = Screen.RegisterScreen.route
        ) {
            RegisterScreen()
        }

        composable(
            route = Screen.LoginScreen.route
        ) {
            LoginScreen()
        }

//        composable(
//            route = "${Screen.NetDetailScreen.route}?type={type}",
//            arguments = arrayListOf(
//                navArgument(
//                    name = "type"
//                ) {
//                    type = NavType.StringType
//                    defaultValue = ALL_MUSIC_TYPE
//                }
//            )
//        ) {
//            val type = it.getValue("type", ALL_MUSIC_TYPE)
//            NetDetail(type)
//        }

        composable(
            route = "${Screen.LocalSheetDetailScreen.route}?mediaItem={mediaItem}&isUser={isUser}",
            arguments = arrayListOf(
                navArgument(
                    name = "mediaItem"
                ) {
                    type = NavTypeMediaItem()
                    defaultValue = defaultParcelizeMediaItem
                },
                navArgument(
                    name = "isUser"
                ) {
                    type = NavType.BoolType
                    defaultValue = true
                }
            )
        ) {
            val item = it.getValue("mediaItem", defaultParcelizeMediaItem)
            val isUser = it.getValue("isUser", true)
            SheetDetail(mediaItem = item)
        }

        composable(Screen.NetSearchScreen.route) {
            NetSearchScreen()
        }
    }
}