package com.hua.abstractmusic.ui.home.mine.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.other.NetWork
import com.hua.abstractmusic.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/03/02
 * @Desc   :
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) :ViewModel(){

    private val _loginCodeText = mutableStateOf("点击获取验证码")
    val loginCodeText: State<String> get() = _loginCodeText


    val loginEmailText = mutableStateOf("")
    val loginPasswordText = mutableStateOf("")
    val loginEmailCodeText = mutableStateOf("")
    val loginCodeIsWait = mutableStateOf(false)
    val loginEmailError = mutableStateOf(false)
    val loginPassWordError = mutableStateOf(false)
    val loginEmailCodeError = mutableStateOf(false)
    val loginEmailCodeEnable = mutableStateOf(false)

    fun loginClear() {
        loginEmailText.value = ""
        loginPasswordText.value = ""
        loginEmailCodeText.value = ""
        loginEmailError.value = false
        loginPassWordError.value = false
        loginEmailCodeEnable.value = false
        loginEmailCodeError.value = false
    }

    suspend fun getLoginEmailCode(): String {
        loginEmailCodeEnable.value = false
        val result = userRepository.getEmailCodeWithLogin(loginEmailText.value)
        if (result.code == NetWork.SUCCESS) {
            viewModelScope.launch {
                loginCodeIsWait.value = true
                for (i in 120 downTo 0) {
                    _loginCodeText.value = "再获取还需${i}秒"
                    delay(1000L)
                }
                _loginCodeText.value = "点击获取验证码"
                loginCodeIsWait.value = false
                loginEmailCodeEnable.value = true
            }
        } else if (result.code == NetWork.SERVER_ERROR || result.code == NetWork.ERROR) {
            loginEmailCodeEnable.value = true
        }
        return result.msg
    }

    suspend fun login(isPassWord: Boolean): NetData<String> {
        return if (isPassWord) loginWithEmail() else loginWithCode()
//        val isSuccess = result.code == NetWork.SUCCESS
//        if (isSuccess) {
//            refresh()
//        }
//        return result
    }

    private suspend fun loginWithEmail(): NetData<String> {
        return  userRepository.loginWithEmail(
            loginEmailText.value,
            loginPasswordText.value
        )
    }

    private suspend fun loginWithCode(): NetData<String> {
        return userRepository.loginWithCode(
            loginEmailText.value,
            loginEmailCodeText.value.toInt()
        )
    }

}