package com.hua.abstractmusic.ui.viewmodels

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import com.hua.abstractmusic.base.BaseBrowserViewModel
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.user.UserBean
import com.hua.abstractmusic.other.Constant.BUCKET_HEAD_IMG
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.other.NetWork.NO_USER
import com.hua.abstractmusic.other.NetWork.SERVER_ERROR
import com.hua.abstractmusic.other.NetWork.SUCCESS
import com.hua.abstractmusic.repository.UserRepository
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.utils.toDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    itemTree: MediaItemTree,
    private val repository: UserRepository
) : BaseBrowserViewModel(application, useCase,itemTree) {

    private val _userIsOut = mutableStateOf(true)
    val userIsOut: State<Boolean> get() = _userIsOut


    suspend fun checkUser():NetData<Unit>{
        val result = repository.hasUser()
        val code = result.code
        _userIsOut.value = when (code) {
            SUCCESS, ERROR -> false
            SERVER_ERROR, NO_USER -> true
            else -> false
        }
        return result
    }


    val user = mutableStateOf(UserBean(0, "", "", "", "", ""))

    fun selectUserInfo() {
        viewModelScope.launch {
            repository.getInfo()?.let {
                user.value = it

            }
        }
    }

    fun logoutUser(){
        viewModelScope.launch {
            _userIsOut.value = repository.logoutUser().code == SUCCESS
        }
//        return repository.logoutUser().code

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

    suspend fun register(): NetData<String> {
        return repository.register(
            registerEmailText.value,
            registerNameText.value,
            registerPasswordText.value,
            registerEmailCodeText.value.toInt()
        )
    }

    suspend fun login(isPassWord: Boolean): NetData<String> {
        return if (isPassWord) loginWithEmail() else loginWithCode()
    }

    private suspend fun loginWithEmail(): NetData<String> {
        val result = repository.loginWithEmail(
            loginEmailText.value,
            loginPasswordText.value
        )
        _userIsOut.value = !(result.code == SUCCESS)
        return result
    }

    private suspend fun loginWithCode(): NetData<String> {
        val result = repository.loginWithCode(
            loginEmailText.value,
            loginEmailCodeText.value.toInt()
        )
        _userIsOut.value = !(result.code == SUCCESS)
        return result
    }

    fun putHeadPicture(
        url: String,
        contentResolver: ContentResolver
    ) {
        val uri = Uri.parse(url)
        val byte = contentResolver.openInputStream(uri)?.readBytes()
        val file = DocumentFile.fromSingleUri(getApplication(), uri)
        viewModelScope.launch(Dispatchers.IO) {
            val fileName = "${BUCKET_HEAD_IMG}/${user.value.id}-head-${
                System.currentTimeMillis().toDate()
            }.png"
            val result = repository.putHeadPicture(
                fileName,
                byte,
                file,
            ) {
                Log.d("TAG", "putHeadPicture: ${it.transferPercentage}")
            }
            if (result.code == 200) {
                selectUserInfo()
            }
            contentResolver.delete(uri,null,null)
        }
    }
}