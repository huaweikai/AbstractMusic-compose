package com.hua.abstractmusic.preference

import com.hua.abstractmusic.bean.user.UserBean
import com.hua.abstractmusic.db.user.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : huaweikai
 * @Date   : 2022/03/23
 * @Desc   :
 */
@Singleton
class UserInfoData @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val userDao: UserDao
) {
    // 协程组件
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private val _userInfo = MutableStateFlow(
        UserInfo(
            preferenceManager.userToken.isNotBlank(), preferenceManager.userToken, null
        )
    )
    val userInfo get() = _userInfo.asStateFlow()

    init {
        refreshUser()
    }

    fun readLoginState(): Flow<UserInfo> {
        return userInfo
    }

    fun refreshUser() {
        serviceScope.launch {
            _userInfo.value = _userInfo.value.copy(
                isLogin = preferenceManager.userToken.isNotBlank(),
                userToken = preferenceManager.userToken,
                userBean = userDao.getUserInfo()
            )
        }
    }

    fun loginUser(userToken: String) {
        serviceScope.launch {
            preferenceManager.userToken = userToken
            _userInfo.value = _userInfo.value.copy(
                isLogin = preferenceManager.userToken.isNotBlank(),
                userToken = preferenceManager.userToken,
                userBean = userDao.getUserInfo()
            )
        }

    }

    fun logout() {
        preferenceManager.userToken = ""
        _userInfo.value = _userInfo.value.copy(
            isLogin = false,
            userToken = "",
            userBean = null
        )

    }
}

data class UserInfo(
    val isLogin: Boolean,
    val userToken: String,
    val userBean: UserBean?
)