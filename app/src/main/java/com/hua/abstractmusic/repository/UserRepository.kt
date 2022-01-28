package com.hua.abstractmusic.repository

import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.user.NetUser
import com.hua.abstractmusic.bean.user.UserBean
import com.hua.abstractmusic.db.user.UserDao
import com.hua.abstractmusic.net.UserService
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.other.NetWork.NO_USER
import com.hua.abstractmusic.other.NetWork.SUCCESS

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
            NetData(ERROR,null,"服务器或网络异常")
        }
    }

    suspend fun register(
        email: String,
        username:String,
        passWord: String,
        code: Int
    ): NetData<String> {
        return try {
            userService.register(email,username, passWord,code)
        }catch (e:Throwable){
            NetData(ERROR,null,"服务器或网络异常")
        }

    }

    suspend fun loginWithEmail(
        email: String,
        passWord: String
    ): NetData<String> {
        return try {
            val result = userService.loginWithEmail(email, passWord)
            if (result.code == 200) {
                getUser(result.data!!).data?.let {
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

    suspend fun hasUser():NetData<Unit>{
        if (dao.userInRoom() == 0) {
            return NetData(NO_USER,null,"本地无用户")
        }
        //todo(此处需要验证token是否登录后，获取最新的用户数据)
        val token = dao.getToken()
        return try {
            userService.testToken(token)
        }catch (e:Throwable){
            NetData(ERROR,null,"服务器或网络异常")
        }
    }

    private suspend fun getUser(token:String):NetData<NetUser>{
        return try {
            userService.getUser(token)
        }catch (e:Throwable){
            NetData(ERROR,null,"服务器或网络异常")
        }
    }

    suspend fun getInfo():UserBean?{
        return dao.getUserInfo()
    }

    suspend fun logoutUser():NetData<Unit>{
        return try {
            val token = dao.getToken()
            val result = userService.logoutUser(token)
            if(result.code == SUCCESS) {
                dao.deleteUser()
            }
            result
        }catch (e:Throwable){
            NetData(ERROR,null,"服务器或网络异常")
        }
    }

    suspend fun getEmailCodeWithLogin(email: String):NetData<Unit>{
        return try {
            userService.getEmailCodeWithLogin(email)
        }catch (e:Throwable){
            NetData(ERROR,null,"服务器或网络异常")
        }
    }

    suspend fun loginWithCode(
        email: String,
        code: Int
    ):NetData<String>{
        return try {
            val result = userService.loginWithCode(email, code)
            if(result.code == SUCCESS){
                val user = userService.getUser(result.data!!).data
                user?.let {
                    dao.insertUser(
                        UserBean(it.id!!,it.name,it.passwd,it.email,result.data)
                    )
                }
            }
            result
        }catch (e:Throwable){
            NetData(ERROR,null,"服务器或网络异常")
        }
    }
}