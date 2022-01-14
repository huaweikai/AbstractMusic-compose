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
    var state by remember {
        mutableStateOf(false)
    }
    val lifecycleObserver = LocalLifecycleOwner.current


    LaunchedEffect(state){
        if(!state){
            delay(500L)
            state = true
        }
        if (state){
            delay(500L)
            val navOptions = NavOptions.Builder()
                .setPopUpTo(Screen.Splash.route,true)
                .build()
            appNavHostController.navigate(nextRoute,navOptions)
        }
    }


    val animate by animateFloatAsState(
        if (!state) 0f else -50f,
        animationSpec = tween(150, easing = LinearEasing)
    )
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff77D3D0))
    ) {
        val (launcher, appName) = createRefs()
        Image(
            painter = painterResource(id = R.drawable.ic_music_launcher),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(launcher) {
                    top.linkTo(parent.top, 50.dp)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .size(100.dp)
                .offset(0.dp, animate.dp)
        )
        if (state) {
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 23.sp,
                modifier = Modifier
                    .constrainAs(appName) {
                        top.linkTo(parent.top, 70.dp)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
        }
    }
}