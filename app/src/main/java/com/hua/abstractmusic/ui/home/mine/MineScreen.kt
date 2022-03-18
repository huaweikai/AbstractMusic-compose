package com.hua.abstractmusic.ui.home.mine

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.hua.abstractmusic.other.NetWork
import com.hua.abstractmusic.ui.LocalUserViewModel
import com.hua.abstractmusic.ui.home.mine.login.LoggedScreen
import com.hua.abstractmusic.ui.home.mine.nologin.NoLoginScreen
import com.hua.abstractmusic.ui.viewmodels.UserViewModel


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
        val result = viewModel.checkUser()
        if (result.code == NetWork.SERVER_ERROR) {
            Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
        }
    }
    DisposableEffect(Unit) {
        viewModel.refresh()
        this.onDispose {
        }
    }

    if (viewModel.userIsOut.value) {
        NoLoginScreen()
    } else {
        LoggedScreen()
    }

}