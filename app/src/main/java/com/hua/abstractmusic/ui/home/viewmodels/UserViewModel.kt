package com.hua.abstractmusic.ui.home.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.hua.abstractmusic.base.BaseBrowserViewModel
import com.hua.abstractmusic.use_case.UseCase
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

//    private val codeButton = mutableStateOf(true)
//
//    private val _codeText = mutableStateOf("点击获取验证码")
//    val codeText:State<String> get() = _codeText

    private val _userIsOut = mutableStateOf(true)
    val userIsOut :State<Boolean> get() = _userIsOut

    init {
        viewModelScope.launch {
            val code = useCase.userTokenOut().code
            _userIsOut.value =when(code){
                200 -> true
                500 -> false
                501 -> true
                502 -> false
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

    val emailText = mutableStateOf("")
    val passwordText = mutableStateOf("")
    val emailCode = mutableStateOf("")

    val loginEmailText = mutableStateOf("")
    val loginPasswordText = mutableStateOf("")

    val loginEmailError = mutableStateOf(false)

//    private val countDown =



    suspend fun getEmailCode():String{
        _codeButton.value = false
        val result = useCase.userRegisterCase.invoke(emailText.value)
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
        val result = useCase.userRegisterCase(emailText.value,passwordText.value,emailCode.value.toInt())
//        if (result.code == 200){
//            login(emailText.value,passwordText.value)
//        }
    }

    suspend fun login(email:String,password:String){
        _userIsOut.value = useCase.userLoginCase(email, password)
    }
}