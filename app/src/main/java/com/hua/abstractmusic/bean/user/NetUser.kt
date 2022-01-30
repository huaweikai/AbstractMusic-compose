package com.hua.abstractmusic.bean.user

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    var head: String?
)
