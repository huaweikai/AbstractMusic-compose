package com.hua.abstractmusic.ui.home.mine

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.hua.abstractmusic.bean.CropParams
import com.hua.abstractmusic.other.NetWork.SERVER_ERROR
import com.hua.abstractmusic.ui.LocalHomeNavController
import com.hua.abstractmusic.ui.LocalUserViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.ArtImage
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
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
    DisposableEffect(Unit) {
        viewModel.initializeController()
        this.onDispose {
            viewModel.releaseBrowser()
        }
    }

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
    navHostController: NavHostController = LocalHomeNavController.current,
    viewModel: UserViewModel = LocalUserViewModel.current
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
        LocalSheet()
    }
}

@Composable
fun Mine(
    viewModel: UserViewModel = LocalUserViewModel.current,
    navHostController: NavHostController = LocalHomeNavController.current
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
    LocalSheet()
    Sheet(
        isLocal = false,
        onClick = { index ->
            navHostController.navigate("${Screen.LocalSheetDetailScreen.route}?sheetIndex=$index&isLocal=false")
        },
        newSheet = {

        }
    )
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun Sheet(
    isLocal: Boolean = true,
    onClick: (Int) -> Unit,
    newSheet: (String) -> Unit,
    userViewModel: UserViewModel = LocalUserViewModel.current,
) {
    val diaLogState = remember {
        mutableStateOf(false)
    }
    Column(
        Modifier
            .height(140.dp)
            .fillMaxWidth(0.9f)
            .background(
                MaterialTheme.colorScheme.background.copy(0.2f),
                RoundedCornerShape(12.dp)
            ),
    ) {
        Row(
            modifier = Modifier
                .height(25.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = if (isLocal) "本地歌单" else "在线歌单", fontSize = 16.sp)
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "",
                modifier = Modifier.clickable {
                    diaLogState.value = true
                }
            )
        }
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            itemsIndexed(
                if (isLocal) userViewModel.sheetList.value
                else userViewModel.netSheetList.value
            ) { index, sheet ->
                Column(
                    modifier = Modifier
                        .height(100.dp)
                        .padding(horizontal = 8.dp)
                        .width(70.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ArtImage(
                        modifier = Modifier
                            .size(70.dp)
                            .clickable {
                                onClick(index)
                            },
                        uri = sheet.mediaItem.mediaMetadata.artworkUri,
                        desc = "",
                        transformation = RoundedCornersTransformation(15f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${sheet.mediaItem.mediaMetadata.title}")
                }
            }
        }
    }
    CreateNewSheet(diaLogState = diaLogState) {
        newSheet(it)
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun LocalSheet(
    navHostController: NavHostController = LocalHomeNavController.current,
    userViewModel: UserViewModel = LocalUserViewModel.current
) {
    Sheet(
        onClick = { index ->
            navHostController.navigate("${Screen.LocalSheetDetailScreen.route}?sheetIndex=$index&isLocal=true")
        },
        newSheet = {
            userViewModel.createSheet(it)
        }
    )
}

@Composable
fun CreateNewSheet(
    diaLogState: MutableState<Boolean>,
    confirm: (String) -> Unit
) {
    val (sheetName, setSheetName) = remember {
        mutableStateOf("")
    }
    if (diaLogState.value) {
        AlertDialog(
            onDismissRequest = {
                diaLogState.value = false
                setSheetName("")
            },
            confirmButton = {
                Text(
                    text = "添加",
                    modifier = Modifier.clickable {
                        confirm(sheetName)
                        diaLogState.value = false
                        setSheetName("")
                    }
                )
            },
            dismissButton = {
                Text(
                    text = "取消",
                    modifier = Modifier.clickable {
                        diaLogState.value = false
                        setSheetName("")
                    }
                )
            },
            title = {
                Text(text = "添加歌单", fontSize = 20.sp)
            },
            text = {
                TextField(
                    value = sheetName,
                    onValueChange = setSheetName
                )
            }
        )
    }
}
