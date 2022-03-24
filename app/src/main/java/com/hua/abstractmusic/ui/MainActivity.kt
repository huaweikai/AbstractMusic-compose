package com.hua.abstractmusic.ui


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hua.abstractmusic.ui.navigation.AppNavigation
import com.hua.abstractmusic.ui.theme.AbstractMusicTheme
import com.hua.abstractmusic.ui.utils.rememberWindowSizeClass
import com.hua.abstractmusic.ui.viewmodels.*
import com.hua.abstractmusic.utils.ComposeUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : MonetActivity() {

    private val playingViewModel by viewModels<PlayingViewModel>()
    private val themeViewModel by viewModels<ThemeViewModel>()
    private val netViewModel by viewModels<NetViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val localViewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var composeUtils: ComposeUtils

    @OptIn(
        ExperimentalMaterialApi::class,
        ExperimentalPagerApi::class,
        ExperimentalFoundationApi::class
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        //不会在系统视图下面绘制
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            monet.awaitMonetReady()
            setContent {
                rememberSystemUiController().setStatusBarColor(
                    Color.Transparent,
                    darkIcons = MaterialTheme.colors.isLight
                )
                val windowSize = rememberWindowSizeClass()
                CompositionLocalProvider(
                    LocalPlayingViewModel provides playingViewModel,
                    LocalThemeViewModel provides themeViewModel,
                    LocalScreenSize provides windowSize,
                    LocalComposeUtils provides composeUtils,
                    LocalNetViewModel provides netViewModel,
                    LocalUserViewModel provides userViewModel,
                    LocalHomeViewModel provides localViewModel,
                ) {
                    AbstractMusicTheme(
                        monet,
                        themeViewModel.monetColor.value
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
//        playingViewModel.initializeController()
//        localViewModel.initializeController()
//        netViewModel.initializeController()
//        userViewModel.initializeController()
    }

//    override fun onStop() {
//        playingViewModel.releaseBrowser()
//        localViewModel.releaseBrowser()
//        netViewModel.releaseBrowser()
//        userViewModel.releaseBrowser()
//        Log.d("TAG", "onStop: ")
//        super.onStop()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d("TAG", "onDestroy: ")
//    }
}
