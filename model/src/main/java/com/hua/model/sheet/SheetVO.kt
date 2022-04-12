package com.hua.model.sheet

import kotlinx.serialization.Serializable

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
@Serializable
data class SheetVO(
    val id: Int,
    val userId: Int,
    val title: String,
    val artUri: String? = null,
    val sheetDesc: String ? = null,
    val num:Int,
    val author:String
)
