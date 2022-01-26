package com.hua.abstractmusic.ui.utils

import android.widget.EditText
import androidx.compose.foundation.Image
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension
import com.hua.abstractmusic.utils.isEmail


/**
 * @author : huaweikai
 * @Date   : 2022/01/26
 * @Desc   :
 */
@Composable
fun EditText(
    value: String,
    onValueChange: (String) -> Unit,
    leftIcon: ImageVector,
    isError: Boolean,
    label: String,
    modifier: Modifier,
    rightIcon: @Composable () -> Unit,
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
        trailingIcon = rightIcon
    )
}
