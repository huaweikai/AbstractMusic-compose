package com.hua.abstractmusic.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.hua.abstractmusic.ui.home.viewmodels.HomeViewModel
import com.hua.abstractmusic.ui.navigation.HomeNavigationNav
import com.hua.abstractmusic.ui.route.Screen

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 主界面，可以看作是activity
 */

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeNavController = rememberNavController()
    Scaffold(
        bottomBar = {
            HomeController(homeNavController,viewModel)
        },
        topBar = {
            HomeTopBar(navHostController = homeNavController)
        },

        ) {
        HomeNavigationNav(
            modifier = Modifier.fillMaxSize(),
            homeNavController = homeNavController,
            viewModel = viewModel
        )
        viewModel.initializeController()
    }
}
