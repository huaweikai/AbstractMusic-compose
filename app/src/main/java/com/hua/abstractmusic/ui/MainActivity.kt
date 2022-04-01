package com.hua.abstractmusic.ui


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hua.abstractmusic.ui.navigation.AppNavigation
import com.hua.abstractmusic.ui.theme.AbstractMusicTheme
import com.hua.abstractmusic.ui.utils.rememberWindowSizeClass
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.hua.abstractmusic.ui.viewmodels.ThemeViewModel
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
import com.hua.abstractmusic.utils.ComposeUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : MonetActivity() {

    private val playingViewModel by viewModels<PlayingViewModel>()
    private val themeViewModel by viewModels<ThemeViewModel>()
    private val userViewModel by viewModels<UserViewModel>()

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
//        lifecycleScope.launchWhenCreated {
//            monet.awaitMonetReady()
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
                    LocalUserViewModel provides userViewModel,
                ) {
                    AbstractMusicTheme(
                        monet,
                        themeViewModel.monetColor.value
                    ) {
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                        }
                        AppNavigation()
                    }
                }
//            }
        }
    }
}
