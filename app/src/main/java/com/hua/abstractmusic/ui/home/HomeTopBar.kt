package com.hua.abstractmusic.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
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
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        val (backBt, title, search) = createRefs()
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "",
            Modifier
                .clickable {
                    navHostController.navigateUp()
                    viewModel.navigationState.value = true
                }
                .constrainAs(backBt) {
                    start.linkTo(parent.start, 10.dp)
                    end.linkTo(title.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .animateContentSize()
                .size((if (!viewModel.navigationState.value) 30.dp else 0.dp))
        )
        Text(
            text = maps[back?.route] ?: "",
            modifier = Modifier.constrainAs(title) {
                start.linkTo(backBt.end, 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            fontSize = 20.sp
        )
        IconButton(onClick = {

        },
            modifier = Modifier.constrainAs(search) {
                end.linkTo(parent.end,5.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "搜索",
                tint = Color(0xff77D3D0),
                modifier = Modifier
                    .animateContentSize()
                    .size((if (viewModel.navigationState.value) 24.dp else 0.dp))
            )
        }
    }
}
