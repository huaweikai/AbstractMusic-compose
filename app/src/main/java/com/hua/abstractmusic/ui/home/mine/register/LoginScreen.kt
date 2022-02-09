package com.hua.abstractmusic.ui.home.mine.register

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.home.viewmodels.UserViewModel
import com.hua.abstractmusic.ui.route.Screen
import com.hua.abstractmusic.ui.utils.EmailCodeEditText
import com.hua.abstractmusic.ui.utils.EmailEditText
import com.hua.abstractmusic.ui.utils.PassWordEditText
import com.hua.abstractmusic.utils.isCode
import com.hua.abstractmusic.utils.isEmail
import com.hua.abstractmusic.utils.isPassWord
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/26
 * @Desc   :
 */
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: UserViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loginMode by remember {
        mutableStateOf(true)
    }
    var loginButtonEnabled by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(
        viewModel.loginPasswordText.value,
        viewModel.loginEmailText.value,
        viewModel.loginEmailCodeText.value,
        loginMode
    ) {
        if (loginMode) {
            loginButtonEnabled =
                viewModel.loginEmailText.value.isEmail()
                        && viewModel.loginPasswordText.value.isPassWord()
        } else {
            loginButtonEnabled =
                viewModel.loginEmailText.value.isEmail()
                        && viewModel.loginEmailCodeText.value.isCode()
        }
    }
    DisposableEffect(Unit) {
        this.onDispose {
            viewModel.loginClear()
        }
    }
    LaunchedEffect(viewModel.loginEmailText.value) {
        if (!viewModel.loginCodeIsWait.value) {
            viewModel.loginEmailCodeEnable.value = viewModel.loginEmailText.value.isEmail()
        }
    }
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val topPercent = createGuidelineFromTop(0.1f)
        val centerPercent = createGuidelineFromStart(0.5f)
        val (title, loginEd, loginButton) = createRefs()
        val (register, codeLogin) = createRefs()
        Text(
            text = "欢迎来到抽象音乐", modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(topPercent)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            textAlign = TextAlign.Center,
            fontSize = 20.sp
        )
        LoginEd(
            viewModel = viewModel,
            modifier = Modifier
                .constrainAs(loginEd) {
                    start.linkTo(parent.start, 20.dp)
                    end.linkTo(parent.end, 20.dp)
                    top.linkTo(title.bottom, 5.dp)
                    width = Dimension.fillToConstraints
                },
            loginMode
        )
        Button(
            onClick = {
                scope.launch {
                    val result = viewModel.login(loginMode)
                    if (result.code == 200) {
                        navController.navigateUp()
                    } else {
                        Toast.makeText(context, result.msg, Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .constrainAs(loginButton) {
                    start.linkTo(loginEd.start)
                    end.linkTo(loginEd.end)
                    top.linkTo(loginEd.bottom, 5.dp)
                    width = Dimension.fillToConstraints
                },
            enabled = loginButtonEnabled
        ) {
            Text(text = "登录")
        }
        Text(
            text = "注册账号",
            modifier = Modifier
                .constrainAs(register) {
                    top.linkTo(loginButton.bottom, 10.dp)
                    end.linkTo(loginButton.end)
                }
                .clickable {
                    navController.navigate(Screen.RegisterScreen.route)
                }
        )
        Text(
            text = if (loginMode) "验证码登录" else "密码登录",
            modifier = Modifier
                .constrainAs(codeLogin) {
                    start.linkTo(centerPercent)
                    end.linkTo(centerPercent)
                    top.linkTo(register.top)
                    bottom.linkTo(register.bottom)
                }
                .clickable {
                    loginMode = !loginMode
                }
        )
    }
}

@Composable
private fun LoginEd(
    viewModel: UserViewModel,
    modifier: Modifier,
    loginMode: Boolean
) {
    Column(
        modifier = modifier
    ) {
        EmailEditText(
            text = viewModel.loginEmailText,
            error = viewModel.loginEmailError,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = if (viewModel.loginEmailError.value) "请输入正确的邮箱" else "",
            color = Color.Red,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontSize = 12.sp
        )
        AnimatedVisibility(
            visible = loginMode,
            modifier = Modifier.fillMaxWidth()
        ) {
            PassWordEditText(
                password = viewModel.loginPasswordText,
                modifier = Modifier.fillMaxWidth(),
                label = "密码",
                onValueChange = {
                    if (it.isBlank()) {
                        viewModel.loginPassWordError.value = false
                    } else {
                        viewModel.loginPassWordError.value = !it.isPassWord()
                    }
                    viewModel.loginPasswordText.value = it
                },
                isError = viewModel.loginPassWordError.value
            )
        }

        AnimatedVisibility(
            visible = !loginMode,
            modifier = Modifier.fillMaxWidth()
        ) {
            EmailCodeEditText(
                text = viewModel.loginEmailCodeText,
                codeError = viewModel.loginEmailCodeError,
                buttonEnabled = viewModel.loginEmailCodeEnable,
                buttonCodeText = viewModel.loginCodeText,
                getCode = {
                    viewModel.getLoginEmailCode()
                }
            )
        }
    }
}