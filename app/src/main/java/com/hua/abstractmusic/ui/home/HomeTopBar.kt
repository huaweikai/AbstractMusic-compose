package com.hua.abstractmusic.ui.home

import android.annotation.SuppressLint
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.accompanist.insets.statusBarsPadding

/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 主页的actionbar
 */
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun HomeTopBar(
    label: String,
    imageVector: ImageVector,
    desc: String,
    actionOnclick: () -> Unit
) {
    SmallTopAppBar(
        title = {
            Text(text = label)
        },
        actions = {
            IconButton(onClick = {
                actionOnclick()
            }) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = desc
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier.statusBarsPadding()
    )
}
//fun HomeTopBar(
//    label: String,
//    modifier: Modifier,
//    navToDetail: Boolean,
//    onPreviewClick: () -> Unit,
//    navHostController: NavHostController = LocalHomeNavController.current,
//    viewModel: HomeViewModel = LocalHomeViewModel.current,
//) {
//
//    val iconSearch = TopBarIconButton(0, Icons.Default.Search, "搜索")
//    val iconRefresh = TopBarIconButton(1, Icons.Default.Refresh, "刷新")
//    val iconSettings = TopBarIconButton(2, Icons.Default.Settings, "设置")
//
//    val (icon, setIcon) = remember {
//        mutableStateOf(iconSearch)
//    }
//    val navigationIcon = remember {
//        mutableStateOf<@Composable (() -> Unit)?>(null)
//    }
//
//    LaunchedEffect(navHostController.currentBackStackEntryAsState().value) {
//        when (navHostController.currentDestination?.route) {
//            Screen.NetScreen.route -> {
//                setIcon(iconSearch)
//            }
//            Screen.LocalScreen.route -> {
//                setIcon(iconRefresh)
//            }
//            Screen.MineScreen.route -> {
//                setIcon(iconSettings)
//            }
//        }
//    }
//
//    LaunchedEffect(navToDetail) {
//        if (navToDetail) {
//            navigationIcon.value = {
//                NavigationIcon {
//                    onPreviewClick()
//                }
//            }
//        } else {
//            navigationIcon.value = null
//        }
//    }
//    TopAppBar(
//        title = {
//            Text(
//                text = label,
//                style = MaterialTheme.typography.titleLarge
//            )
//        },
//        navigationIcon = navigationIcon.value,
//        actions = {
//            IconButton(
//                onClick = {
//                    when (icon.id) {
//                        0 -> {}
//                        1 -> {
//                            viewModel.refresh()
//                        }
//                        2 -> {}
//                    }
//                },
//            ) {
//                Icon(
//                    imageVector = icon.icon,
//                    contentDescription = icon.desc,
//                    tint = Color(0xff77D3D0),
//                    modifier = Modifier
//                        .animateContentSize()
//                        .size((if (navToDetail) 0.dp else 30.dp))
//                )
//            }
//        },
//        elevation = 0.dp,
//        modifier = modifier,
//        backgroundColor = MaterialTheme.colorScheme.background
//    )
//}
//
//@Composable
//private fun NavigationIcon(
//    onClick: () -> Unit
//) {
//    Icon(
//        imageVector = Icons.Rounded.ArrowBack,
//        contentDescription = "",
//        Modifier
//            .clickable {
//                onClick()
//            }
//            .size(36.dp)
//    )
//}
