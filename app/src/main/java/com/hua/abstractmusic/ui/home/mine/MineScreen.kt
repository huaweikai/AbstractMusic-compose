package com.hua.abstractmusic.ui.home.mine

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.home.viewmodels.UserViewModel
import com.hua.abstractmusic.ui.route.Screen


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
    Column(
        Modifier.fillMaxSize()
    ){
        if (viewModel.userIsOut.value) {
            Mine()
        } else {
            NoLogin(navHostController)
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
fun Mine() {
    Text(text = "已经登录了")
}
