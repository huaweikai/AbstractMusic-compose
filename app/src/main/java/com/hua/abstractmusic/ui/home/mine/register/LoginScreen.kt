package com.hua.abstractmusic.ui.home.mine.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.hua.abstractmusic.R
import com.hua.abstractmusic.ui.home.viewmodels.UserViewModel
import com.hua.abstractmusic.ui.utils.EditText
import com.hua.abstractmusic.utils.isEmail

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
    val passwordVis = remember {
        mutableStateOf(false)
    }
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val topPercent = createGuidelineFromTop(0.1f)
        val (title, email, emailTips, password,button) = createRefs()
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
        EditText(
            value = viewModel.loginEmailText.value,
            onValueChange = {
                if (it.isBlank()) {
                    viewModel.loginEmailError.value = false
                } else {
                    viewModel.loginEmailError.value = !it.isEmail()
                }
                viewModel.loginEmailText.value = it

            },
            leftIcon = Icons.Default.Email,
            isError = viewModel.loginEmailError.value,
            label = "邮箱",
            modifier = Modifier.constrainAs(email) {
                start.linkTo(parent.start, 20.dp)
                end.linkTo(parent.end, 20.dp)
                top.linkTo(title.bottom, 5.dp)
                width = Dimension.fillToConstraints
            },
            rightIcon = @Composable {
                if (viewModel.loginEmailError.value) {
                    Icon(
                        painterResource(id = R.drawable.ic_login_warning),
                        contentDescription = "",
                        tint = Color.Red
                    )
                }
            }
        )
        Text(
            text = if (viewModel.loginEmailError.value) "请输入正确的邮箱" else "",
            color = Color.Red,
            modifier = Modifier
                .constrainAs(emailTips) {
                    top.linkTo(email.bottom, 5.dp)
                    start.linkTo(email.start, 5.dp)
                    end.linkTo(email.end)
                    width = Dimension.fillToConstraints
                },
            textAlign = TextAlign.Start,
            fontSize = 12.sp
        )
        EditText(
            value = viewModel.loginPasswordText.value,
            onValueChange = {
                viewModel.loginPasswordText.value = it
            },
            leftIcon = Icons.Default.Lock,
            isError = false,
            label = "密码",
            modifier = Modifier.constrainAs(password) {
                start.linkTo(email.start)
                end.linkTo(email.end)
                top.linkTo(emailTips.bottom, 5.dp)
                width = Dimension.fillToConstraints
            },
            visualTransformation = if (passwordVis.value) VisualTransformation.None else PasswordVisualTransformation(),
            rightIcon = @Composable {
                Image(
                    painter = painterResource(if (passwordVis.value) R.drawable.ic_login_eye_on else R.drawable.ic_login_eye_off),
                    contentDescription = "",
                    modifier = Modifier
                        .clickable {
                            passwordVis.value = !passwordVis.value
                        }
                )
            }
        )
        Button(
            onClick = {},
            modifier = Modifier
                .constrainAs(button){
                    start
                }
        ) {
            Text(text = "登录")

        }
    }
}

@Composable
private fun LoginEd(){
    Column {

    }
}