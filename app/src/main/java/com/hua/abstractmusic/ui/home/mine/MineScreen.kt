package com.hua.abstractmusic.ui.home.mine

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.hua.abstractmusic.ui.LocalUserViewModel
import com.hua.abstractmusic.ui.home.mine.login.LoggedScreen
import com.hua.abstractmusic.ui.home.mine.nologin.NoLoginScreen
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
import com.hua.network.ApiResult


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 我的screen
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MineScreen(
    viewModel: UserViewModel = LocalUserViewModel.current
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if(viewModel.userInfo.value.isLogin){
            val result =  viewModel.checkUser()
            if (result is ApiResult.Failure) {
                Toast.makeText(context, result.error.errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }
    DisposableEffect(Unit) {
        viewModel.refresh()
        this.onDispose {
        }
    }
    val userInfo = viewModel.userInfo.collectAsState()

    if (!userInfo.value.isLogin) {
        NoLoginScreen()
    } else {
        LoggedScreen()
    }

}