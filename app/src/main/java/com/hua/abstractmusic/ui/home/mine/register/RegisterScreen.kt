package com.hua.abstractmusic.ui.home.mine.register

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.TextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hua.abstractmusic.ui.home.viewmodels.UserViewModel
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
    Column(
        Modifier.padding(top = 90.dp)
    ) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        TextField(
            value = viewModel.emailText.value,
            onValueChange = {
                viewModel.emailText.value = it
            },
        )
        TextField(
            value = viewModel.passwordText.value,
            onValueChange = {
                viewModel.passwordText.value = it
            },
        )
        TextField(
            value = viewModel.emailCode.value,
            onValueChange = {
                viewModel.emailCode.value = it
            }
        )
        Text(text = viewModel.codeText.value, modifier = Modifier
            .clickable {
                if (viewModel.codeButton.value) {
                    scope.launch {
                        Toast.makeText(context, viewModel.getEmailCode(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        Button(onClick = {
            scope.launch {
                viewModel.register()
            }
        }
        ) {
            Text(text = "注册")
        }

        Button(onClick = {
            scope.launch {
                viewModel.login(viewModel.emailText.value,viewModel.passwordText.value)
            }
        }
        ) {
            Text(text = "登录")
        }

    }
}