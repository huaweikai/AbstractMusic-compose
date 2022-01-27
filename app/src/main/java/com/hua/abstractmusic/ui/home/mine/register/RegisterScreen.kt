package com.hua.abstractmusic.ui.home.mine.register

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hua.abstractmusic.ui.home.viewmodels.UserViewModel
import com.hua.abstractmusic.ui.utils.EmailEditText
import com.hua.abstractmusic.ui.utils.PassWordEditText
import com.hua.abstractmusic.utils.isCode
import com.hua.abstractmusic.utils.isEmail
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   :
 */
@Composable
fun RegisterScreen(
    viewModel: UserViewModel
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "注册账号", fontSize = 22.sp)
        EmailEditText(
            text = viewModel.registerEmailText,
            error = viewModel.registerEmailError,
            modifier = Modifier.fillMaxWidth()
        )
        PassWordEditText(
            password = viewModel.registerPasswordText,
            modifier = Modifier.fillMaxWidth(),
            label = "密码"
        )
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
                        it == viewModel.registerPasswordText.value
                }
            }
        )
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            val (code, button) = createRefs()
            val centerPercent = createGuidelineFromStart(0.5f)
            OutlinedTextField(
                value = viewModel.registerEmailCode.value,
                onValueChange = {
                    if (it.length <= 6) {
                        if (it.isBlank()) {
                            viewModel.registerCodeError.value = false
                        }
                        viewModel.registerEmailCode.value = it.filter {
                            it.isDigit()
                        }
                        viewModel.registerCodeError.value =
                            !viewModel.registerEmailCode.value.isCode()
                    }
                },
                isError = viewModel.registerCodeError.value,
                modifier = Modifier
                    .constrainAs(code) {
                        start.linkTo(parent.start)
                        end.linkTo(centerPercent, 5.dp)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    },
                label = @Composable {
                    Text(text = "验证码")
                }
            )
            val registerCodeButton = remember {
                mutableStateOf(false)
            }
            LaunchedEffect(viewModel.registerEmailText.value) {
                registerCodeButton.value = viewModel.registerEmailText.value.isEmail()
            }
            Button(
                onClick = {
                    scope.launch {
                        Toast.makeText(context, viewModel.getEmailCode(), Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.constrainAs(button) {
                    start.linkTo(centerPercent, 5.dp)
                    top.linkTo(code.top, 10.dp)
                    bottom.linkTo(code.bottom, 5.dp)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                },
                enabled = registerCodeButton.value,
            ) {
                Text(text = viewModel.codeText.value)
            }
        }
        Button(onClick = {
            scope.launch {
                viewModel.register()
            }
        }) {
            Text(text = "注册")
        }
    }
}