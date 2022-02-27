package com.hua.abstractmusic.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hua.abstractmusic.ui.navigation.AppNavigation
import com.hua.abstractmusic.ui.theme.AbstractMusicTheme
import com.hua.abstractmusic.ui.utils.rememberWindowSizeClass
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.hua.abstractmusic.utils.ComposeUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val localPlayingViewModel by viewModels<PlayingViewModel>()

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

            val appNavHostController = rememberNavController()

            rememberSystemUiController().setStatusBarColor(
                Color.Transparent,
                darkIcons = MaterialTheme.colors.isLight
            )
            val windowSize = rememberWindowSizeClass()
            val popupWindow = remember {
                mutableStateOf(false)
            }
            AbstractMusicTheme {
                CompositionLocalProvider(
                    // 可提前加载信息，不会造成空白
                    LocalPlayingViewModel provides localPlayingViewModel,
                    LocalAppNavController provides appNavHostController,
                    LocalScreenSize provides windowSize,
                    LocalComposeUtils provides composeUtils,
                    LocalPopWindow provides popupWindow
                ) {
                    Surface {
                        AppNavigation()
                    }
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


