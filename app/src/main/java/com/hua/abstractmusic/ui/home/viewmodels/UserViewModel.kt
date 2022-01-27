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
    private val dao: UserDao
) : BaseBrowserViewModel(application, useCase) {

    private val _userIsOut = mutableStateOf(true)
    val userIsOut: State<Boolean> get() = _userIsOut

    init {
        viewModelScope.launch {
            val code = useCase.userTokenOut().code
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
            useCase.userInfoCase()?.let {
                user.value = it
            }
        }
    }

    suspend fun logoutUser() {
        _userIsOut.value = useCase.userLogoutCase(dao.getToken()).code == SUCCESS
    }


    private val _codeText = mutableStateOf("点击获取验证码")
    val codeText: State<String> get() = _codeText

    private val _loginCodeText = mutableStateOf("点击获取验证码")
    val loginCodeText: State<String> get() = _loginCodeText


    val loginEmailText = mutableStateOf("")
    val loginPasswordText = mutableStateOf("")
    val loginEmailError = mutableStateOf(false)
    val loginPassWordError = mutableStateOf(false)
    val loginEmailCodeText = mutableStateOf("")
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
    val registerEmailCode = mutableStateOf("")
    val registerNameText = mutableStateOf("")
    val registerEmailError = mutableStateOf(false)
    val registerPassWordAgainError = mutableStateOf(false)
    val registerCodeError = mutableStateOf(false)
    val registerPassWordError = mutableStateOf(false)
    val registerButtonEnabled = mutableStateOf(false)
    private val nullNetData = NetData<String>(0, null, "")
    private val _registerState = mutableStateOf(nullNetData)
    val registerState: State<NetData<String>> get() = _registerState
    val registerCodeButton = mutableStateOf(false)
    val registerNameError = mutableStateOf(false)


    fun registerClear() {
        //在退出注册时，将已有数据清空
        registerEmailText.value = ""
        registerPasswordText.value = ""
        registerPasswordAgainText.value = ""
        registerEmailCode.value = ""
        registerEmailError.value = false
        registerPassWordAgainError.value = false
        registerCodeError.value = false
        registerPassWordError.value = false
        registerButtonEnabled.value = false
        _registerState.value = nullNetData
        registerCodeButton.value = false
        registerNameError.value = false
        registerNameText.value = ""
    }

    suspend fun getLoginEmailCode(): String {
        loginEmailCodeEnable.value = false
        val result = useCase.userRegisterCase.invoke(registerEmailText.value)
        if (result.code == SUCCESS) {
            viewModelScope.launch {
                for (i in 120 downTo 0) {
                    _loginCodeText.value = "再获取还需${i}秒"
                    delay(1000L)
                }
                _loginCodeText.value = "点击获取验证码"
                loginEmailCodeEnable.value = true
            }
        } else if (result.code == SERVER_ERROR || result.code == ERROR) {
            loginEmailCodeEnable.value = true
        }
        return result.msg
    }

    suspend fun getRegisterEmailCode(): String {
        registerCodeButton.value = false
        val result = useCase.userRegisterCase.invoke(registerEmailText.value)
        if (result.code == SUCCESS) {
            viewModelScope.launch {
                for (i in 120 downTo 0) {
                    _codeText.value = "再获取还需${i}秒"
                    delay(1000L)
                }
                _codeText.value = "点击获取验证码"
                registerCodeButton.value = true
            }
        } else if (result.code == SERVER_ERROR || result.code == ERROR) {
            registerCodeButton.value = true
        }
        return result.msg
    }

    suspend fun register() {
        if (!registerCodeError.value && !registerEmailError.value) {
            _registerState.value = useCase.userRegisterCase(
                registerEmailText.value,
                registerNameText.value,
                registerPasswordText.value,
                registerEmailCode.value.toInt()
            )
        }
    }

    suspend fun loginWithEmail(): NetData<String> {
        val result = useCase.userLoginCase(
            loginEmailText.value,
            loginPasswordText.value
        )
        _userIsOut.value = !(result.code == SUCCESS)
        return result
    }
}