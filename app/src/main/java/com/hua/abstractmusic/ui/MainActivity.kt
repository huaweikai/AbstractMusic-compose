package com.hua.abstractmusic.ui


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hua.abstractmusic.ui.navigation.AppNavigation
import com.hua.abstractmusic.ui.theme.AbstractMusicTheme
import com.hua.abstractmusic.ui.utils.rememberWindowSizeClass
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.hua.abstractmusic.ui.viewmodels.ThemeViewModel
import com.hua.abstractmusic.utils.ComposeUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val localPlayingViewModel by viewModels<PlayingViewModel>()
    private val themeViewModel by viewModels<ThemeViewModel>()

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
        setContent {
            rememberSystemUiController().setStatusBarColor(
                Color.Transparent,
                darkIcons = MaterialTheme.colors.isLight
            )
            val windowSize = rememberWindowSizeClass()
            val popupWindow = remember {
                mutableStateOf(false)
            }
            CompositionLocalProvider(
                // 可提前加载信息，不会造成空白
                LocalPlayingViewModel provides localPlayingViewModel,
                LocalThemeViewModel provides themeViewModel,
                LocalScreenSize provides windowSize,
                LocalComposeUtils provides composeUtils,
                LocalPopWindow provides popupWindow
            ) {
                AbstractMusicTheme(
                    themeViewModel.monetColor.value
                ){
//                    ProvideWindowInsets {
                        AppNavigation()
//                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        localPlayingViewModel.initializeController()
    }

    override fun onStop() {
        super.onStop()
        localPlayingViewModel.releaseBrowser()
    }
}


