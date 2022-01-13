package com.hua.abstractmusic.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hua.abstractmusic.ui.home.local.album.detail.LocalAlbumDetail
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.route.Screen

/**
 * @author : huaweikai
 * @Date   : 2022/01/13
 * @Desc   : detail
 */
@Composable
fun HomeNavigationNav(
    homeController: NavHostController,
    bottomNavHostController: NavHostController,
    viewModel: HomeViewModel,
    modifier: Modifier
){
    NavHost(
        navController = homeController,
        startDestination = Screen.BottomNavHost.route,
        modifier = modifier
    ){
        composable(Screen.BottomNavHost.route){
             BottomControllerNavigation(
                 bottomNavController = bottomNavHostController,
                 viewModel = viewModel,
                 homeNavHostController = homeController
             )
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
                homeController,
                item = viewModel.localAlbumList.value[index!!].mediaItem,
                viewModel
            )
        }
    }
}