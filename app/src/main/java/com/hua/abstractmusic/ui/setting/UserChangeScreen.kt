package com.hua.abstractmusic.ui.setting

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.LocalUserViewModel
import com.hua.abstractmusic.ui.utils.CoilImage
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.abstractmusic.ui.utils.LifecycleFocusClearUtils
import com.hua.abstractmusic.ui.utils.UCropActivityResultContract
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
import com.hua.abstractmusic.utils.getCacheDir
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/05/07
 * @Desc   : 用户修改
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserChangeScreen(
    settingNavController: NavHostController,
    userChangeViewModel: UserChangeViewModel = hiltViewModel()
) {
    val userInfo = userChangeViewModel.userInfo.value
    LifecycleFocusClearUtils()
    val userState = userChangeViewModel.userStateInfo.collectAsState()
    LaunchedEffect(key1 = userState.value) {
        if (!userState.value.isLogin) {
            settingNavController.navigateUp()
        }
    }
    val userUpdateState = userChangeViewModel.userUpdateState.collectAsState()

    val contentResolver = LocalContext.current.contentResolver
    val context = LocalContext.current
    val cropPicture = rememberLauncherForActivityResult(UCropActivityResultContract()) {
        if (it != null) {
            userChangeViewModel.sendAction(UserChangeAction.UserHead(it.toString()))
        } else {
            Toast.makeText(context, "连接为空", Toast.LENGTH_SHORT).show()
        }
    }
    val snackbarData = SnackbarHostState()

    val scope = rememberCoroutineScope()

    val checkState = remember { mutableStateOf(false) }

    val selectPicture = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            val outputUri = getCacheDir(context, it)
            cropPicture.launch(Pair(it, outputUri!!))
        }
    }
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    settingNavController.navigateUp()
                }) {
                    Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "返回")
                }
                Text(text = "编辑用户", fontSize = 18.sp)
                IconButton(
                    onClick = {
                        userChangeViewModel.sendAction(
                            UserChangeAction.SaveUser(
                                contentResolver,
                                success = {
                                    settingNavController.navigateUp()
                                },
                                error = {
                                    scope.launch {
                                        snackbarData.showSnackbar(it)
                                    }
                                }
                            )
                        )
                    },
                    enabled = userUpdateState.value == LCE.Success
                ) {
                    when (userUpdateState.value) {
                        is LCE.Success -> Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = "保存"
                        )
                        is LCE.Loading -> CircularProgressIndicator()
                        is LCE.Error -> Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = "错误",
                            tint = Color.Red
                        )
                    }
                }
            }
        },
        modifier = Modifier.statusBarsPadding(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarData) {
                Snackbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 16.dp)
                ) {
                    Text(text = it.visuals.message)
                }
            }
        }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            constraintSet = userChangeConstraintSet()
        ) {
            CoilImage(
                url = userInfo.head,
                modifier = Modifier
                    .layoutId("head")
                    .clip(CircleShape)
                    .size(80.dp)
                    .clickable {
                        selectPicture.launch("image/*")
                    }
            )
            OutlinedTextField(
                modifier = Modifier.layoutId("userName"),
                value = userInfo.userName, onValueChange = {
                    userChangeViewModel.sendAction(UserChangeAction.UserName(it))
                },
                label = { Text(text = "用户名") }
            )
            OutlinedTextField(
                modifier = Modifier.layoutId("email"),
                value = userInfo.email, onValueChange = {},
                enabled = false,
                label = { Text(text = "邮箱") }
            )
            OutlinedTextField(
                modifier = Modifier.layoutId("createTime"),
                value = userInfo.createTime, onValueChange = {},
                enabled = false,
                label = { Text(text = "注册时间") }
            )
            Button(
                onClick = {
                    checkState.value = true
                },
                modifier = Modifier.layoutId("deleteUser")
            ) {
                Text(text = "销毁账户")
            }
        }
    }

    DeleteUserCheck(
        visState = checkState,
        userChangeViewModel = userChangeViewModel,
        success = {
            settingNavController.navigateUp()
        },
        error = {
            scope.launch {
                snackbarData.showSnackbar(it)
            }
        }
    )

//    LaunchedEffect(userChangeViewModel.userUpdateState.value){
//        icon.value = when(userChangeViewModel.userUpdateState.value){
//            is LCE.Success -> Icons.Default.Send
//            is LCE.Loading -> Icons.Filled
//        }
//    }

}

@Composable
private fun DeleteUserCheck(
    visState: MutableState<Boolean>,
    userChangeViewModel: UserChangeViewModel,
    success: () -> Unit,
    error: (String) -> Unit,
) {
    if (visState.value) {
        AlertDialog(
            onDismissRequest = { visState.value = false },
            dismissButton = {
                TextButton(onClick = {
                    visState.value = false
                }) {
                    Text(text = "取消")
                }
            },
            title = {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = "警告",
                        tint = Color.Red
                    )
                    Text(text = "请输入密码验证信息")
                }
            },
            text = {
                TextField(
                    value = userChangeViewModel.userPassWordCheck.value, onValueChange = {
                        userChangeViewModel.sendAction(
                            UserChangeAction.UserPassWord(it)
                        )
                    },
                    maxLines = 1
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    userChangeViewModel.sendAction(UserChangeAction.DeleteUser(
                        success = {
                            visState.value = false
                            success()
                        },
                        error = {
                            visState.value = false
                            error(it)
                        }
                    ))
                }) {
                    Text(text = "确定销毁")
                }
            }
        )
    }

    LaunchedEffect(visState.value){
        if(!visState.value){
            userChangeViewModel.sendAction(UserChangeAction.UserPassWord(""))
        }
    }

}

fun userChangeConstraintSet(): ConstraintSet {
    return ConstraintSet {
        val head = createRefFor("head")
        val userName = createRefFor("userName")
        val email = createRefFor("email")
        val createTime = createRefFor("createTime")
        val deleteUser = createRefFor("deleteUser")
        val deleteStart = createGuidelineFromStart(0.2f)
        val deleteEnd = createGuidelineFromStart(0.8f)
        constrain(head) {
            top.linkTo(parent.top, 16.dp)
            start.linkTo(parent.start, 16.dp)
            end.linkTo(parent.end, 16.dp)
        }
        constrain(userName) {
            top.linkTo(head.bottom, 32.dp)
            start.linkTo(parent.start, 16.dp)
            end.linkTo(parent.end, 16.dp)
            width = Dimension.fillToConstraints
        }
        constrain(email) {
            top.linkTo(userName.bottom, 16.dp)
            start.linkTo(userName.start)
            end.linkTo(userName.end)
            width = Dimension.fillToConstraints
        }
        constrain(createTime) {
            top.linkTo(email.bottom, 16.dp)
            start.linkTo(userName.start)
            end.linkTo(userName.end)
            width = Dimension.fillToConstraints
        }
        constrain(deleteUser) {
            top.linkTo(createTime.bottom, 32.dp)
            start.linkTo(deleteStart)
            end.linkTo(deleteEnd)
            width = Dimension.fillToConstraints
        }
    }
}