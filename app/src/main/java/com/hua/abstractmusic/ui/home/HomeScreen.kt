package com.hua.abstractmusic.ui.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.R
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalPlayingViewModel
import com.hua.abstractmusic.ui.home.playlist.HomePlayList
import com.hua.abstractmusic.ui.navigation.HomeNavigationNav
import com.hua.abstractmusic.ui.play.PlayScreen
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.PopupWindow
import com.hua.abstractmusic.ui.viewmodels.PlayingViewModel
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 主界面，可以看作是activity
 */
val pages = listOf(MainPageItem.Net, MainPageItem.Local, MainPageItem.Mine)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnsafeOptInUsageError")
@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    homeNavController: NavHostController = LocalHomeNavController.current,
    playingViewModel: PlayingViewModel = LocalPlayingViewModel.current
) {
    var bottomControllerHeight by remember {
        mutableStateOf(0.dp)
    }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val bottomBarHeight = remember {
        mutableStateOf(0.dp)
    }

    val navToDetailState = rememberSaveable {
        mutableStateOf(false)
    }

    val sheetIsVisible = rememberSaveable {
        mutableStateOf(false)
    }

    val sheetListState = rememberSaveable(
        saver = ModalBottomSheetState.Saver(
            animationSpec = SwipeableDefaults.AnimationSpec,
            skipHalfExpanded = false,
            confirmStateChange = { true }
        )
    ) {
        ModalBottomSheetState(ModalBottomSheetValue.Hidden)
    }

    val sheetPlayState = rememberSaveable(
        saver = ModalBottomSheetState.Saver(
            animationSpec = SwipeableDefaults.AnimationSpec,
            skipHalfExpanded = true,
            confirmStateChange = { true }
        )
    ) {
        ModalBottomSheetState(ModalBottomSheetValue.Hidden, isSkipHalfExpanded = true)
    }

    LaunchedEffect(sheetListState.currentValue, sheetPlayState.currentValue) {
        sheetIsVisible.value = sheetPlayState.isVisible || sheetListState.isVisible
    }

    val label = remember {
        mutableStateOf("")
    }
    val backState = homeNavController.currentBackStackEntryAsState()

    val translationBottom by animateDpAsState(
        if (backState.value?.destination?.route in pages.map { it.route }) 80.dp else 0.dp,
//        animationSpec = tween(300)
    )

    PlayScreen(state = sheetPlayState) {
        HomePlayList(sheetListState) {
            Scaffold(
                bottomBar = {
                    AnimatedVisibility(
                        visible = backState.value?.destination?.route in pages.map { it.route },
                        enter = slideInVertically { fullHeight -> fullHeight },
                        exit = slideOutVertically { fullHeight -> fullHeight },
                        modifier = Modifier.height(80.dp)
                    ) {
                        HomeBottomBar()
                    }

                },
            )
            {
                val bottomPadding by animateDpAsState(
                    if (backState.value?.destination?.route in pages.map { it.route }) 80.dp else 0.dp,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = bottomPadding)
                ) {
                    CompositionLocalProvider(LocalBottomControllerHeight provides bottomControllerHeight) {
                        HomeNavigationNav(Modifier)
                    }
                    BackHandler(
                        sheetIsVisible.value
                    ) {
                        val state = if (sheetListState.isVisible) sheetListState else sheetPlayState
                        scope.launch {
                            state.animateTo(ModalBottomSheetValue.Hidden)
                        }
                    }
                    AnimatedVisibility(
                        visible = playingViewModel.currentPlayItem.value != Constant.NULL_MEDIA_ITEM,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .onSizeChanged {
                                bottomControllerHeight = with(density) { it.height.toDp() }
                            }
                    ) {
                        Surface(
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            tonalElevation = 3.dp,
                        ) {
                            Controller(
                                playListClick = {
                                    scope.launch { sheetListState.animateTo(ModalBottomSheetValue.Expanded) }
                                },
                                playScreenClick = {
                                    scope.launch {
                                        sheetPlayState.animateTo(ModalBottomSheetValue.Expanded)
                                    }
                                })
                        }

                    }
                }
            }
        }
    }
    PopupWindow()
}

@Composable
fun HomeBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController = LocalHomeNavController.current
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