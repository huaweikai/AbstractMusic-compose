package com.hua.abstractmusic.ui


import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
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
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import com.hua.abstractmusic.ui.viewmodels.ThemeViewModel
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
import com.hua.abstractmusic.utils.ComposeUtils
import com.hua.model.parcel.ParcelizeMediaItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : MonetActivity() {

    private val playingViewModel by viewModels<PlayingViewModel>()
    private val themeViewModel by viewModels<ThemeViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val homeViewModel by viewModels<HomeViewModel>()

    @Inject
    lateinit var composeUtils: ComposeUtils

    @OptIn(
        ExperimentalMaterialApi::class,
        ExperimentalPagerApi::class,
        ExperimentalFoundationApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        //不会在系统视图下面绘制
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            monet.awaitMonetReady()
            setContent {
                rememberSystemUiController().setStatusBarColor(
                    Color.Transparent,
                    darkIcons = MaterialTheme.colors.isLight
                )
                val windowSize = rememberWindowSizeClass()
                AppNavigation()
            }
        }
    }
}
