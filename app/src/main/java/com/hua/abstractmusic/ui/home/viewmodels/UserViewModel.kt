package com.hua.abstractmusic.ui.home.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.hua.abstractmusic.base.BaseBrowserViewModel
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.other.NetWork.NO_USER
import com.hua.abstractmusic.other.NetWork.SERVER_ERROR
import com.hua.abstractmusic.other.NetWork.SUCCESS
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.utils.isEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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

):BaseBrowserViewModel(application, useCase) {

    private val _userIsOut = mutableStateOf(true)
    val userIsOut :State<Boolean> get() = _userIsOut

    init {
        viewModelScope.launch {
            val code = useCase.userTokenOut().code
            _userIsOut.value =when(code){
                SUCCESS, ERROR -> true
                SERVER_ERROR, NO_USER -> false
                else -> false
            }
        }
    }

    override fun initializeController() {

    }

    private val _codeButton = mutableStateOf(true)
    val codeButton:State<Boolean> get()= _codeButton

    private val _codeText = mutableStateOf("点击获取验证码")
    val codeText: State<String> get() = _codeText



    val loginEmailText = mutableStateOf("")
    val loginPasswordText = mutableStateOf("")

    val loginEmailError = mutableStateOf(false)

    val registerEmailText = mutableStateOf("")
    val registerPasswordText = mutableStateOf("")
    val registerPasswordAgainText = mutableStateOf("")
    val registerEmailCode = mutableStateOf("")
    val registerEmailError = mutableStateOf(false)
    val registerPassWordAgainError = mutableStateOf(false)
    val registerCodeError = mutableStateOf(false)



    suspend fun getEmailCode():String{
        _codeButton.value = false
        val result = useCase.userRegisterCase.invoke(registerEmailText.value)
        if(result.code == 200){
            viewModelScope.launch {
                for (i in 120 downTo 0){
                    _codeText.value = "重新获取，还需${i}秒"
                    delay(1000L)
                }
                _codeText.value = "点击获取验证码"
                _codeButton.value = true
            }
        }else if(result.code == 500){
            _codeButton.value = true
        }
        return result.msg
    }

    suspend fun register(){
        if(!registerCodeError.value && !registerEmailError.value){
            val result = useCase.userRegisterCase(
                registerEmailText.value,
                registerPasswordText.value,
                registerEmailCode.value.toInt())
        }
    }

    suspend fun login():Boolean{
        _userIsOut.value = !useCase.userLoginCase(
            loginEmailText.value,
            loginPasswordText.value
        )
        return _userIsOut.value
    }
}