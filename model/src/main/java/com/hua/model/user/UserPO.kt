package com.hua.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
@Entity
data class UserPO(
    @PrimaryKey
    val id: Int,
    val userName: String,
    val password: String,
    val email: String,
    val head: String?,
    val createTime:String
)