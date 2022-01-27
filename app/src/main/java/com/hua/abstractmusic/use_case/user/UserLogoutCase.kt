package com.hua.abstractmusic.use_case.user

import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.repository.UserRepository

/**
 * @author : huaweikai
 * @Date   : 2022/01/27
 * @Desc   :
 */
class UserLogoutCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(token:String):NetData<Unit>{
        return repository.logoutUser(token)
    }
}