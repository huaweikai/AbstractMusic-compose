package com.hua.abstractmusic.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.home.viewmodels.UserViewModel
import com.hua.abstractmusic.ui.navigation.AppNavigation
import com.hua.abstractmusic.ui.theme.AbstractMusicTheme
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val localHomeViewModel by viewModels<HomeViewModel>()
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        //不会在系统视图下面绘制
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {

            val appNavHostController = rememberNavController()
            val homeNavHostController = rememberNavController()

            rememberSystemUiController().setStatusBarColor(
                Color.Transparent,
                darkIcons = MaterialTheme.colors.isLight
            )
            AbstractMusicTheme {
                CompositionLocalProvider(
                    LocalHomeViewModel provides localHomeViewModel,
                    LocalUserViewModel provides userViewModel,
                    LocalAppNavController provides appNavHostController,
                    LocalHomeNavController provides homeNavHostController
                ) {
                   Surface{
                       AppNavigation()
                   }
                }
            }
        }
    }
}


