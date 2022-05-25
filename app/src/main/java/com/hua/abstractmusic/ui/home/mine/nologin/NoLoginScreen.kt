package com.hua.abstractmusic.ui.home.mine.nologin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.home.mine.LocalSheet
import com.hua.abstractmusic.ui.home.mine.login.LoginSetting
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
        LoginSetting(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navHostController.navigate(Screen.SettingScreen.route)
            }
            .height(42.dp)
        )
        Button(onClick = {
            navHostController.navigate(Screen.LoginScreen.route)
        }) {
            Text(text = "登录抽象音乐账号")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "畅享海量歌曲", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f))
        Spacer(modifier = Modifier.height(16.dp))
        LocalSheet()
    }
}