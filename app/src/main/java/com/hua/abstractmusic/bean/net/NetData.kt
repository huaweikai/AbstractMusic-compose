package com.hua.abstractmusic.bean.net

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   :
 */
data class NetData<T>(
    val code: Int,
    val `data`: T?,
    val msg: String
)