package com.hua.abstractmusic.repository

import androidx.documentfile.provider.DocumentFile
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.user.NetUser
import com.hua.abstractmusic.bean.user.UserBean
import com.hua.abstractmusic.db.user.UserDao
import com.hua.abstractmusic.net.UserService
import com.hua.abstractmusic.other.Constant.BUCKET_NAME
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.other.NetWork.SERVER_ERROR
import com.hua.abstractmusic.other.NetWork.SUCCESS
import com.hua.abstractmusic.preference.UserInfoData
import com.obs.services.ObsClient
import com.obs.services.exception.ObsException
import com.obs.services.model.ObjectMetadata
import com.obs.services.model.ProgressStatus
import com.obs.services.model.PutObjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.io.InputStream

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   : 仓库
 */
class UserRepository(
    private val userService: UserService,
    private val userDao: UserDao,
    private val obsClient: ObsClient,
    private val userInfoData: UserInfoData
) {

    suspend fun getEmailCode(email: String): NetData<Unit> {
        return try {
            userService.getEmailCode(email)
        } catch (e: Throwable) {
            NetData(ERROR, null, "服务器或网络异常")
        }
    }

    suspend fun register(
        email: String,
        username: String,
        passWord: String,
        code: Int
    ): NetData<String> {
        return try {
            userService.register(email, username, passWord, code)
        } catch (e: Throwable) {
            NetData(ERROR, null, "服务器或网络异常")
        }

    }

    suspend fun loginWithEmail(
        email: String,
        passWord: String
    ): NetData<String> {
        return try {
            val result = userService.loginWithEmail(email, passWord)
            if (result.code == 200) {
                getUser(result.data!!, true)
            }
            result
        } catch (e: Throwable) {
            NetData(ERROR, null, "服务器或网络异常")
        }
    }

    suspend fun hasUser(): NetData<Unit> {
        //todo(此处需要验证token是否登录后，获取最新的用户数据)
        val token = userInfoData.userInfo.value.userToken
        return try {
            val result = userService.testToken(token)
            if (result.code == SERVER_ERROR) {
                userDao.deleteUser()
                userInfoData.refreshUser()
            } else {
                getUser(token)
            }
            result
        } catch (e: Throwable) {
            NetData(ERROR, null, "服务器或网络异常")
        }
    }

    private suspend fun getUser(token: String, isLogin: Boolean = false) {
        flow {
            emit(userService.getUser(token))
        }.onEach {
            val user = it.data
            if (it.code == SUCCESS) {
                userDao.insertUser(
                    UserBean(user?.id!!, user.name, user.passwd, user.email, user.head)
                )
            }
        }.catch {
            NetData(ERROR, null, "服务器或网络异常")
        }.onCompletion {
            if (isLogin) {
                userInfoData.loginUser(token)
            } else {
                userInfoData.refreshUser()
            }
        }.flowOn(Dispatchers.IO).collect()
    }

    suspend fun logoutUser(): NetData<Unit> {
        return try {
            val token = userInfoData.userInfo.value.userToken
            val result = userService.logoutUser(token)
            if (result.code == SUCCESS) {
                userDao.deleteUser()
                userInfoData.logout()
            }
            result
        } catch (e: Throwable) {
            userDao.deleteUser()
            NetData(SUCCESS, null, "服务器或网络异常")
        }
    }

    suspend fun getEmailCodeWithLogin(email: String): NetData<Unit> {
        return try {
            userService.getEmailCodeWithLogin(email)
        } catch (e: Throwable) {
            NetData(ERROR, null, "服务器或网络异常")
        }
    }

    suspend fun loginWithCode(
        email: String,
        code: Int
    ): NetData<String> {
        return try {
            val result = userService.loginWithCode(email, code)
            if (result.code == SUCCESS) {
                getUser(result.data!!,true)
            }
            result
        } catch (e: Throwable) {
            NetData(ERROR, null, "服务器或网络异常")
        }
    }

    fun putFile(
        fileName: String,
        byte: InputStream?,
        file: DocumentFile?,
        progress: (ProgressStatus) -> Unit
    ): NetData<String> {
        return try {
            val request = PutObjectRequest(BUCKET_NAME, fileName)
                .apply {
                    metadata = ObjectMetadata().apply {
                        this.contentLength = file?.length()
                    }
                    input = byte
                    setProgressListener {
                        progress(it)
                    }
                }
            return NetData(SUCCESS, obsClient.putObject(request).objectUrl, "")
//            updateUser(obsClient.putObject(request))
        } catch (e: ObsException) {
            NetData(ERROR, null, "服务器异常")
        }
    }

    suspend fun updateUser(url: String) {
        val user = userInfoData.userInfo.value.userBean
        val netUser =
            NetUser(user?.id, user?.userName!!, user.email, user.password, url)
        val token = userInfoData.userInfo.value.userToken
        try {
            val updateResult = userService.setUser(token, netUser)
            if (updateResult.code == SUCCESS) {
                getUser(token = token)
                NetData(SUCCESS, null, "成功")
            } else {
                NetData(SERVER_ERROR, null, "服务器异常")
            }
        } catch (e: Throwable) {
            NetData(ERROR, null, "服务器或网络异常")
        }
    }
}