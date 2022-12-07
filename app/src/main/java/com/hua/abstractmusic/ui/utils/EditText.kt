package com.hua.abstractmusic.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.hua.abstractmusic.R
import com.hua.abstractmusic.utils.isEmail
import com.hua.abstractmusic.utils.isUser


/**
 * @author : huaweikai
 * @Date   : 2022/01/26
 * @Desc   :
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditText(
    value: String,
    onValueChange: (String) -> Unit,
    leftIcon: ImageVector,
    isError: Boolean,
    label: String,
    modifier: Modifier,
    rightIcon: @Composable () -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = @Composable {
            Image(
                imageVector = leftIcon,
                contentDescription = ""
            )
        },
        isError = isError,
        label = @Composable {
            Text(text = label)
        },
        visualTransformation = visualTransformation,
        modifier = modifier,
        trailingIcon = rightIcon,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions
    )
}

@Composable
fun EmailEditText(
    text: MutableState<String>,
    error: MutableState<Boolean>,
    modifier: Modifier
) {
    EditText(
        value = text.value,
        onValueChange = {
            if (it.isBlank()) {
                error.value = false
            } else {
                error.value = !it.isEmail()
            }
            text.value = it

        },
        leftIcon = Icons.Default.Email,
        isError = error.value,
        label = "邮箱",
        modifier = modifier,
        rightIcon = {
            if (error.value) {
                Icon(
                    painterResource(id = R.drawable.ic_login_warning),
                    contentDescription = "",
                    tint = Color.Red
                )
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        )
    )
}

@Composable
fun PassWordEditText(
    //密码文字
    password: MutableState<String>,
    //修饰符
    modifier: Modifier,
    //标题
    label: String,
    //当文字发生改变时
    onValueChange: (String) -> Unit = {
        password.value = it
    },
    // 是否要报错，也就是输入的不是标准密码
    isError: Boolean = false
) {
    val passwordVis = remember {
        mutableStateOf(false)
    }
    EditText(
        value = password.value,
        onValueChange = onValueChange,
        leftIcon = Icons.Default.Lock,
        isError = isError,
        label = label,
        modifier = modifier,
        visualTransformation = if (passwordVis.value) VisualTransformation.None else PasswordVisualTransformation(),
        rightIcon = {
            Image(
                painter = painterResource(if (passwordVis.value) R.drawable.ic_login_eye_on else R.drawable.ic_login_eye_off),
                contentDescription = "",
                modifier = Modifier
                    .clickable {
                        passwordVis.value = !passwordVis.value
                    }
                    .size(24.dp)
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password
        )
    )
}

@Composable
fun UserEditText(
    user: MutableState<String>,
    isError: MutableState<Boolean>,
    modifier: Modifier
) {
    EditText(
        value = user.value,
        onValueChange = {
            if (it.isBlank()) {
                isError.value = false
            }else{
                isError.value = !it.isUser()
            }
            user.value = it
        },
        leftIcon = Icons.Default.Person,
        isError = isError.value,
        label = "用户名(2-16位)",
        modifier = modifier,
        rightIcon = @Composable{
            if (isError.value) {
                Icon(
                    painterResource(id = R.drawable.ic_login_warning),
                    contentDescription = "",
                    tint = Color.Red
                )
            }
        }
    )
}


@Composable
fun TransparentHintTextField(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    isHintVisible: Boolean = true,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    maxLines:Int = 1,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onFocusChange: (FocusState) -> Unit = {}
) {
    Box(
        modifier = modifier
    ) {
        BasicTextField(
            value = text,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = textStyle,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    onFocusChange(it)
                },
            maxLines = maxLines,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
        )
        val align = when(textStyle.textAlign){
            TextAlign.Center->{
                Alignment.Center
            }
            TextAlign.Left->{
                Alignment.CenterStart
            }
            TextAlign.Right->{
                Alignment.CenterEnd
            }
            else->{
                Alignment.CenterStart
            }
        }
        if (isHintVisible) {
            Text(text = hint, modifier = Modifier.align(align),style = textStyle, color = Color.DarkGray.copy(0.6f))
        }
    }
}