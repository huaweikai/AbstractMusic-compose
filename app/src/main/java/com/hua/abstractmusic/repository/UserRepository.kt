package com.hua.abstractmusic.repository

import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.db.user.UserDao
import com.hua.abstractmusic.net.UserService
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.other.NetWork.NO_USER

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   : 仓库
 */
class UserRepository(
    private val userService: UserService,
    private val dao: UserDao
) {

    suspend fun getEmailCode(email: String) :NetData<Unit> {
        return try {
            userService.getEmailCode(email)
        }catch (e:Throwable){
            NetData(ERROR,null,"网络异常")
        }
    }

    suspend fun register(
        email: String,
        passWord: String,
        code: Int
    ): NetData<String> {
        return userService.register(email, passWord, code)
    }

    suspend fun loginWithEmail(
        email: String,
        passWord: String
    ): NetData<String> {
        return userService.loginWithEmail(email, passWord)
    }

    suspend fun hasUser():NetData<Unit>{
        if (dao.userInRoom() == 0) {
            return NetData(NO_USER,null,"本地无用户")
        }
        //todo(此处需要验证token是否登录后，获取最新的用户数据)
        val token = dao.getToken()
        return try {
            userService.testToken(token)
        }catch (e:Throwable){
            NetData(ERROR,null,"网络异常")
        }
    }

}