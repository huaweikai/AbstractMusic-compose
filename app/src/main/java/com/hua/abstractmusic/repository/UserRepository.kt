package com.hua.abstractmusic.repository

import com.hua.abstractmusic.db.user.UserDao
import com.hua.network.api.UserService
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.utils.UpLoadFile
import com.hua.model.user.UserPO
import com.hua.model.user.UserVO
import com.hua.network.*
import com.hua.service.room.dao.MusicDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   : 仓库
 */
class UserRepository(
    private val userService: UserService,
    private val userDao: UserDao,
    private val dao: MusicDao,
    private val userInfoData: UserInfoData,
    val upLoadFile: UpLoadFile
) {

    suspend fun getEmailCode(email: String): ApiResult<String> {
        return userService.getEmailCode(email)
    }

    suspend fun register(
        email: String,
        username: String,
        passWord: String,
        code: Int
    ): ApiResult<String> {
        return userService.register(email, username, passWord, code)
    }

    suspend fun loginWithEmail(
        email: String,
        passWord: String
    ): ApiResult<String> {
        val result = userService.loginWithEmail(email, passWord)
        if (result is ApiResult.Success) {
            getUser(result.data, true)
        }
        return userService.loginWithEmail(email, passWord)
    }

    suspend fun hasUser(): ApiResult<Unit> {
        val token = userInfoData.userInfo.value.userToken
        val result = userService.testToken(token)
        if (result is ApiResult.Failure && result.error.errorCode == 500) {
            userDao.deleteUser()
            userInfoData.logout()
        } else {
            getUser(token)
        }
        return result
    }

    suspend fun selectUser():ApiResult<UserVO>{
        return userService.getUser(userInfoData.userInfo.value.userToken)
    }

    private suspend fun getUser(token: String, isLogin: Boolean = false) {
        flow {
            emit(userService.getUser(token))
        }.onEach {
            if (it is ApiResult.Success) {
                val user = it.data
                userDao.insertUser(
                    UserPO(
                        user.id!!,
                        user.name,
                        user.passwd,
                        user.email,
                        user.head,
                        user.createTime
                    )
                )
            }
        }.onCompletion {
            if (isLogin) {
                userInfoData.loginUser(token)
            } else {
                userInfoData.refreshUser()
            }
        }.flowOn(Dispatchers.IO).collect()
    }

    suspend fun logoutUser(): ApiResult<String> {
        val token = userInfoData.userInfo.value.userToken
        val result = userService.logoutUser(token)
        if (result is ApiResult.Success || result is ApiResult.Failure && result.error.errorCode == 500) {
            userDao.deleteUser()
            userInfoData.logout()
        }
        return result
    }

    suspend fun getEmailCodeWithLogin(email: String): ApiResult<String> {
        return userService.getEmailCodeWithLogin(email)
    }

    suspend fun loginWithCode(
        email: String,
        code: Int
    ): ApiResult<String> {
        val result = userService.loginWithCode(email, code)
        if (result is ApiResult.Success) {
            getUser(result.data, true)
        }
        return result
    }

    suspend fun updateUser(url: String) {
        val user = userInfoData.userInfo.value.userBean
        val userVO =
            UserVO(user?.id, user?.userName!!, user.email, user.password, url, user.createTime)
        updateUser(userVO)
    }

    suspend fun updateUser(userPO: UserPO):ApiResult<Unit>{
       return updateUser(
            UserVO(
                userPO.id,
                userPO.userName,
                userPO.email,
                userPO.password,
                userPO.head,
                userPO.createTime
            )
        )
    }

    suspend fun updateUser(userVO: UserVO):ApiResult<Unit>{
        val token = userInfoData.userInfo.value.userToken
        val updateResult = userService.setUser(token, userVO)
        return updateResult.also {
            if (it is ApiResult.Success) {
                getUser(token = token)
            }
        }
    }

    suspend fun removeLocalSheet(sheetId: String) {
        dao.deleteSheet(sheetId)
    }

    suspend fun deleteUser(passWord: String):ApiResult<Unit>{
        val token = userInfoData.userInfo.value.userToken
        val user = userInfoData.userInfo.value.userBean
            ?: return ApiResult.Failure(ApiError.userIsNulError)
        if(user.password != passWord) return ApiResult.Failure(Error("密码匹配错误"))
        return userService.deleteUser(token).also {
            it.onSuccess {
                userDao.deleteUser()
                userInfoData.logout()
            }
        }
    }
}