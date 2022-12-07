package com.hua.abstractmusic.ui.utils

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hua.abstractmusic.utils.isCode
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2022/01/27
 * @Desc   :
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailCodeEditText(
    text : MutableState<String>,
    codeError:MutableState<Boolean>,
    buttonEnabled:MutableState<Boolean>,
    buttonCodeText: State<String>,
    getCode: suspend ()->String
) {
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
    ) {
        val (code, button) = createRefs()
        val centerPercent = createGuidelineFromStart(0.5f)
        OutlinedTextField(
            value = text.value,
            onValueChange = {
                if (it.length <= 6) {
                    if (it.isBlank()) {
                        codeError.value = false
                    }
                    text.value = it.filter {
                        it.isDigit()
                    }
                    codeError.value =
                        !text.value.isCode()
                }
            },
            isError = codeError.value,
            modifier = Modifier
                .constrainAs(code) {
                    start.linkTo(parent.start)
                    end.linkTo(centerPercent, 5.dp)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                },
            label = @Composable {
                Text(text = "验证码")
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )
        Button(
            onClick = {
                scope.launch {
                    Toast.makeText(context,getCode(),Toast.LENGTH_SHORT).show()
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
            enabled = buttonEnabled.value,
        ) {
            Text(text = buttonCodeText.value)
        }
    }
}