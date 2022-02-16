package com.hua.abstractmusic.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hua.abstractmusic.base.BaseBrowserViewModel


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
        CircularProgressIndicator()
    }
}

@Composable
fun Error(
    onclick:()->Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .clickable {
                onclick()
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Text(text = "加载失败,请点击屏幕重试")
        }
    }
}