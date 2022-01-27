package com.hua.abstractmusic.use_case.user

import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.user.UserBean
import com.hua.abstractmusic.db.user.UserDao
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.repository.UserRepository

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   :
 */
class UserLoginCase(
    private val userRepository: UserRepository,
    private val dao: UserDao
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): NetData<String> {
        return try {
            val result = userRepository.loginWithEmail(email, password)
            if (result.code == 200) {
                userRepository.getUser(result.data!!).data?.let {
                    dao.insertUser(
                        UserBean(it.id!!, it.name, it.passwd, it.email, result.data)
                    )
                }
            }
            result
        } catch (e: Throwable) {
            NetData(ERROR,null,"服务器或网络异常")
        }
    }
}