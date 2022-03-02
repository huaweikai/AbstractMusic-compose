package com.hua.abstractmusic.ui.home.mine.nologin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.home.mine.LocalSheet
import com.hua.abstractmusic.ui.route.Screen


/**
 * @author : huaweikai
 * @Date   : 2022/03/02
 * @Desc   :
 */
@Composable
fun NoLoginScreen(
    navHostController: NavHostController = LocalHomeNavController.current
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
        LocalSheet()
    }
}