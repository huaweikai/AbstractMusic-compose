package com.hua.abstractmusic.ui.home.mine.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalUserViewModel
import com.hua.abstractmusic.ui.home.mine.LocalSheet
import com.hua.abstractmusic.ui.home.mine.Sheet
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.utils.UCropActivityResultContract
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
import com.hua.abstractmusic.utils.getCacheDir

/**
 * @author : huaweikai
 * @Date   : 2022/03/02
 * @Desc   :
 */
@Composable
fun LoggedScreen(
    navHostController: NavHostController = LocalHomeNavController.current,
    viewModel: UserViewModel = LocalUserViewModel.current
) {
    LaunchedEffect(Unit) {
        viewModel.refresh()
        viewModel.selectUserInfo()
    }

    val contentResolver = LocalContext.current.contentResolver
//    val cropPicture = rememberLauncherForActivityResult(CropPhotoContract()) {
//        viewModel.putHeadPicture(it.toString(), contentResolver)
//    }
    val context= LocalContext.current
    val cropPicture = rememberLauncherForActivityResult(UCropActivityResultContract()) {
        if(it != null){
            viewModel.putHeadPicture(it.toString(),contentResolver)
        }else{
            Toast.makeText(context, "连接为空", Toast.LENGTH_SHORT).show()
        }
    }

    val selectPicture = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            val outputUri = getCacheDir(context,it)
            cropPicture.launch(Pair(it,outputUri!!))
        }
    }

    Column(Modifier.fillMaxSize()) {
        val user = viewModel.user.collectAsState()
        Text(text = "已经登录了")
        ArtImage(
            modifier = Modifier
                .size(60.dp)
                .clickable {
                    selectPicture.launch("image/*")
                },
            uri = user.value.head,
            desc = "头像",
            transformation = CircleCropTransformation()
        )
        Text(text = user.value.userName)
        Button(onClick = {
            viewModel.logoutUser()
        }) {
            Text(text = "退出登录")
        }
        LocalSheet()
        Sheet(
            isLocal = false,
            onClick = { index ->
                navHostController.navigate("${Screen.LocalSheetDetailScreen.route}?sheetIndex=$index&isLocal=false")
            },
            newSheet = {
                viewModel.createSheet(it, false)
            },
            sheetList = viewModel.netSheetList
        )
    }
}