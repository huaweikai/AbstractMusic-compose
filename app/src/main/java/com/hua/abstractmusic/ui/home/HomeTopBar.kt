package com.hua.abstractmusic.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hua.abstractmusic.bean.ui.home.TopBarIconButton
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.route.Screen

/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 主页的actionbar
 */
@Composable
fun HomeTopBar(
    label: String,
    modifier: Modifier,
    navToDetail: Boolean,
    onPreviewClick: () -> Unit,
    navHostController: NavHostController = LocalHomeNavController.current,
    viewModel: HomeViewModel = LocalHomeViewModel.current,
) {

    val iconSearch = TopBarIconButton(0, Icons.Default.Search, "搜索")
    val iconRefresh = TopBarIconButton(1, Icons.Default.Refresh, "刷新")
    val iconSettings = TopBarIconButton(2, Icons.Default.Settings, "设置")

    val (icon, setIcon) = remember {
        mutableStateOf(iconSearch)
    }
    val navigationIcon = remember {
        mutableStateOf<@Composable (() -> Unit)?>(null)
    }

    LaunchedEffect(navHostController.currentBackStackEntryAsState().value) {
        when (navHostController.currentDestination?.route) {
            Screen.NetScreen.route -> {
                setIcon(iconSearch)
            }
            Screen.LocalScreen.route -> {
                setIcon(iconRefresh)
            }
            Screen.MineScreen.route -> {
                setIcon(iconSettings)
            }
        }
    }

    LaunchedEffect(navToDetail) {
        if (!navToDetail) {
            navigationIcon.value = {
                NavigationIcon {
                    onPreviewClick()
                }
            }
        } else {
            navigationIcon.value = null
        }
    }
    TopAppBar(
        title = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = navigationIcon.value,
        actions = {
            IconButton(
                onClick = {
                    when (icon.id) {
                        0 -> {}
                        1 -> {
                            viewModel.refresh()
                        }
                        2 -> {}
                    }
                },
            ) {
                Icon(
                    imageVector = icon.icon,
                    contentDescription = icon.desc,
                    tint = Color(0xff77D3D0),
                    modifier = Modifier
                        .animateContentSize()
                        .size((if (navToDetail) 30.dp else 0.dp))
                )
            }
        },
        elevation = 0.dp,
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.background
    )
}

@Composable
private fun NavigationIcon(
    onClick: () -> Unit
) {
    Icon(
        imageVector = Icons.Rounded.ArrowBack,
        contentDescription = "",
        Modifier
            .clickable {
                onClick()
            }
            .size(36.dp)
    )
}
