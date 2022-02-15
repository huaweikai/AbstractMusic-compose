package com.hua.abstractmusic.bean.ui.home

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @author : huaweikai
 * @Date   : 2022/02/09
 * @Desc   :
 */
data class IconBean(
    val resId :Int,
    val desc :String,
    val size: Dp = 33.dp,
    val width:Dp = 0.dp,
    val onClick:()->Unit
)
