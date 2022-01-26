package com.hua.abstractmusic.use_case.user

import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.repository.UserRepository

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   :
 */
class UserTokenOut(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke():NetData<Unit>{
        return userRepository.hasUser()
    }
}