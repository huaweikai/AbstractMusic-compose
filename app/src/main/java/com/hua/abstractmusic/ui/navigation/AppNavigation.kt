package com.hua.abstractmusic.ui.navigation

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hua.abstractmusic.ui.hello.HelloScreen
import com.hua.abstractmusic.ui.hello.PermissionGet
import com.hua.abstractmusic.ui.home.HomeScreen
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.splash.SplashScreen
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 整个界面的navigation
 */


@Composable
fun HomeNavigation(
    nextRoute:String,
    appNavController: NavHostController,
    viewModel: HomeViewModel,
    homeController: NavHostController
) {
    var isGet by remember {
        mutableStateOf(true)
    }
    val scpoe = rememberCoroutineScope()
/*    todo(这个是我刚写的还不知道能不能生效)
Snackbar(
        snackbarData = object : SnackbarData {
            override val actionLabel: String?
                get() = null
            override val duration: SnackbarDuration
                get() = SnackbarDuration.Short
            override val message: String
                get() = "权限未获取完整"

            override fun dismiss() {

            }

            override fun performAction() {
            }
        },
        actionOnNewLine = !isGet
    )*/

    val s = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            it.forEach { map ->
                if (!map.value) {
                    isGet = false
                }
            }
            if (isGet) {
                val navOptions =
                    NavOptions.Builder().setPopUpTo(Screen.HelloScreen.route, true).build()
//                scpoe.launch {
                    appNavController.navigate(Screen.HomeScreen.route, navOptions)
//                }
            }
        }
    )
    NavHost(
        navController = appNavController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route){
            SplashScreen(appNavHostController = appNavController, nextRoute = nextRoute)
        }
        composable(route = Screen.HelloScreen.route) {
            HelloScreen {
                //从欢迎界面传递过来的点击的lambda
                s.launch(
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        }
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(appNaviController = appNavController,viewModel,homeController)
        }
    }
}