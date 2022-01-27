package com.hua.abstractmusic.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hua.abstractmusic.bean.ui.home.TopBarIconButton
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.route.Screen

/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 主页的actionbar
 */
@Composable
fun HomeTopBar(
    navHostController: NavHostController,
    viewModel: HomeViewModel,
    label:MutableState<String>,
    modifier: Modifier
) {
    val icon = remember {
        mutableStateOf(TopBarIconButton(Icons.Default.Search, "搜索"))
    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        val (backBt, title, search) = createRefs()
        Icon(
            imageVector = Icons.Rounded.ArrowBack,
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
                .size((if (!viewModel.navigationState.value) 24.dp else 0.dp))
        )
        Text(
            text = label.value,
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
                end.linkTo(parent.end, 15.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
        ) {
            when (navHostController.currentDestination?.route) {
                Screen.NetScreen.route -> {
                    icon.value.also {
                        it.icon = Icons.Default.Search
                        it.desc = "搜索"
                    }
                }
                Screen.LocalScreen.route -> {
                    icon.value.also {
                        it.icon = Icons.Default.Refresh
                        it.desc = "刷新"
                    }
                }
                Screen.MineScreen.route -> {
                    icon.value.also {
                        it.icon = Icons.Default.Settings
                        it.desc = "设置"
                    }
                }
            }
            Icon(
                imageVector = icon.value.icon,
                contentDescription = icon.value.desc,
                tint = Color(0xff77D3D0),
                modifier = Modifier
                    .animateContentSize()
                    .size((if (viewModel.navigationState.value) 30.dp else 0.dp))
                    .clickable {
                        when (icon.value.desc) {
                            "搜索" -> {

                            }
                            "刷新" -> {
                                viewModel.refresh()
                            }
                            "设置" -> {

                            }
                        }
                    }
            )
        }
    }
}
