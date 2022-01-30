package com.hua.abstractmusic.ui.home.mine

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.contentValuesOf
import androidx.lifecycle.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.hua.abstractmusic.R
import com.hua.abstractmusic.bean.CropParams
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.other.NetWork.NO_USER
import com.hua.abstractmusic.other.NetWork.SERVER_ERROR
import com.hua.abstractmusic.other.NetWork.SUCCESS
import com.hua.abstractmusic.ui.home.viewmodels.UserViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.utils.CropPhotoContract
import kotlinx.coroutines.launch
import java.io.File


/**
 * @author : huaweikai
 * @Date   : 2022/01/08
 * @Desc   : 我的screen
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MineScreen(
    navHostController: NavHostController,
    viewModel: UserViewModel
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val result = viewModel.checkUser()
        if (result.code == SERVER_ERROR) {
            Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
        }
    }
    if(viewModel.userIsOut.value){
        NoLogin(navHostController = navHostController)
    }else{
        Mine(viewModel = viewModel)
    }
}

@Composable
fun NoLogin(
    navHostController: NavHostController
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
    viewModel: UserViewModel
) {
    val result = remember {
        mutableStateOf("")
    }
    val contentResolver = LocalContext.current.contentResolver
    val cropPicture = rememberLauncherForActivityResult(CropPhotoContract()) {
//        result.value = it.toString()
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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).apply {
                data(viewModel.user.value.head)
                error(R.drawable.music)
                transformations(CircleCropTransformation())
            }.build(),
            contentDescription = "头像",
            modifier = Modifier
                .size(60.dp)
                .clickable {
                    selectPicture.launch("image/*")
                }
        )

//        Button(onClick = {
//
//        }) {
//            Text(text = "提交头像")
//        }
        Text(text = viewModel.user.value.userName)
        Button(onClick = {
            viewModel.logoutUser()
        }) {
            Text(text = "退出登录")
        }
    }
}
