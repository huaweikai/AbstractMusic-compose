package com.hua.abstractmusic.ui.viewmodels

import android.content.ContentResolver
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.NetWork
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.repository.UserRepository
import com.hua.abstractmusic.services.MediaConnect
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.use_case.events.MusicInsertError
import com.hua.abstractmusic.utils.isLocal
import com.hua.abstractmusic.utils.toDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/03/25
 * @Desc   :
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    mediaConnect: MediaConnect,
    private val itemTree: MediaItemTree,
    private val netRepository: NetRepository,
    private val userRepository: UserRepository,
    private val useCase: UseCase,
    private val userInfoData: UserInfoData
) : BaseViewModel(mediaConnect) {
    val userInfo get() = userInfoData.userInfo


    suspend fun checkUser(): NetData<Unit> {
        return userRepository.hasUser()
    }


    val sheetList = mutableStateOf<List<MediaData>>(emptyList())
    val netSheetList = mutableStateOf<List<MediaItem>>(emptyList())

    init {
        localListMap[Constant.LOCAL_SHEET_ID] = sheetList
    }

    fun selectNetWork() {
        viewModelScope.launch {
            val result = netRepository.selectUserSheet()
            netSheetList.value = if (result.isSuccess) {
                result.getOrNull() ?: emptyList()
            } else {
                emptyList()
            }
            itemTree.addMusicToTree("${Constant.ROOT_SCHEME}${userInfo.value.userToken}", netSheetList.value)
        }
    }

    fun putHeadPicture(
        url: String,
        contentResolver: ContentResolver
    ) {
        val uri = Uri.parse(url)
        val byte = contentResolver.openInputStream(uri)
        val file = DocumentFile.fromSingleUri(mediaConnect.context, uri)
        viewModelScope.launch(Dispatchers.IO) {
            val fileName = "${Constant.BUCKET_HEAD_IMG}/${userInfo.value.userBean?.id}-head-${
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


    fun logoutUser() {
        viewModelScope.launch {
            val result = userRepository.logoutUser().code
            val isSuccess = result == NetWork.SUCCESS
            if (isSuccess) {
                netSheetList.value = emptyList()
            }
        }
    }

    fun createSheet(sheetName: String, isLocal: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                if (isLocal) {
                    useCase.insertSheetCase(sheetName)
                } else {
                    netRepository.createNewSheet(sheetName)
                }
                refresh()
                Toast.makeText(mediaConnect.context, "创建歌单成功", Toast.LENGTH_SHORT).show()
            } catch (e: MusicInsertError) {
                Toast.makeText(mediaConnect.context, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteSheet(sheetId: String) {
        viewModelScope.launch {
            val id = Uri.parse(sheetId).lastPathSegment
            try {
                if (sheetId.isLocal()) {
                    userRepository.removeSheet(id!!)
                } else {
                    netRepository.removeSheet(id!!)
                }
                refresh()
            } catch (e: Exception) {
            }

        }
    }
}