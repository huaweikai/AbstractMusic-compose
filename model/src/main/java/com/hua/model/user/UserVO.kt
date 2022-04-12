package com.hua.model.user

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
@Serializable
data class UserVO(
    var id: Int?,
    var name: String,
    var email: String,
    var passwd: String,
    var head: String?,
    var createTime:String
)