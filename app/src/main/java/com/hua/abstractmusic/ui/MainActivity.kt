package com.hua.abstractmusic.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hua.abstractmusic.ui.navigation.HomeNavigation
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.theme.AbstractMusicTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController
    private var currentRoute :String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            navHostController = rememberNavController()
            currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route
            //这个，第三方做的透明状态栏
            rememberSystemUiController().setStatusBarColor(
                Color.Transparent,
                darkIcons = MaterialTheme.colors.isLight)
            AbstractMusicTheme {
                HomeNavigation(activity = this@MainActivity,navHostController)
            }
        }
    }

    override fun onBackPressed() {
        if(currentRoute != null && currentRoute == Screen.HomeScreen.route){
            finish()
        }else{
            super.onBackPressed()
        }
    }

    override fun finish() {
        moveTaskToBack(true)
    }
}


