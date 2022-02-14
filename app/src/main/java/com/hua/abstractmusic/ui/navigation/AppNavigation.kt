package com.hua.abstractmusic.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.ui.*
import com.hua.abstractmusic.ui.hello.HelloScreen
import com.hua.abstractmusic.ui.home.HomeScreen
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.splash.SplashScreen
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 整个界面的navigation
 */

@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun AppNavigation(
    appNavController:NavHostController = LocalAppNavController.current
) {
    NavHost(
        navController = appNavController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen()
        }
        composable(route = Screen.HelloScreen.route) {
            HelloScreen()
        }
        composable(route = Screen.HomeScreen.route) {
            CompositionLocalProvider(
                LocalHomeNavController provides rememberNavController(),
                LocalNetViewModel provides hiltViewModel(),
                LocalUserViewModel provides hiltViewModel()
            ){
                HomeScreen()
            }
        }
    }

}