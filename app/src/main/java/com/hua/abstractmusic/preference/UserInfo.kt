package com.hua.abstractmusic.preference

import com.hua.abstractmusic.db.user.UserDao
import com.hua.model.user.UserPO
import com.hua.service.preference.PreferenceManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _registerEnabled = MutableStateFlow(false)
    val registerEnabled :StateFlow<Boolean> get()= _registerEnabled

    private val _registerTime = MutableStateFlow(0)
    val registerTime:StateFlow<Int> get() = _registerTime

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

    fun actionRegister(){
        _registerEnabled.value = true
        serviceScope.launch {
            for (i in 120 downTo 0) {
                _registerTime.value = i
                delay(1000L)
            }
            _registerEnabled.value = false
        }
    }


    fun loginUser(userToken: String) {
        serviceScope.launch {
            preferenceManager.userToken = userToken
            refreshUser()
//            _userInfo.value = _userInfo.value.copy(
//                isLogin = preferenceManager.userToken.isNotBlank(),
//                userToken = preferenceManager.userToken,
//                userBean = userDao.getUserInfo()
//            )
        }

    }

    fun logout() {
        preferenceManager.userToken = ""
        refreshUser()
//        _userInfo.value = _userInfo.value.copy(
//            isLogin = false,
//            userToken = "",
//            userBean = null
//        )
    }
}

data class UserInfo(
    val isLogin: Boolean,
    val userToken: String,
    val userBean: UserPO?
)