package com.hua.abstractmusic.ui.home.mine.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import com.hua.abstractmusic.bean.toNavType
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.LocalUserViewModel
import com.hua.abstractmusic.ui.home.mine.LocalSheet
import com.hua.abstractmusic.ui.home.mine.Sheet
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.CoilImage
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
    navHostController: NavHostController = LocalAppNavController.current,
    viewModel: UserViewModel = LocalUserViewModel.current
) {
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    val contentResolver = LocalContext.current.contentResolver
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
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ){
        
    }

    Column(Modifier.fillMaxSize()) {
        val user = viewModel.userInfo.collectAsState().value.userBean
        Text(text = "已经登录了")
        CoilImage(
            modifier = Modifier
                .size(60.dp)
                .clickable {
                    selectPicture.launch("image/*")
                },
            url = user?.head,
            contentDescription = "头像",
            builder = {
                transformations(CircleCropTransformation())
            }
        )
        Text(text = "${user?.userName}")
        Button(onClick = {
            viewModel.logoutUser()
        }) {
            Text(text = "退出登录")
        }
        LocalSheet()
        Sheet(
            isLocal = false,
            onClick = { mediaItem ->
                navHostController.navigate("${Screen.SheetDetailScreen.route}?mediaItem=${mediaItem.toNavType()}")
            },
            newSheet = {
                viewModel.createSheet(it, false)
            },
            sheetList = viewModel.netSheetList
        )
    }
}