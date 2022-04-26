package com.hua.abstractmusic.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.home.local.LocalScreen
import com.hua.abstractmusic.ui.home.local.album.LocalAlbum
import com.hua.abstractmusic.ui.home.local.artist.LocalArtist
import com.hua.abstractmusic.ui.home.local.music.LocalMusic
import com.hua.abstractmusic.ui.home.mine.MineScreen
import com.hua.abstractmusic.ui.home.net.NetScreen
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
    homeNavController:NavHostController,
    modifier: Modifier,
) {
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
    }
}