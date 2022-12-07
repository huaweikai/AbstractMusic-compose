package com.hua.abstractmusic.ui.navigation

import android.annotation.SuppressLint
import android.util.Base64
import android.widget.PopupWindow
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.*
import androidx.navigation.compose.*
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.preference.getValue
import com.hua.abstractmusic.ui.*
import com.hua.abstractmusic.ui.hello.HelloScreen
import com.hua.abstractmusic.ui.home.Controller
import com.hua.abstractmusic.ui.home.HomeScreen
import com.hua.abstractmusic.ui.home.detail.DescDetailBottomSheet
import com.hua.abstractmusic.ui.home.detail.albumdetail.LocalAlbumDetail
import com.hua.abstractmusic.ui.home.detail.artistdetail.LocalArtistDetail
import com.hua.abstractmusic.ui.home.mine.register.LoginScreen
import com.hua.abstractmusic.ui.home.mine.register.RegisterScreen
import com.hua.abstractmusic.ui.sheet.SheetDetail
import com.hua.abstractmusic.ui.home.net.detail.NetSearchScreen
import com.hua.abstractmusic.ui.play.PlayListScreen
import com.hua.abstractmusic.ui.play.PlayScreen
import com.hua.abstractmusic.ui.popItem.PopupWindow
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.setting.SettingScreen
import com.hua.abstractmusic.ui.sheet.ShareSheetDialog
import com.hua.abstractmusic.ui.splash.SplashScreen
import com.hua.abstractmusic.ui.route.Dialog
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.hua.abstractmusic.ui.viewmodels.ThemeViewModel
import com.hua.model.parcel.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 整个界面的navigation
 */

val controllerDone = listOf(
    Screen.PlayScreen.route,
    Screen.PlayListScreen.route,
    Screen.Splash.route,
    Screen.HelloScreen.route,
    Screen.SettingScreen.route
)

@OptIn(
    ExperimentalAnimationApi::class,
    com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi::class,
    androidx.compose.material3.ExperimentalMaterial3Api::class
)
@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun AppNavigation(
    themeViewModel: ThemeViewModel = LocalThemeViewModel.current
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        val navController = rememberNavController()
        NavHost(
            navController = navController, 
            startDestination = Screen.Splash.route
        ) {
            composable(Screen.Splash.route) {
                SplashScreen()
            }
            composable(Screen.HelloScreen.route) {
                HelloScreen()
            }
            composable(Screen.HomeScreen.route) {
                ModalDrawerSheet() {
                    
                }
            }
        }
    }
}