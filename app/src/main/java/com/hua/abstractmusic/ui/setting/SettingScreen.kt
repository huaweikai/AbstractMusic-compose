package com.hua.abstractmusic.ui.setting

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.RangeSlider
import androidx.compose.material.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.LocalThemeViewModel
import com.hua.abstractmusic.ui.LocalUserViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.theme.defaultColor
import com.hua.abstractmusic.ui.viewmodels.ThemeViewModel
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
import com.hua.abstractmusic.utils.toTime


/**
 * @author : huaweikai
 * @Date   : 2022/03/28
 * @Desc   :
 */
@Composable
fun SettingScreen() {
    val settNavController = rememberNavController()
    NavHost(navController = settNavController, startDestination = "setting_main") {
        composable("setting_main") {
            SettingMain(settNavController)
        }
        composable("setting_theme") {
            ThemeScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingMain(
    settingNavController: NavHostController,
) {
    val themeViewModel: ThemeViewModel = LocalThemeViewModel.current
    val settingViewModel: SettingViewModel = hiltViewModel()
    val appNavController: NavHostController = LocalAppNavController.current
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(text = "设置") },
                modifier = Modifier.statusBarsPadding(),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { appNavController.navigateUp() })
                }
            )
        }
    ) {
        Column(Modifier.padding(it)) {
            SettingSwitchItem(
                title = "是否开启跟随壁纸主题色",
                switchState = themeViewModel.monetColor.value == null,
                onSwitch = {
                    if (it) {
                        themeViewModel.closeCustomThemeColor()
                    } else {
                        themeViewModel.setCustomThemeColor(defaultColor.toArgb())
                    }
                },
                enabled = themeViewModel.hasWallPermission,
                subTitle = "此功能只支持安卓8以上"
            )
            AnimatedVisibility(visible = themeViewModel.monetColor.value != null) {
                SettingItem(title = "设置自定义主题色") {
                    settingNavController.navigate("setting_theme")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val isOpen = settingViewModel.timeOpen.collectAsState()
            SettingSwitchItem(title = "定时播放", switchState = isOpen.value, onSwitch = {
                settingViewModel.startTimer(0F,it)
            }, enabled = true)
            AnimatedVisibility(visible = isOpen.value, modifier =  Modifier.padding(horizontal = 16.dp)) {
                Column {
                    Slider(value = settingViewModel.timeSlider.value, onValueChange = {
                        settingViewModel.startTimer(it)
                    }, valueRange = 0F..5F, steps = 4)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        val time = settingViewModel.mediaTime.collectAsState()
                        Text(text = "${(settingViewModel.timeSlider.value.toInt() + 1) * 5}分钟")
                        Text(text = "剩余${time.value.toTime()}")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            val loginState = settingViewModel.userInfo.collectAsState().value.isLogin
            if (loginState) {
                Button(
                    onClick = {
                        settingViewModel.logoutUser()
                        appNavController.navigateUp()
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "退出登录")
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    onclick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clickable {
                onclick()
            }
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title)
        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = title)
    }
}

@Composable
fun SettingSwitchItem(
    title: String,
    switchState: Boolean,
    onSwitch: (Boolean) -> Unit,
    subTitle: String? = null,
    enabled: Boolean
) {
    Column(modifier = Modifier.height(62.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(46.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title)
            Switch(checked = switchState, onCheckedChange = {
                onSwitch(it)
            }, enabled = enabled)
        }
        if (subTitle?.isNotBlank() == true) {
            Text(
                text = subTitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 24.dp)
            )
        }
    }
}
