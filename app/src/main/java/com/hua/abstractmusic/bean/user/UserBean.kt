package com.hua.abstractmusic.bean.user

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author : huaweikai
 * @Date   : 2022/01/24
 * @Desc   :
 */
@Entity
data class UserBean(
    @PrimaryKey
    val id: Int,
    val userName: String,
    val password: String,
    val email: String,
    val head: String?,
)