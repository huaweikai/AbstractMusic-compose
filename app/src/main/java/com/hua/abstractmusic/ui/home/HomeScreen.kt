package com.hua.abstractmusic.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.home.playlist.HomePlayList
import com.hua.abstractmusic.ui.navigation.HomeNavigationNav
import com.hua.abstractmusic.ui.play.PlayScreen
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.viewmodels.HomeViewModel

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 主界面，可以看作是activity
 */
@ExperimentalFoundationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun HomeScreen(
    homeNavController: NavHostController = LocalHomeNavController.current
) {

    val navToDetailState = rememberSaveable {
        mutableStateOf(false)
    }

    val sheetListIsVisible = rememberSaveable {
        mutableStateOf(false)
    }

    val sheetPlayIsVisible = rememberSaveable {
        mutableStateOf(false)
    }

    val sheetListState by remember {
        mutableStateOf(
            ModalBottomSheetState(ModalBottomSheetValue.Hidden)
        )
    }

    val sheetPlayState by remember {
        mutableStateOf(
            ModalBottomSheetState(ModalBottomSheetValue.Hidden, isSkipHalfExpanded = true)
        )
    }

    LaunchedEffect(sheetListState.currentValue) {
        if (!sheetListState.isVisible) {
            sheetListIsVisible.value = false
        }
    }

    LaunchedEffect(sheetPlayState.currentValue) {
        if (!sheetPlayState.isVisible) {
            sheetPlayIsVisible.value = false
        }
    }

    LaunchedEffect(sheetPlayIsVisible.value) {
        if (!sheetPlayIsVisible.value) {
            sheetPlayState.animateTo(ModalBottomSheetValue.Hidden)
        } else {
            sheetPlayState.animateTo(ModalBottomSheetValue.Expanded)
        }
    }

    LaunchedEffect(sheetListIsVisible.value) {
        if (!sheetListIsVisible.value) {
            sheetListState.animateTo(ModalBottomSheetValue.Hidden)
        } else {
            sheetListState.animateTo(ModalBottomSheetValue.Expanded)
        }
    }

    val label = remember {
        mutableStateOf("")
    }

    val routes = listOf(Screen.LocalScreen.route, Screen.NetScreen.route, Screen.MineScreen.route)

    LaunchedEffect(homeNavController.currentBackStackEntryAsState().value) {
        homeNavController.currentDestination?.route.let {
            when (it) {
                in routes -> {
                    navToDetailState.value = true
                    label.value = "${homeNavController.currentDestination?.label}"
                }
                else -> {
                    navToDetailState.value = false
                    label.value = ""
                }
            }
        }
    }

//    val translationBottom by animateDpAsState(
//        if (navToDetailState.value) 120.dp else 60.dp,
//        animationSpec = spring(1f, 100f)
//    )
    Scaffold(
        topBar = {
            HomeTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 42.dp)
                    .height(42.dp),
                label = label.value,
                navToDetail = navToDetailState.value,
                onPreviewClick = {
                    homeNavController.navigateUp()
                    navToDetailState.value = true
                }
            )
        },
        bottomBar = {
            HomeController(
                Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .height(if (navToDetailState.value) 120.dp else 60.dp)
                    .background(MaterialTheme.colorScheme.background),
                {
                    sheetListIsVisible.value = true
                }, {
                    sheetPlayIsVisible.value = true
                }
            )
        }
    )
    {
        BackHandler(
            sheetListIsVisible.value || sheetPlayIsVisible.value
        ) {
            when {
                sheetListIsVisible.value -> {
                    sheetListIsVisible.value = false
                }
                sheetPlayIsVisible.value -> {
                    sheetPlayIsVisible.value = false
                }
            }
        }
        HomeNavigationNav(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        )
    }
    HomePlayList(sheetListState)
    PlayScreen(sheetPlayState)
}
