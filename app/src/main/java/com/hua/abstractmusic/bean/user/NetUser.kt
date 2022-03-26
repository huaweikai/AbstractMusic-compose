package com.hua.abstractmusic.bean.user

/**
 * @author : huaweikai
 * @Date   : 2022/01/27
 * @Desc   :
 */
data class NetUser(
    var id: Int?,
    var name: String,
    var email: String,
    var passwd: String,
    var head: String?,
    var createTime:String
)
