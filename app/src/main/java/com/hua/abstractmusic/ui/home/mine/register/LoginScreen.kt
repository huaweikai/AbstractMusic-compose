package com.hua.abstractmusic.ui.home.mine.register

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.LocalAppNavController
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
@OptIn(ExperimentalMaterial3Api::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    navController: NavHostController = LocalAppNavController.current,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focus = LocalFocusManager.current
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
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(Unit) {
        val observer = LifecycleEventObserver { source, event ->
            if(event == Lifecycle.Event.ON_PAUSE){
                focus.clearFocus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        this.onDispose {
            viewModel.loginClear()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(viewModel.loginEmailText.value) {
            viewModel.loginEmailCodeEnable.value = viewModel.loginEmailText.value.isEmail() && !viewModel.registerTimeEnable.value
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text(text = "登录")},
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) {
        Column(
            Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                top = it.calculateTopPadding(),
                bottom = it.calculateBottomPadding()
            )
        ){
            Text(
                text = "欢迎来到抽象音乐",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(5.dp))
            LoginEd(
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth(),
                loginMode
            )
            Spacer(modifier = Modifier.height(5.dp))
            Button(
                onClick = {
                    focus.clearFocus()
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
                    .fillMaxWidth(),
                enabled = loginButtonEnabled
            ) {
                Text(text = "登录")
            }
            Spacer(modifier = Modifier
                .height(10.dp)
                .fillMaxWidth())
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (loginMode) "验证码登录" else "密码登录",
                    modifier = Modifier
                        .clickable {
                            loginMode = !loginMode
                        }
                )
                Text(
                    text = "注册账号",
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Screen.RegisterScreen.route)
                        }
                )
            }
        }
    }
}

@Composable
private fun LoginEd(
    viewModel: LoginViewModel,
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