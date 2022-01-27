package com.hua.abstractmusic.use_case.user

import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.repository.UserRepository

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   :
 */
class UserRegisterCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email:String):NetData<Unit>{
        return userRepository.getEmailCode(email)
    }

    suspend operator fun invoke(
        email:String,
        username:String,
        password:String,
        code:Int
    ):NetData<String>{
        return userRepository.register(email,username,password,code)
    }
}