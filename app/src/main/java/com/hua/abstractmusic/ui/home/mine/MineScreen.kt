package com.hua.abstractmusic.ui.home.mine

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 我的screen
 */
@Composable
fun MineScreen(navHostController:NavHostController){
    val back = navHostController.currentBackStackEntryAsState().value?.destination
    Text(text = "${back?.label}")
}