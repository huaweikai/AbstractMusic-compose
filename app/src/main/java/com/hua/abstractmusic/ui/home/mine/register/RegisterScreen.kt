package com.hua.abstractmusic.ui.home.mine.register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.LocalAppNavController
import com.hua.abstractmusic.ui.utils.EmailCodeEditText
import com.hua.abstractmusic.ui.utils.EmailEditText
import com.hua.abstractmusic.ui.utils.PassWordEditText
import com.hua.abstractmusic.ui.utils.UserEditText
import com.hua.abstractmusic.utils.isCode
import com.hua.abstractmusic.utils.isEmail
import com.hua.abstractmusic.utils.isPassWord
import com.hua.abstractmusic.utils.isUser
import com.hua.network.get
import com.hua.network.onSuccess
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   :
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    navHostController: NavHostController = LocalAppNavController.current
) {
    LaunchedEffect(viewModel.registerEmailText.value) {
        viewModel.registerCodeButtonEnabled.value = viewModel.registerEmailText.value.isEmail() && !viewModel.registerTimeEnable.value
    }
    LaunchedEffect(
        viewModel.registerEmailError.value,
        viewModel.registerPassWordError.value,
        viewModel.registerPassWordAgainError.value,
        viewModel.registerEmailCodeError.value,
        viewModel.registerNameText.value
    ) {
        viewModel.registerButtonEnabled.value = viewModel.registerEmailText.value.isEmail() &&
                viewModel.registerPasswordText.value.isPassWord() &&
                viewModel.registerPasswordAgainText.value == viewModel.registerPasswordText.value &&
                viewModel.registerEmailCodeText.value.isCode() &&
                viewModel.registerNameText.value.isUser()
    }
    LaunchedEffect(
        viewModel.registerPasswordText.value,
        viewModel.registerPasswordAgainText.value
    ) {
        viewModel.registerPassWordAgainError.value = if(viewModel.registerPasswordAgainText.value.isBlank()){
            false
        }else{
            viewModel.registerPasswordAgainText.value != viewModel.registerPasswordText.value
        }
    }
    DisposableEffect(Unit) {
        this.onDispose {
            viewModel.registerClear()
        }
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "注册")},
                navigationIcon = {
                    IconButton(onClick = { navHostController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = it.calculateTopPadding(),
                    bottom = it.calculateBottomPadding()
                ),
            verticalArrangement = Arrangement.Top
        ) {
//            Text(text = "注册账号", fontSize = 34.sp)
            Spacer(modifier = Modifier.padding(top = 10.dp))
            EmailEditText(
                text = viewModel.registerEmailText,
                error = viewModel.registerEmailError,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            UserEditText(
                user = viewModel.registerNameText,
                isError = viewModel.registerNameError,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            PassWordEditText(
                password = viewModel.registerPasswordText,
                modifier = Modifier.fillMaxWidth(),
                label = "8-16位密码,数字,字符和符号至少两种",
                isError = viewModel.registerPassWordError.value,
                onValueChange = {
                    if (it.isBlank()) {
                        viewModel.registerPassWordError.value = false
                    } else {
                        viewModel.registerPassWordError.value = !it.isPassWord()
                    }
                    viewModel.registerPasswordText.value = it
                }
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            PassWordEditText(
                password = viewModel.registerPasswordAgainText,
                modifier = Modifier.fillMaxWidth(),
                label = "确认密码",
                isError = viewModel.registerPassWordAgainError.value,
                onValueChange = {
                    viewModel.registerPasswordAgainText.value = it
                }
            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            EmailCodeEditText(
                text = viewModel.registerEmailCodeText,
                codeError = viewModel.registerEmailCodeError,
                buttonEnabled = viewModel.registerCodeButtonEnabled,
                buttonCodeText = viewModel.codeText
            ) {
                viewModel.getRegisterEmailCode()
            }
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Button(
                onClick = {
                    scope.launch {
                        val data = viewModel.register()
                        Toast.makeText(context, data.get{it.error.errorMsg}, Toast.LENGTH_SHORT).show()
                        data.onSuccess {
                            navHostController.navigateUp()
                        }
                    }
                },
                enabled = viewModel.registerButtonEnabled.value,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "注册")
            }
        }
    }
}