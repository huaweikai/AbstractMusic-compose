package com.hua.abstractmusic.ui.home.mine.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hua.abstractmusic.bean.toNavType
import com.hua.abstractmusic.bean.user.UserBean
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.LocalBottomControllerHeight
import com.hua.abstractmusic.ui.LocalUserViewModel
import com.hua.abstractmusic.ui.home.mine.CreateNewSheet
import com.hua.abstractmusic.ui.home.mine.LocalSheet
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.CoilImage
import com.hua.abstractmusic.ui.utils.SheetItem
import com.hua.abstractmusic.ui.utils.SnackBarDataWithError
import com.hua.abstractmusic.ui.utils.UCropActivityResultContract
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
import com.hua.abstractmusic.utils.getCacheDir
import com.hua.abstractmusic.utils.toTime

/**
 * @author : huaweikai
 * @Date   : 2022/03/02
 * @Desc   :
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoggedScreen(
    navHostController: NavHostController = LocalAppNavController.current,
    viewModel: UserViewModel = LocalUserViewModel.current
) {
    LaunchedEffect(Unit) {
        viewModel.refresh()
        viewModel.selectNetWork()
    }
    val controllerHeight = LocalBottomControllerHeight.current


    val user = viewModel.userInfo.collectAsState().value.userBean
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState) {
                val vis = it.visuals as? SnackBarDataWithError
                Snackbar(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    action = {

                    }
                ) {
                    Text(text = "${vis?.message}")
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        PaddingValues(
                            bottom = controllerHeight.coerceAtLeast(16.dp)
                        )
                    )
                    .padding(it)
            ) {
                item {
                    LoginSetting(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .clickable {
                                navHostController.navigate(Screen.SettingScreen.route)
                            }
                            .height(56.dp)
                    )
                }
                item {
                    UserInfo(
                        data = user,
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp), viewModel = viewModel
                    )
                }
                item {
                    LocalSheet()
                }
                item {
                    SubscribedPlayList(viewModel = viewModel, navHostController = navHostController)
                }
            }
        }
    }
}

@Composable
fun SubscribedPlayList(
    viewModel: UserViewModel,
    navHostController: NavHostController
) {
    val diaLogState = remember {
        mutableStateOf(false)
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "自建歌单 ${viewModel.netSheetList.value.size}",
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.padding(start = 8.dp)
                )
                TextButton(
                    onClick = {
                        diaLogState.value = true
                    },
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "")
                    Text(text = "新建")
                }
            }

            viewModel.netSheetList.value.forEach {
                SheetItem(item = it, onClick = {
                    navHostController.navigate("${Screen.SheetDetailScreen.route}?mediaItem=${it.toNavType()}")
                })
            }
        }
        CreateNewSheet(diaLogState = diaLogState) {
            viewModel.createSheet(it, false)
        }
    }
}


@Composable
fun UserInfo(
    data: UserBean?,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 80.dp,
    viewModel: UserViewModel
) {
    if (data == null) return
    val contentResolver = LocalContext.current.contentResolver
    val context = LocalContext.current
    val cropPicture = rememberLauncherForActivityResult(UCropActivityResultContract()) {
        if (it != null) {
            viewModel.putHeadPicture(it.toString(), contentResolver)
        } else {
            Toast.makeText(context, "连接为空", Toast.LENGTH_SHORT).show()
        }
    }

    val selectPicture = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            val outputUri = getCacheDir(context, it)
            cropPicture.launch(Pair(it, outputUri!!))
        }
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier.padding(top = avatarSize / 2),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .padding(top = (avatarSize - 16.dp) / 2)
            ) {
                Text(
                    text = data.userName,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                val day = (System.currentTimeMillis() - data.createTime.toTime()) / 864e5
                Text(text = "${day.toInt()} 天")
            }
        }
        CoilImage(
            url = data.head,
            modifier = Modifier
                .size(avatarSize)
                .clickable {
                    selectPicture.launch("image/*")
                },
            shape = CircleShape
        )
    }
}

@Composable
fun LoginSetting(modifier: Modifier) {
    Box(modifier = modifier) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "",
            modifier = Modifier
                .align(
                    Alignment.BottomEnd
                )
                .padding(bottom = 16.dp, end = 16.dp)
        )
    }
}
