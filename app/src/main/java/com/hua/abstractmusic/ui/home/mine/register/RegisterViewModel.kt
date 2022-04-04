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
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userInfoData: UserInfoData
):ViewModel() {
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

    private val _codeText = mutableStateOf("点击获取验证码")
    val codeText: State<String> get() = _codeText

    val registerTimeEnable get() =  userInfoData.registerEnabled


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

    suspend fun getRegisterEmailCode(): String {
        registerCodeButtonEnabled.value = false
        val result = userRepository.getEmailCode(registerEmailText.value)
        if (result.code == NetWork.SUCCESS) {
            userInfoData.actionRegister()
        } else if (result.code == NetWork.SERVER_ERROR || result.code == NetWork.ERROR) {
            registerCodeButtonEnabled.value = true
        }
        return result.msg
    }

    init {
        viewModelScope.launch {
            userInfoData.registerEnabled.collect{
                registerCodeButtonEnabled.value = !it
                if(it){
                    startCodeTime()
                }else{
                    cancelCodeTimeJob()
                    _codeText.value = "点击获取验证码"
                }
            }
        }
    }

    private var codeTimeJob :Job ?= null

    fun startCodeTime(){
        cancelCodeTimeJob()
        codeTimeJob = viewModelScope.launch {
            userInfoData.registerTime.collect{
                _codeText.value = "再获取还需${it}秒"
            }
        }
    }

    fun cancelCodeTimeJob(){
        codeTimeJob?.cancel()
    }

    suspend fun register(): NetData<String> {
        return userRepository.register(
            registerEmailText.value,
            registerNameText.value,
            registerPasswordText.value,
            registerEmailCodeText.value.toInt()
        )
    }
}