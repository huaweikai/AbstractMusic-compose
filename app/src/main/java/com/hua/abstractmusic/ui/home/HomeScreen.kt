package com.hua.abstractmusic.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hua.abstractmusic.ui.home.playlist.HomePlayList
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.navigation.HomeNavigationNav
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
    val bottomNavController = rememberNavController()
//    val duration = 100
//    val easingType = FastOutSlowInEasing
//    val bottomHeight by animateIntAsState(
//        if (viewModel.navigationState2.value) 130 else 60,
//        animationSpec = tween(delayMillis = duration, easing = easingType)
//    )
    val dampingRtio = 1f
    val stiffness = 100f
    val bottomHeight by animateFloatAsState(
        if (viewModel.navigationState2.value) 130f else 60f,
        animationSpec = spring(dampingRtio,stiffness)
    )
    HomePlayList(viewModel = viewModel) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ){
            val (content,bottomController) = createRefs()
            HomeNavigationNav(
                homeNavController,
                bottomNavController,
                viewModel,
                modifier = Modifier.constrainAs(content){
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(bottomController.top)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
            )
            HomeController(
                bottomNavController,
                viewModel,
                Modifier
                    .constrainAs(bottomController) {
                        start.linkTo(parent.start)
                        top.linkTo(content.bottom)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxWidth()
                    .height(bottomHeight.dp)
            ) {
                scope.launch { playListState.show() }
            }
        }
    }
}
