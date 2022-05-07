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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.hua.abstractmusic.ui.utils.LifecycleFocusClearUtils
import com.hua.abstractmusic.ui.utils.UCropActivityResultContract
import com.hua.abstractmusic.ui.viewmodels.UserViewModel
import com.hua.abstractmusic.utils.getCacheDir

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

    val contentResolver = LocalContext.current.contentResolver
    val context = LocalContext.current
    val cropPicture = rememberLauncherForActivityResult(UCropActivityResultContract()) {
        if (it != null) {
            userChangeViewModel.sendAction(UserChangeAction.UserHead(it.toString()))
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
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                }
                Text(text = "编辑用户", fontSize = 18.sp)
                IconButton(onClick = {
                    userChangeViewModel.sendAction(UserChangeAction.SaveUser(contentResolver))
                    settingNavController.navigateUp()
                }) {
                    Icon(imageVector = Icons.Filled.Send, contentDescription = "保存")
                }
            }
        },
        modifier = Modifier.statusBarsPadding()
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
        }
    }

}

fun userChangeConstraintSet(): ConstraintSet {
    return ConstraintSet {
        val head = createRefFor("head")
        val userName = createRefFor("userName")
        val email = createRefFor("email")
        val createTime = createRefFor("createTime")
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
    }
}