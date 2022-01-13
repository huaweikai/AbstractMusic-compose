package com.hua.abstractmusic.ui


import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.navigation.HomeNavigation
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.theme.AbstractMusicTheme
import com.hua.abstractmusic.utils.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController
    private var currentRoute :String? = null
    lateinit var viewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        //不会在系统视图下面绘制
        WindowCompat.setDecorFitsSystemWindows(window,false)
        super.onCreate(savedInstanceState)
        setContent {
            navHostController = rememberNavController()
            currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route
            //这个， 透明状态栏
            rememberSystemUiController().setStatusBarColor(
                Color.Transparent,
                darkIcons = MaterialTheme.colors.isLight)
            AbstractMusicTheme {
                viewModel = viewModel()
                viewModel.initializeController()
                HomeNavigation(activity = this@MainActivity,navHostController,viewModel)
            }
        }
    }

//    override fun onBackPressed() {
//        if(navHostController.currentDestination?.route == Screen.HomeScreen.route){
//            finish()
//        }
//        else{
//            super.onBackPressed()
//        }
//    }
}


