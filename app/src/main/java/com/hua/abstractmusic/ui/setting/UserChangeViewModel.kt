package com.hua.abstractmusic.ui.setting

import android.content.ContentResolver
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.repository.UserRepository
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.abstractmusic.utils.toDate
import com.hua.model.user.UserPO
import com.hua.network.ApiResult
import com.hua.network.get
import com.hua.network.onFailure
import com.hua.network.onSuccess
import com.hua.service.MediaConnect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/05/07
 * @Desc   :
 */
@HiltViewModel
class UserChangeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userInfoData: UserInfoData,
    mediaConnect: MediaConnect
) : BaseViewModel(mediaConnect) {

    val userStateInfo = userInfoData.userInfo

    private val _userInfo = mutableStateOf(userInfoData.userInfo.value.userBean!!)
    val userInfo: State<UserPO> get() = _userInfo

    private val _userPassWordCheck = mutableStateOf("")
    val userPassWordCheck :State<String> get() = _userPassWordCheck

    init {
        checkUser()
    }
    private val _userUpdateState = MutableStateFlow<LCE>(LCE.Success)
    val userUpdateState :StateFlow<LCE> get()= _userUpdateState

    private fun checkUser() {
        viewModelScope.launch {
            val result = userRepository.hasUser()
            if (result is ApiResult.Success) {
                _userInfo.value = userInfoData.userInfo.value.userBean!!
            }
        }
    }

    fun sendAction(userChangeAction: UserChangeAction) {
        when (userChangeAction) {
            is UserChangeAction.UserName -> {
                _userInfo.value = _userInfo.value.copy(
                    userName = userChangeAction.data
                )
                if(_userUpdateState.value == LCE.Error){
                    _userUpdateState.value = LCE.Success
                }
            }
            is UserChangeAction.UserHead -> {
                _userInfo.value = _userInfo.value.copy(
                    head = userChangeAction.data
                )
                if(_userUpdateState.value == LCE.Error){
                    _userUpdateState.value = LCE.Success
                }
            }
            is UserChangeAction.SaveUser -> {
                _userUpdateState.value = LCE.Loading
                viewModelScope.launch {
                    if (_userInfo.value.head != userInfoData.userInfo.value.userBean?.head && _userInfo.value.head != null) {
                        putHeadPicture(
                            _userInfo.value.head!!,
                            userChangeAction.contentResolver,
                            userChangeAction.success,
                            userChangeAction.error
                        )
                    } else {
                        userRepository.updateUser(_userInfo.value).onSuccess {
                            userChangeAction.success()
                            _userUpdateState.value = LCE.Success
                        }.onFailure {
                            userChangeAction.error(it.errorMsg ?:"")
                            _userUpdateState.value = LCE.Error
                        }
                    }
                }
            }
            is UserChangeAction.UserPassWord->{
                _userPassWordCheck.value = userChangeAction.data
            }
            is UserChangeAction.DeleteUser->{
                viewModelScope.launch {
                    userRepository.deleteUser(_userPassWordCheck.value).onSuccess {
                        userChangeAction.success()
                    }.onFailure {
                        userChangeAction.error(it.errorMsg ?:"")
                    }
                }

            }
        }
    }

    private fun putHeadPicture(
        url: String,
        contentResolver: ContentResolver,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uri = Uri.parse(url)
        val byte = contentResolver.openInputStream(uri)
        val file = DocumentFile.fromSingleUri(mediaConnect.context, uri)
        viewModelScope.launch(Dispatchers.IO) {
            val fileName = "${Constant.BUCKET_HEAD_IMG}/${userInfo.value.id}-head-${
                System.currentTimeMillis().toDate()
            }.png"
            userRepository.upLoadFile.putFile(fileName, byte, file,
                onSuccess = {
                    userRepository.updateUser(_userInfo.value.copy(head = it))
                    withContext(Dispatchers.Main){
                        onSuccess()
                        contentResolver.delete(uri, null, null)
                    }
                    _userUpdateState.tryEmit(LCE.Success)
                }, onError = {
                    withContext(Dispatchers.Main){
                        onError(it)
                    }
                    _userUpdateState.tryEmit(LCE.Error)
                }, onCompletion = {}
            )
        }
    }

}

sealed class UserChangeAction{
    data class UserName(val data: String) : UserChangeAction()
    data class UserHead(val data: String) : UserChangeAction()
    data class SaveUser(
        val contentResolver: ContentResolver,
        val success: () -> Unit,
        val error: (String) -> Unit
    ) : UserChangeAction()
    data class UserPassWord(val data:String):UserChangeAction()
    data class DeleteUser(
        val success: () -> Unit,
        val error: (String) -> Unit
    ):UserChangeAction()
}