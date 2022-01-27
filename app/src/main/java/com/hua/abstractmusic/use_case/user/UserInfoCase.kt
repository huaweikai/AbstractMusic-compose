package com.hua.abstractmusic.use_case.user

import com.hua.abstractmusic.bean.user.UserBean
import com.hua.abstractmusic.db.user.UserDao
import com.hua.abstractmusic.repository.UserRepository

/**
 * @author : huaweikai
 * @Date   : 2022/01/27
 * @Desc   :
 */
class UserInfoCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke():UserBean?{
        return repository.getInfo()
    }
}