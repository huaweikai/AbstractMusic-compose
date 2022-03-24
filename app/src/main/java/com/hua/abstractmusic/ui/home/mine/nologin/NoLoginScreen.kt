package com.hua.abstractmusic.ui.home.mine.nologin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.home.mine.LocalSheet
import com.hua.abstractmusic.ui.route.Screen


/**
 * @author : huaweikai
 * @Date   : 2022/03/02
 * @Desc   :
 */
@Composable
fun NoLoginScreen(
    navHostController: NavHostController = LocalAppNavController.current
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = WindowInsets.statusBars
                    .asPaddingValues()
                    .calculateTopPadding() + 32.dp
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            navHostController.navigate(Screen.LoginScreen.route)
        }) {
            Text(text = "登录抽象音乐账号")
        }
        Text(text = "畅享海量歌曲")
        Spacer(modifier = Modifier.height(16.dp))
        LocalSheet()
    }
}