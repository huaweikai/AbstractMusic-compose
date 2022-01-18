package com.hua.abstractmusic.ui.splash

import android.util.Log
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.hello.PermissionGet
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.utils.getVersion
import kotlinx.coroutines.*


/**
 * @author : huaweikai
 * @Date   : 2022/01/14
 * @Desc   : splash
 */
@Composable
fun SplashScreen(
    nextRoute: String,
    appNavHostController: NavHostController
) {
    val scope = rememberCoroutineScope()
//    var state by remember {
//        mutableStateOf(false)
//    }
    val lifecycleObserver = LocalLifecycleOwner.current

//    LaunchedEffect(state){
//        if(!state){
//            delay(500L)
//            state = true
//        }
//        if (state){
//            delay(500L)
//            val navOptions = NavOptions.Builder()
//                .setPopUpTo(Screen.Splash.route,true)
//                .build()
//            appNavHostController.navigate(nextRoute,navOptions)
//        }
//    }
    DisposableEffect(Unit) {
        val observer = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResume() {
                scope.launch {
                    delay(500L)
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(Screen.Splash.route, true)
                        .build()
                    appNavHostController.navigate(nextRoute, navOptions)
                }
            }
        }
        lifecycleObserver.lifecycle.addObserver(observer)
        this.onDispose {
            lifecycleObserver.lifecycle.removeObserver(observer)
            scope.cancel()
        }
    }


//    val animate by animateFloatAsState(
//        if (!state) 0f else -50f,
//        animationSpec = tween(150, easing = LinearEasing)
//    )
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff77D3D0))
    ) {
        val (launcher, appName,version) = createRefs()
        Image(
            painter = painterResource(id = R.drawable.ic_music_launcher),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(launcher) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom, 50.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .size(100.dp)
        )
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 23.sp,
            modifier = Modifier
                .constrainAs(appName) {
                    top.linkTo(launcher.bottom,5.dp)
                    start.linkTo(launcher.start)
                    end.linkTo(launcher.end)
                }
        )
        Text(
            text = "Version: ${getVersion(LocalContext.current)}",
            modifier = Modifier
                .constrainAs(version){
                    bottom.linkTo(parent.bottom,20.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
    }
}