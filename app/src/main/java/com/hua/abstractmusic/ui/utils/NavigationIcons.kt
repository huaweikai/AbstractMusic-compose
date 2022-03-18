package com.hua.abstractmusic.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable


/**
 * @author : huaweikai
 * @Date   : 2022/03/17
 * @Desc   :
 */
@Composable
fun NavigationBack(
    onclick:()->Unit
){
    IconButton(onClick = {onclick()}) {
        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "")
    }
}