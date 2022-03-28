package com.hua.abstractmusic.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


/**
 * @author : huaweikai
 * @Date   : 2022/02/16
 * @Desc   :
 */

sealed class LCE {
    object Success : LCE()
    object Error : LCE()
    object Loading : LCE()
}

@Composable
fun Loading() {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Color.Transparent
        CircularProgressIndicator()
    }
}

@Composable
fun Error(
    onclick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(imageVector = Icons.Default.Warning, contentDescription = "")
        Button(onClick = { onclick() }) {
            Text(text = "点击重试")
        }
    }
}