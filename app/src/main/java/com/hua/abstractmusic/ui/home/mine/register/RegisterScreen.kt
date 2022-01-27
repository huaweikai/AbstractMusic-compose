package com.hua.abstractmusic.ui.home.mine.register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hua.abstractmusic.ui.home.viewmodels.UserViewModel
import com.hua.abstractmusic.ui.utils.EmailCodeEditText
import com.hua.abstractmusic.ui.utils.EmailEditText
import com.hua.abstractmusic.ui.utils.PassWordEditText
import com.hua.abstractmusic.ui.utils.UserEditText
import com.hua.abstractmusic.utils.isCode
import com.hua.abstractmusic.utils.isEmail
import com.hua.abstractmusic.utils.isPassWord
import com.hua.abstractmusic.utils.isUser
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   :
 */
@Composable
fun RegisterScreen(
    viewModel: UserViewModel,
    navHostController: NavHostController
) {

    LaunchedEffect(viewModel.registerEmailText.value) {
        viewModel.registerCodeButton.value = viewModel.registerEmailText.value.isEmail()
    }
    LaunchedEffect(
        viewModel.registerEmailError.value,
        viewModel.registerPassWordError.value,
        viewModel.registerPassWordAgainError.value,
        viewModel.registerCodeError.value,
        viewModel.registerNameText.value
    ) {
        viewModel.registerButtonEnabled.value = viewModel.registerEmailText.value.isEmail() &&
                viewModel.registerPasswordText.value.isPassWord() &&
                viewModel.registerPasswordAgainText.value == viewModel.registerPasswordText.value &&
                viewModel.registerEmailCode.value.isCode()&&
                viewModel.registerNameText.value.isUser()
    }
    DisposableEffect(Unit) {
        this.onDispose {
            viewModel.registerClear()
        }
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(viewModel.registerState.value) {
        val data = viewModel.registerState.value
        if(data.code != 0){
            Toast.makeText(context, data.msg, Toast.LENGTH_SHORT).show()
            if(data.code == 200){
                navHostController.navigateUp()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "注册账号", fontSize = 34.sp)
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
                if (it.isBlank()) {
                    viewModel.registerPassWordAgainError.value = false
                } else {
                    viewModel.registerPassWordAgainError.value =
                        it != viewModel.registerPasswordText.value
                }
            }
        )
        Spacer(modifier = Modifier.padding(top = 10.dp))
        EmailCodeEditText(
            text = viewModel.registerEmailCode,
            codeError = viewModel.registerCodeError,
            buttonEnabled = viewModel.registerCodeButton,
            buttonCodeText = viewModel.codeText
        ) {
            viewModel.getRegisterEmailCode()
        }
        Spacer(modifier = Modifier.padding(top = 10.dp))
        Button(
            onClick = {
                scope.launch {
                    viewModel.register()
                }
            },
            enabled = viewModel.registerButtonEnabled.value,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "注册")
        }
    }
}