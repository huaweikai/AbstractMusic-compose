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


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val playListState = viewModel.playListState.value
    val scope = rememberCoroutineScope()
    val homeNavController = rememberNavController()
    val translationTop by animateFloatAsState(
            if(viewModel.navigationState2.value) 42f else 0f,
            animationSpec = spring(1f, 100f)
        )

    val translationBottom by animateFloatAsState(
        if(viewModel.navigationState2.value) 130f else 60f,
        animationSpec = spring(1f,100f)
    )

//    val translationBottom = remember{
//        Animatable(130f)
//    }
    HomePlayList(viewModel = viewModel) {
        Scaffold(
            bottomBar = {
                HomeController(
                    homeNavController, viewModel,
                    Modifier
                        .fillMaxWidth()
                        .height(translationBottom.dp)
                ) {
                    scope.launch { playListState.show() }
                }
            },
            topBar = {
                HomeTopBar(
                    navHostController = homeNavController,
                    viewModel = viewModel,
                    modifier = Modifier
                        //todo(后续要自动计算每个手机的状态栏高度)
//                        .padding(top = viewModel.topBarState.value.value)
//                        .height(viewModel.topBarState.value.value)
                        .padding(top = translationTop.dp)
                        .height(translationTop.dp)
                        .background(Color.White)
                )
            }) {
            HomeNavigationNav(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = viewModel.navigationState.value.value),
                homeNavController = homeNavController,
                viewModel = viewModel
            )
        }
    }
}
