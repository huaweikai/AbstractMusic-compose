package com.hua.abstractmusic.ui.home.mine.register

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.home.viewmodels.UserViewModel
import com.hua.abstractmusic.ui.utils.EditText
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
    val passwordVis1 = remember {
        mutableStateOf(false)
    }
    val passwordVis2 = remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Top
    ) {
        EditText(
            value = viewModel.registerEmailText.value,
            onValueChange = {
                if (it.isBlank()) {
                    viewModel.registerEmailError.value = false
                } else {
                    viewModel.registerEmailError.value = !it.isEmail()
                }
                viewModel.registerEmailText.value = it
            },
            leftIcon = Icons.Default.Email,
            isError = viewModel.registerEmailError.value,
            label = "邮箱",
            modifier = Modifier.fillMaxWidth(),
            rightIcon = {
                if (viewModel.registerEmailError.value) {
                    Icon(
                        painterResource(id = R.drawable.ic_login_warning),
                        contentDescription = "",
                        tint = Color.Red
                    )
                }
            }
        )
        EditText(
            value = viewModel.registerPasswordText.value,
            onValueChange = {
                viewModel.registerPasswordText.value = it
            },
            leftIcon = Icons.Default.Lock,
            isError = false,
            label = "密码",
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVis1.value) VisualTransformation.None else PasswordVisualTransformation(),
            rightIcon = {
                Image(
                    painter = painterResource(if (passwordVis1.value) R.drawable.ic_login_eye_on else R.drawable.ic_login_eye_off),
                    contentDescription = "",
                    modifier = Modifier
                        .clickable {
                            passwordVis1.value = !passwordVis1.value
                        }
                )
            }
        )
        EditText(
            value = viewModel.registerPasswordAgainText.value,
            onValueChange = {
                viewModel.registerPasswordAgainText.value = it
                if (it.isBlank()) {
                    viewModel.registerPassWordAgainError.value = false
                } else {
                    viewModel.registerPassWordAgainError.value =
                        it == viewModel.registerPasswordText.value
                }
            },
            leftIcon = Icons.Default.Lock,
            isError = viewModel.registerPassWordAgainError.value,
            label = "确认密码",
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVis2.value) VisualTransformation.None else PasswordVisualTransformation(),
            rightIcon = {
                Image(
                    painter = painterResource(if (passwordVis2.value) R.drawable.ic_login_eye_on else R.drawable.ic_login_eye_off),
                    contentDescription = "",
                    modifier = Modifier
                        .clickable {
                            passwordVis2.value = !passwordVis2.value
                        }
                )
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = viewModel.registerEmailCode.value,
                onValueChange = {
                    if(it.toIntOrNull() != null){
                        viewModel.registerEmailCode.value = it
                    }
                },
                isError = !viewModel.registerEmailCode.value.isCode(),
                modifier = Modifier
                    .weight(1f)
            )
            Button(onClick = {
                scope.launch {
                    Toast.makeText(context, viewModel.getEmailCode(), Toast.LENGTH_SHORT).show()
                }
            },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(text = viewModel.codeText.value)
            }
        }
    }
}