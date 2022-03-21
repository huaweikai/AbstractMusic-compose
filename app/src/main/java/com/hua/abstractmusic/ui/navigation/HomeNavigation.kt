package com.hua.abstractmusic.ui.navigation

import android.annotation.SuppressLint
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
import com.hua.abstractmusic.preference.getValue
import com.hua.abstractmusic.ui.*
import com.hua.abstractmusic.ui.home.detail.albumdetail.LocalAlbumDetail
import com.hua.abstractmusic.ui.home.detail.artistdetail.LocalArtistDetail
import com.hua.abstractmusic.ui.home.local.LocalScreen
import com.hua.abstractmusic.ui.home.mine.MineScreen
import com.hua.abstractmusic.ui.home.mine.register.LoginScreen
import com.hua.abstractmusic.ui.home.mine.register.RegisterScreen
import com.hua.abstractmusic.ui.home.mine.sheetdetail.SheetDetail
import com.hua.abstractmusic.ui.home.net.NetScreen
import com.hua.abstractmusic.ui.home.net.detail.NetDetail
import com.hua.abstractmusic.ui.home.net.detail.NetSearchScreen
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.viewmodels.NetViewModel
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
import com.hua.abstractmusic.utils.isLocal

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
    val homeViewModel: HomeViewModel = LocalHomeViewModel.current
    val netViewModel: NetViewModel = LocalNetViewModel.current
    val userViewModel: UserViewModel = LocalUserViewModel.current
    val homeNavController: NavHostController = LocalHomeNavController.current
    val searchViewModel = LocalSearchViewModel.current
    DisposableEffect(Unit) {
        homeViewModel.initializeController()
        netViewModel.initializeController()
        userViewModel.initializeController()
        this.onDispose {
            homeViewModel.releaseBrowser()
            netViewModel.releaseBrowser()
            userViewModel.releaseBrowser()
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
            route = "${Screen.LocalAlbumDetail.route}?albumId={albumId}&isSearch={isSearch}",
            arguments = listOf(
                navArgument(
                    name = "albumId"
                ) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(
                    name = "isSearch"
                ) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            ),
        ) {
            val albumId = it.getValue("albumId", "")
            val isSearch = it.getValue("isSearch", false)
            val isLocal = albumId.isLocal()
            val item = if (isLocal) {
                homeViewModel.localAlbumList.value.find { it.mediaId == albumId }!!.mediaItem
            } else {
                if (isSearch) {
                    searchViewModel.searchAlbum.value.data!!.find { it.mediaId == albumId }!!
                } else {
                    netViewModel.getItem(albumId)
                }
            }
            LocalAlbumDetail(
                item = item,
                isLocal = isLocal
            )
        }
        composable(
            route = "${Screen.LocalArtistDetail.route}?artistId={artistId}&isSearch={isSearch}",
            arguments = listOf(
                navArgument(
                    name = "artistId"
                ) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(
                    name = "isSearch"
                ) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            ),
        ) {
            val artistId = it.getValue("artistId", "")
            val isSearch = it.getValue("isSearch", false)
            val isLocal = artistId.isLocal()
            val item = if (isLocal) {
                homeViewModel.localArtistList.value.find { it.mediaId == artistId }!!.mediaItem
            } else {
                if (isSearch) {
                    searchViewModel.searchArtist.value.data!!.find { it.mediaId == artistId }!!
                } else {
                    netViewModel.getItem(artistId)
                }
            }
            LocalArtistDetail(
                item = item,
                isLocal = isLocal
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
            val type = it.getValue("type", ALL_MUSIC_TYPE)
            NetDetail(type)
        }

        composable(
            route = "${Screen.LocalSheetDetailScreen.route}?sheetId={sheetId}&isUser={isUser}&isSearch={isSearch}",
            arguments = arrayListOf(
                navArgument(
                    name = "sheetId"
                ) {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument(
                    name = "isUser"
                ) {
                    type = NavType.BoolType
                    defaultValue = true
                },
                navArgument(
                    name = "isSearch"
                ) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            val sheetId = it.getValue("sheetId", "")
            val isLocal = sheetId.isLocal()
            val isUser = it.getValue("isUser", true)
            val isSearch = it.getValue("isSearch", false)
            val mediaItem = if (isLocal) {
                userViewModel.sheetList.value.find { it.mediaId == sheetId }!!.mediaItem
            } else {
                if (isSearch) {
                    searchViewModel.searchSheet.value.data!!.find { it.mediaId == sheetId }!!
                } else {
                    if (isUser) {
                        userViewModel.netSheetList.value.find { it.mediaId == sheetId }!!.mediaItem
                    } else {
                        netViewModel.recommendList.value.find { it.mediaId == sheetId }!!.mediaItem
                    }
                }
            }
            SheetDetail(mediaItem = mediaItem)
        }

        composable(Screen.NetSearchScreen.route) {
            NetSearchScreen()
        }
    }
}