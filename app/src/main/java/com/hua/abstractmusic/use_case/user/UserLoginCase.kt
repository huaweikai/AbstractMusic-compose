package com.hua.abstractmusic.use_case.user

import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.user.UserBean
import com.hua.abstractmusic.db.user.UserDao
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
        email:String,
        password:String
    ) :Boolean{
        return try {
            val result = userRepository.loginWithEmail(email,password)
            if (result.code == 200) {
                dao.insertUser(
                    UserBean(0,email,password,email,result.data!!)
                )
                true
            }else{
                false
            }
        }catch (e:Throwable){
            false
        }
    }
}