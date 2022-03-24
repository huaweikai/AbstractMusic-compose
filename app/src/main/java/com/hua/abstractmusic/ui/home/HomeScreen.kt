package com.hua.abstractmusic.ui.home

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.navigation.HomeNavigationNav
import com.hua.abstractmusic.ui.route.Screen

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 主界面，可以看作是activity
 */
val pages = listOf(MainPageItem.Net, MainPageItem.Local, MainPageItem.Mine)

@OptIn(
    ExperimentalMaterial3Api::class,
    com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi::class
)
@SuppressLint("UnsafeOptInUsageError")
@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(
//    onBack: () -> Unit,
    onSizeChange:(Dp)->Unit
) {
    val appNavHostController = LocalAppNavController.current
    DisposableEffect(Unit) {
        this.onDispose {
            onSizeChange(0.dp)
        }
    }
    val density = LocalDensity.current

    val backState = appNavHostController.currentBackStackEntryAsState().value
    val homeNavController: NavHostController = rememberNavController()

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                modifier = Modifier.onSizeChanged {
                    with(density) {
                        onSizeChange(it.height.toDp())
                    }
                },
                homeNavController
            )
        },
    )
    {
        HomeNavigationNav(homeNavController,
            Modifier
                .fillMaxSize()
                .padding(it))
//        BackHandler(backState?.destination?.route in controllerDone) {
//            onBack()
//        }
    }
}

@Composable
fun HomeBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val back = navController.currentBackStackEntryAsState()
    NavigationBar(
        modifier = modifier
    ) {
        pages.forEach { item ->
            NavigationBarItem(selected =
            item.route == back.value?.destination?.route, onClick = {
                navController.navigate(item.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }, icon = {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = null
                )
            }, label = {
                Text(text = stringResource(id = item.label))
            }, alwaysShowLabel = false
            )
        }
    }
}

sealed class MainPageItem(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int
) {
    object Net : MainPageItem(Screen.NetScreen.route, R.string.label_net, R.drawable.ic_line)
    object Mine :
        MainPageItem(Screen.MineScreen.route, R.string.label_mine, R.drawable.ic_person_icon)

    object Local :
        MainPageItem(Screen.LocalScreen.route, R.string.label_local, R.drawable.ic_music_icon)
}