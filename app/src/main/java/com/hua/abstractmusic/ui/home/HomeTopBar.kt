package com.hua.abstractmusic.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.utils.getStatusBarHeight

/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 主页的actionbar
 */
@Composable
fun HomeTopBar(
    navHostController: NavHostController,
    viewModel: HomeViewModel,
    modifier: Modifier
) {
    val back = navHostController.currentBackStackEntryAsState().value?.destination
    val maps = HashMap<String, String>()
    maps[Screen.NetScreen.route] = "在线音乐"
    maps[Screen.LocalScreen.route] = "本地音乐"
    maps[Screen.MineScreen.route] = "我的"
    val statusBarPadding = rememberInsetsPaddingValues(
        insets = LocalWindowInsets.current.statusBars, applyTop = true
    )
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = maps[back?.route]?:"")
        },
        actions = {
            IconButton(onClick = {

            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "搜索",
                    tint = Color(0xff77D3D0)
                )
            }
        },
        backgroundColor = Color.White,
        elevation = 0.dp

    )
}
