package com.hua.abstractmusic.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
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
import com.hua.abstractmusic.ui.viewmodels.ThemeViewModel

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 整个界面的navigation
 */

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun AppNavigation(
    themeViewModel:ThemeViewModel = LocalThemeViewModel.current
) {
    CompositionLocalProvider(
        LocalAppNavController provides rememberNavController(),
        LocalHomeNavController provides rememberNavController(),
    ) {
        NavHost(
            navController = LocalAppNavController.current,
            startDestination = Screen.Splash.route
        ) {
            composable(route = Screen.Splash.route) {
                SplashScreen()
            }
            composable(route = Screen.HelloScreen.route) {
                HelloScreen()
            }
            composable(route = Screen.HomeScreen.route) {
                val popupWindow = remember {
                    mutableStateOf(false)
                }
                val popItem = remember {
                    mutableStateOf(NULL_MEDIA_ITEM)
                }
                CompositionLocalProvider(
                    LocalPopWindowItem provides popItem,
                    LocalPopWindow provides popupWindow,
//                    LocalNetViewModel provides hiltViewModel(),
//                    LocalUserViewModel provides hiltViewModel(),
//                    LocalHomeViewModel provides hiltViewModel(),
//                    LocalSearchViewModel provides hiltViewModel()
                ) {
                    HomeScreen()
                }
            }
        }
        LaunchedEffect(key1 = themeViewModel.isReady) {
            if (!themeViewModel.isReady.value) themeViewModel.init()
        }
    }
}