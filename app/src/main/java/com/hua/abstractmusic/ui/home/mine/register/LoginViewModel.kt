package com.hua.abstractmusic.ui.home.mine.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.other.NetWork
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
    private val userRepository: UserRepository,
    private val userInfoData: UserInfoData
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

    val registerTimeEnable get() =  userInfoData.registerEnabled

    fun loginClear() {
        loginEmailText.value = ""
        loginPasswordText.value = ""
        loginEmailCodeText.value = ""
        loginEmailError.value = false
        loginPassWordError.value = false
        loginEmailCodeEnable.value = false
        loginEmailCodeError.value = false
    }

    init {
        viewModelScope.launch {
            userInfoData.registerEnabled.collect{
                loginEmailCodeEnable.value = !it
                if(it){
                    startCodeTime()
                }else{
                    cancelCodeTimeJob()
                    _loginCodeText.value = "点击获取验证码"
                }
            }
        }
    }

    private var codeTimeJob : Job?= null

    fun startCodeTime(){
        cancelCodeTimeJob()
        codeTimeJob = viewModelScope.launch {
            userInfoData.registerTime.collect{
                _loginCodeText.value = "再获取还需${it}秒"
            }
        }
    }

    fun cancelCodeTimeJob(){
        codeTimeJob?.cancel()
    }


    suspend fun getLoginEmailCode(): String {
        loginEmailCodeEnable.value = false
        val result = userRepository.getEmailCodeWithLogin(loginEmailText.value)
        if (result.code == NetWork.SUCCESS) {
            userInfoData.actionRegister()
        } else if (result.code == NetWork.SERVER_ERROR || result.code == NetWork.ERROR) {
            loginEmailCodeEnable.value = true
        }
        return result.msg
    }

    suspend fun login(isPassWord: Boolean): NetData<String> {
        return if (isPassWord) loginWithEmail() else loginWithCode()
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