package com.hua.abstractmusic.ui.home.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.hua.abstractmusic.base.BaseBrowserViewModel
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.user.UserBean
import com.hua.abstractmusic.db.user.UserDao
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.other.NetWork.NO_USER
import com.hua.abstractmusic.other.NetWork.SERVER_ERROR
import com.hua.abstractmusic.other.NetWork.SUCCESS
import com.hua.abstractmusic.repository.UserRepository
import com.hua.abstractmusic.use_case.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   :
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    private val repository: UserRepository
) : BaseBrowserViewModel(application, useCase) {

    private val _userIsOut = mutableStateOf(true)
    val userIsOut: State<Boolean> get() = _userIsOut

    init {
        viewModelScope.launch {
            val code = repository.hasUser().code
            _userIsOut.value = when (code) {
                SUCCESS, ERROR -> false
                SERVER_ERROR, NO_USER -> true
                else -> false
            }
        }
    }

    override fun initializeController() {

    }

    val user = mutableStateOf(UserBean(0, "", "", "", ""))

    fun selectUserInfo() {
        viewModelScope.launch {
            repository.getInfo()?.let {
                user.value = it
            }
        }
    }

    suspend fun logoutUser() {
        _userIsOut.value = repository.logoutUser().code == SUCCESS
    }


    private val _codeText = mutableStateOf("点击获取验证码")
    val codeText: State<String> get() = _codeText

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


    //注册时，防止横屏数据消失
    val registerEmailText = mutableStateOf("")
    val registerPasswordText = mutableStateOf("")
    val registerPasswordAgainText = mutableStateOf("")
    val registerEmailCodeText = mutableStateOf("")
    val registerNameText = mutableStateOf("")
    val registerEmailError = mutableStateOf(false)
    val registerPassWordAgainError = mutableStateOf(false)
    val registerEmailCodeError = mutableStateOf(false)
    val registerPassWordError = mutableStateOf(false)
    val registerNameError = mutableStateOf(false)
    val registerButtonEnabled = mutableStateOf(false)
    val registerCodeButtonEnabled = mutableStateOf(false)



    fun registerClear() {
        //在退出注册时，将已有数据清空
        registerEmailText.value = ""
        registerPasswordText.value = ""
        registerPasswordAgainText.value = ""
        registerEmailCodeText.value = ""
        registerEmailError.value = false
        registerPassWordAgainError.value = false
        registerEmailCodeError.value = false
        registerPassWordError.value = false
        registerButtonEnabled.value = false
        registerCodeButtonEnabled.value = false
        registerNameError.value = false
        registerNameText.value = ""
    }

    suspend fun getLoginEmailCode(): String {
        loginEmailCodeEnable.value = false
        val result = repository.getEmailCodeWithLogin(loginEmailText.value)
        if (result.code == SUCCESS) {
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
        } else if (result.code == SERVER_ERROR || result.code == ERROR) {
            loginEmailCodeEnable.value = true
        }
        return result.msg
    }

    suspend fun getRegisterEmailCode(): String {
        registerCodeButtonEnabled.value = false
        val result = repository.getEmailCode(registerEmailText.value)
        if (result.code == SUCCESS) {
            viewModelScope.launch {
                for (i in 120 downTo 0) {
                    _codeText.value = "再获取还需${i}秒"
                    delay(1000L)
                }
                _codeText.value = "点击获取验证码"
                registerCodeButtonEnabled.value = true
            }
        } else if (result.code == SERVER_ERROR || result.code == ERROR) {
            registerCodeButtonEnabled.value = true
        }
        return result.msg
    }

    suspend fun register():NetData<String> {
        return repository.register(
            registerEmailText.value,
            registerNameText.value,
            registerPasswordText.value,
            registerEmailCodeText.value.toInt()
        )
    }

    suspend fun login(isPassWord:Boolean):NetData<String>{
        return if (isPassWord) loginWithEmail() else loginWithCode()
    }

    suspend fun loginWithEmail(): NetData<String> {
        val result = repository.loginWithEmail(
            loginEmailText.value,
            loginPasswordText.value
        )
        _userIsOut.value = !(result.code == SUCCESS)
        return result
    }

    suspend fun loginWithCode(): NetData<String> {
        val result = repository.loginWithCode(
            loginEmailText.value,
            loginEmailCodeText.value.toInt()
        )
        _userIsOut.value = !(result.code == SUCCESS)
        return result
    }
}