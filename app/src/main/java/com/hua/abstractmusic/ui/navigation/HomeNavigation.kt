package com.hua.abstractmusic.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.other.Constant.ALL_MUSIC_TYPE
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.LocalNetViewModel
import com.hua.abstractmusic.ui.LocalUserViewModel
import com.hua.abstractmusic.ui.home.detail.artistdetail.LocalArtistDetail
import com.hua.abstractmusic.ui.home.detail.albumdetail.LocalAlbumDetail
import com.hua.abstractmusic.ui.home.local.LocalScreen
import com.hua.abstractmusic.ui.home.mine.MineScreen
import com.hua.abstractmusic.ui.home.mine.register.LoginScreen
import com.hua.abstractmusic.ui.home.mine.register.RegisterScreen
import com.hua.abstractmusic.ui.home.mine.sheetdetail.SheetDetail
import com.hua.abstractmusic.ui.home.net.NetScreen
import com.hua.abstractmusic.ui.home.net.detail.NetDetail
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.viewmodels.NetViewModel

/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 主页的小navigation，用于跳转在线音乐，本地音乐和我的界面
 */

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun HomeNavigationNav(
    modifier: Modifier,
    viewModel: HomeViewModel = LocalHomeViewModel.current,
    netViewModel: NetViewModel = LocalNetViewModel.current,
    homeNavController: NavHostController = LocalHomeNavController.current,
) {
    DisposableEffect(Unit) {
        viewModel.initializeController()
        netViewModel.initializeController()
        this.onDispose {
            viewModel.releaseBrowser()
            netViewModel.releaseBrowser()
        }
    }
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
            route = "${Screen.LocalAlbumDetail.route}?albumId={albumId}&isLocal={isLocal}",
            arguments = listOf(
                navArgument(
                    name = "albumId"
                ) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(
                    name = "isLocal"
                ) {
                    type = NavType.BoolType
                    defaultValue = true
                }
            ),
        ) { it ->
            val isLocal = it.arguments?.getBoolean("isLocal", true)!!
            val albumId = it.arguments?.getString("albumId", "")
            val item = if (isLocal) {
                viewModel.localAlbumList.value.find { it.mediaId == albumId }!!.mediaItem
            } else {
                netViewModel.getItem(albumId ?: "")
            }
            LocalAlbumDetail(
                item = item,
                isLocal = isLocal
            )
        }
        composable(
            route = "${Screen.LocalArtistDetail.route}?artistId={artistId}&isLocal={isLocal}",
            arguments = listOf(
                navArgument(
                    name = "artistId"
                ) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(
                    name = "isLocal"
                ) {
                    type = NavType.BoolType
                    defaultValue = true
                }
            ),
        ) {
            val artistId = it.arguments?.getString("artistId", "")
            val isLocal = it.arguments?.getBoolean("isLocal", true)!!
            val item = if (isLocal) {
                viewModel.localArtistList.value.find { it.mediaId == artistId }!!.mediaItem
            } else {
                netViewModel.getItem(artistId ?: "")
            }
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

        composable(
            route = "${Screen.NetDetailScreen.route}?type={type}",
            arguments = arrayListOf(
                navArgument(
                    name = "type"
                ) {
                    type = NavType.StringType
                    defaultValue = ALL_MUSIC_TYPE
                }
            )
        ) {
            val type = it.arguments?.getString("type")
            NetDetail(type!!)
        }

        composable(
            route = "${Screen.LocalSheetDetailScreen.route}?sheetIndex={sheetIndex}&isLocal={isLocal}",
            arguments = arrayListOf(
                navArgument(
                    name = "sheetIndex"
                ) {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument(
                    name = "isLocal"
                ) {
                    type = NavType.BoolType
                    defaultValue = true
                }
            )
        ) {
            val userViewModel = LocalUserViewModel.current
            val sheetIndex = it.arguments?.getInt("sheetIndex") ?: 0
            val isLocal = it.arguments?.getBoolean("isLocal") ?: true
            val mediaData = if (isLocal) {
                userViewModel.sheetList.value[sheetIndex]
            } else {
                userViewModel.netSheetList.value[sheetIndex]
            }
            SheetDetail(mediaData = mediaData)
        }
    }
}
