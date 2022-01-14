package com.hua.abstractmusic.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.ui.home.playlist.HomePlayList
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.navigation.HomeNavigationNav
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 主界面，可以看作是activity
 */


@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    appNaviController: NavHostController,
    viewModel: HomeViewModel,
    homeNavController: NavHostController
) {
    val playListState = viewModel.playListState.value
    val scope = rememberCoroutineScope()
    val translationTop by animateFloatAsState(
        if (viewModel.navigationState.value) 0f else -80f,
        animationSpec = spring(1f, 100f)
    )

    val translationBottom by animateFloatAsState(
        if (viewModel.navigationState.value) 0f else 70f,
        animationSpec = spring(1f, 100f)
    )
    HomePlayList(viewModel = viewModel) {
        Scaffold(
            topBar = {
                HomeTopBar(
                    navHostController = homeNavController,
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                        .height(40.dp)
                )
            },
            bottomBar = {
                HomeController(
                    homeNavController, viewModel,
                    Modifier
                        .fillMaxWidth()
                        .offset(0.dp, translationBottom.dp)
                        .height(130.dp)
                ) {
                    scope.launch { playListState.show() }
                }
            }
        )
        {
            HomeNavigationNav(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = (if (viewModel.navigationState.value) 130 else 60).dp),
                homeNavController = homeNavController,
                viewModel = viewModel
            )
        }
    }
}
