package com.hua.abstractmusic.ui.home

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalHomeViewModel
import com.hua.abstractmusic.ui.home.playlist.HomePlayList
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.navigation.HomeNavigationNav
import com.hua.abstractmusic.ui.play.PlayScreen
import com.hua.abstractmusic.ui.route.Screen
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 主界面，可以看作是activity
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = LocalHomeViewModel.current,
    homeNavController: NavHostController = LocalHomeNavController.current
) {

    val navToDetailState = remember {
        mutableStateOf(false)
    }

    val sheetListIsVisible = remember {
        mutableStateOf(false)
    }

    val sheetPlayIsVisible = remember {
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
            sheetListState.animateTo(ModalBottomSheetValue.HalfExpanded)
        }
    }

    val label = remember {
        mutableStateOf("")
    }

    val routes = listOf(Screen.LocalScreen.route, Screen.NetScreen.route, Screen.MineScreen.route)

    LaunchedEffect(homeNavController.currentBackStackEntryAsState().value) {
        homeNavController.currentDestination?.route.let {
            when {
                it in routes -> {
                    navToDetailState.value = true
                    label.value = "${homeNavController.currentDestination?.label}"
                }
                it?.startsWith(Screen.LocalAlbumDetail.route) == true -> {
                    navToDetailState.value = false
                    label.value = ""
                }
                it?.startsWith(Screen.LocalArtistDetail.route) == true -> {
                    navToDetailState.value = false
                    label.value = ""
                }
                it == Screen.LoginScreen.route -> {
                    navToDetailState.value = false
                    label.value = ""
                }
            }
        }
    }

    val translationBottom by animateDpAsState(
        if (navToDetailState.value) 120.dp else 60.dp,
        animationSpec = spring(1f, 100f)
    )
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
        HomeNavigationNav(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        )
    }
    HomePlayList(sheetListState)
    PlayScreen(sheetPlayState)
    BackHandler(
        sheetListIsVisible.value || sheetListIsVisible.value
    ) {
        when {
            sheetListState.isVisible -> {
                sheetListIsVisible.value = false
            }
            sheetPlayState.isVisible -> {
                sheetPlayIsVisible.value = false
            }
        }
    }
}
