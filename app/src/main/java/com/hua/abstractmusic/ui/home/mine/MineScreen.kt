package com.hua.abstractmusic.ui.home.mine

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.NavHostController
import com.hua.abstractmusic.bean.user.UserBean
import com.hua.abstractmusic.other.NetWork.SUCCESS
import com.hua.abstractmusic.ui.home.viewmodels.UserViewModel
import com.hua.abstractmusic.ui.route.Screen
import kotlinx.coroutines.launch


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 我的screen
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MineScreen(
    navHostController: NavHostController,
    viewModel: UserViewModel
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (viewModel.userIsOut.value) {
            NoLogin(navHostController = navHostController)
        } else {
            Mine(viewModel)
        }

    }


}

@Composable
fun NoLogin(
    navHostController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            navHostController.navigate(Screen.LoginScreen.route)
        }) {
            Text(text = "登录抽象音乐账号")
        }
        Text(text = "畅享海量歌曲")
    }
}

@Composable
fun Mine(
    viewModel: UserViewModel
) {
    val lifecycle = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    DisposableEffect(Unit) {
        val observer = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
                viewModel.selectUserInfo()
            }
        }
        lifecycle.lifecycle.addObserver(observer)
        this.onDispose {
            lifecycle.lifecycle.removeObserver(observer)
        }
    }
    Text(text = "已经登录了")

    Text(text = viewModel.user.value.userName)
    Button(onClick = {
        scope.launch {
            scope.launch {
                viewModel.logoutUser()
            }
        }

    }) {
        Text(text = "退出登录")
    }
}
