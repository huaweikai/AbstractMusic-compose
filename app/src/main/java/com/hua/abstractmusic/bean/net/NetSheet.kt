package com.hua.abstractmusic.bean.net

/**
 * @author : huaweikai
 * @Date   : 2022/02/13
 * @Desc   :
 */
data class NetSheet(
    val id: Int,
    val userId: Int,
    val title: String,
    val artUri: String? = null,
    val sheetDesc: String? = null
)