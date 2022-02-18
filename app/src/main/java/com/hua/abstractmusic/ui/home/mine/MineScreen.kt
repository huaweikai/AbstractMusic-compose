package com.hua.abstractmusic.ui.home.mine

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import com.hua.abstractmusic.bean.CropParams
import com.hua.abstractmusic.other.NetWork.SERVER_ERROR
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalUserViewModel
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.utils.CropPhotoContract


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 我的screen
 */
@Composable
fun MineScreen(
    viewModel: UserViewModel = LocalUserViewModel.current
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val result = viewModel.checkUser()
        if (result.code == SERVER_ERROR) {
            Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
        }
    }
    if (viewModel.userIsOut.value) {
        NoLogin()
    } else {
        Mine()
    }
}

@Composable
fun NoLogin(
    navHostController: NavHostController = LocalHomeNavController.current
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            navHostController.navigate(Screen.LoginScreen.route)
        }) {
            Text(text = "登录抽象音乐账号")
        }
        Text(text = "畅享海量歌曲")
    }
}

@Composable
fun Mine(
    viewModel: UserViewModel = LocalUserViewModel.current
) {
    val contentResolver = LocalContext.current.contentResolver
    val cropPicture = rememberLauncherForActivityResult(CropPhotoContract()) {
        viewModel.putHeadPicture(it.toString(), contentResolver)
    }

    val selectPicture = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            cropPicture.launch(CropParams(uri = it))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.selectUserInfo()
    }

    Column(Modifier.fillMaxSize()) {
        Text(text = "已经登录了")
        ArtImage(
            modifier = Modifier
                .size(60.dp)
                .clickable {
                    selectPicture.launch("image/*")
                },
            uri = Uri.parse(viewModel.user.value.head),
            desc = "头像",
            transformation = CircleCropTransformation()
        )
        Text(text = viewModel.user.value.userName)
        Button(onClick = {
            viewModel.logoutUser()
        }) {
            Text(text = "退出登录")
        }
    }
}
