package com.hua.abstractmusic.repository

import androidx.documentfile.provider.DocumentFile
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.user.NetUser
import com.hua.abstractmusic.bean.user.UserBean
import com.hua.abstractmusic.db.user.UserDao
import com.hua.abstractmusic.net.UserService
import com.hua.abstractmusic.other.Constant.BUCKET_NAME
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.other.NetWork.NO_USER
import com.hua.abstractmusic.other.NetWork.SERVER_ERROR
import com.hua.abstractmusic.other.NetWork.SUCCESS
import com.obs.services.ObsClient
import com.obs.services.exception.ObsException
import com.obs.services.model.ObjectMetadata
import com.obs.services.model.ProgressStatus
import com.obs.services.model.PutObjectRequest
import java.io.InputStream

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   : 仓库
 */
class UserRepository(
    private val userService: UserService,
    private val dao: UserDao,
    private val obsClient: ObsClient
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
                getUser(result.data!!).data?.let {
                    dao.insertUser(
                        UserBean(it.id!!, it.name, it.passwd, it.email, result.data, it.head)
                    )
                }
            }
            result
        } catch (e: Throwable) {
            NetData(ERROR, null, "服务器或网络异常")
        }
    }

    suspend fun hasUser(): NetData<Unit> {
        if (dao.userInRoom() == 0) {
            return NetData(NO_USER, null, "本地无用户")
        }
        //todo(此处需要验证token是否登录后，获取最新的用户数据)
        val token = dao.getToken()
        return try {
            val result = userService.testToken(token)
            if (result.code == SERVER_ERROR) {
                dao.deleteUser()
            }
            result
        } catch (e: Throwable) {
            NetData(ERROR, null, "服务器或网络异常")
        }
    }

    private suspend fun getUser(token: String): NetData<NetUser> {
        return try {
            userService.getUser(token)
        } catch (e: Throwable) {
            NetData(ERROR, null, "服务器或网络异常")
        }
    }

    suspend fun getInfo(): UserBean? {
        return dao.getUserInfo()
    }

    suspend fun logoutUser(): NetData<Unit> {
        return try {
            val token = dao.getToken()
            val result = userService.logoutUser(token)
            if (result.code == SUCCESS) {
                dao.deleteUser()
            }
            result
        } catch (e: Throwable) {
            dao.deleteUser()
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
                val user = userService.getUser(result.data!!).data
                user?.let {
                    dao.insertUser(
                        UserBean(it.id!!, it.name, it.passwd, it.email, result.data, it.head)
                    )
                }
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
            return NetData(SUCCESS,obsClient.putObject(request).objectUrl,"")
//            updateUser(obsClient.putObject(request))
        } catch (e: ObsException) {
            NetData(ERROR, null, "服务器异常")
        }
    }

    suspend fun updateUser(url:String): NetData<Unit> {
        val user = dao.getUserInfo()
        val netUser =
            NetUser(user?.id, user?.userName!!, user.email, user.password, url)
        return try {
            if (userService.setUser(user.token, netUser).code == 200) {
                val result = userService.getUser(dao.getToken())
                if (result.code == 200) {
                    val data = result.data!!
                    dao.insertUser(
                        UserBean(
                            data.id!!,
                            data.name,
                            data.passwd,
                            data.email,
                            user.token,
                            data.head
                        )
                    )
                    NetData(SUCCESS, null, "成功")
                } else {
                    NetData(SERVER_ERROR, null, "服务器异常")
                }
            } else {
                NetData(SERVER_ERROR, null, "服务器异常")
            }
        } catch (e: Throwable) {
            NetData(ERROR, null, "服务器或网络异常")
        }
    }
}