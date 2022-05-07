package com.hua.abstractmusic.ui.setting

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.repository.UserRepository
import com.hua.abstractmusic.utils.toDate
import com.hua.model.user.UserPO
import com.hua.network.ApiResult
import com.hua.service.MediaConnect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    init {
        checkUser()
    }

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
            }
            is UserChangeAction.UserHead -> {
                _userInfo.value = _userInfo.value.copy(
                    head = userChangeAction.data
                )
            }
            is UserChangeAction.SaveUser -> {
                viewModelScope.launch {
                    userRepository.updateUser(_userInfo.value)
                    if (_userInfo.value.head != userInfoData.userInfo.value.userBean?.head && _userInfo.value.head != null) {
                        putHeadPicture(_userInfo.value.head!!, userChangeAction.contentResolver)
                    }
                }
            }
        }
    }

    private fun putHeadPicture(
        url: String,
        contentResolver: ContentResolver
    ) {
        val uri = Uri.parse(url)
        val byte = contentResolver.openInputStream(uri)
        val file = DocumentFile.fromSingleUri(mediaConnect.context, uri)
        viewModelScope.launch(Dispatchers.IO) {
            val fileName = "${Constant.BUCKET_HEAD_IMG}/${userInfo.value.id}-head-${
                System.currentTimeMillis().toDate()
            }.png"
            userRepository.upLoadFile.putFile(fileName, byte, file, onSuccess = {
                userRepository.updateUser(it)
            }, onError = {

            }, onCompletion = {
                contentResolver.delete(uri, null, null)
            })
        }
    }

}

sealed class UserChangeAction() {
    data class UserName(val data: String) : UserChangeAction()
    data class UserHead(val data: String) : UserChangeAction()
    data class SaveUser(val contentResolver: ContentResolver) : UserChangeAction()
}