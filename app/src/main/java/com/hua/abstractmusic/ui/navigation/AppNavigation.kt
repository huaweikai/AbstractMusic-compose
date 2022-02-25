package com.hua.abstractmusic.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.ui.*
import com.hua.abstractmusic.ui.hello.HelloScreen
import com.hua.abstractmusic.ui.home.HomeScreen
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.splash.SplashScreen

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 整个界面的navigation
 */

@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@SuppressLint("UnsafeOptInUsageError")
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
            val popItem = remember{
                mutableStateOf(NULL_MEDIA_ITEM)
            }
            CompositionLocalProvider(
                LocalHomeNavController provides rememberNavController(),
                LocalNetViewModel provides hiltViewModel(),
                LocalUserViewModel provides hiltViewModel(),
                LocalHomeViewModel provides hiltViewModel(),
                LocalPopWindowItem provides popItem
            ){
                HomeScreen()
            }
        }
    }

}