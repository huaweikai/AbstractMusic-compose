package com.hua.abstractmusic.ui.home.local

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 本地音乐screen
 */
@Composable
fun LocalScreen(navHostController:NavHostController){
    val back = navHostController.currentBackStackEntryAsState().value?.destination
    Text(text = "${back?.label}")
}