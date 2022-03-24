package com.hua.abstractmusic.ui.viewmodels

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import com.hua.abstractmusic.base.viewmodel.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.NetWork.SUCCESS
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.repository.Repository
import com.hua.abstractmusic.repository.UserRepository
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
 * @Date   : 2022/01/25
 * @Desc   :
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    itemTree: MediaItemTree,
    private val netRepository: NetRepository,
    private val userRepository: UserRepository,
    private val repository: Repository,
    userInfoData: UserInfoData
) : BaseBrowserViewModel(application, useCase, itemTree) {

    val userInfo = userInfoData.userInfo

//    private val _userIsOut = mutableStateOf(true)
//    val userIsOut: State<Boolean> get() = _userIsOut


    suspend fun checkUser(): NetData<Unit> {
        return userRepository.hasUser()
    }


    val sheetList = mutableStateOf<List<MediaData>>(emptyList())
    val netSheetList = mutableStateOf<List<MediaData>>(emptyList())

    init {
        localListMap[Constant.LOCAL_SHEET_ID] = sheetList
        netListMap[Constant.NET_SHEET_ID] = netSheetList
        initializeController()
    }

    override fun onMediaConnected() {
        refresh()
    }

    fun putHeadPicture(
        url: String,
        contentResolver: ContentResolver
    ) {
        val uri = Uri.parse(url)
        val byte = contentResolver.openInputStream(uri)
        val file = DocumentFile.fromSingleUri(getApplication(), uri)
        viewModelScope.launch(Dispatchers.IO) {
            val fileName = "${Constant.BUCKET_HEAD_IMG}/${userInfo.value.userBean?.id}-head-${
                System.currentTimeMillis().toDate()
            }.png"
            val result = userRepository.putFile(
                fileName,
                byte,
                file,
            ) {
                Log.d("TAG", "putHeadPicture: ${it.transferPercentage}")
            }
            userRepository.updateUser(result.data!!)
            contentResolver.delete(uri, null, null)
        }
    }


    fun logoutUser() {
        viewModelScope.launch {
            val result = userRepository.logoutUser().code
            val isSuccess = result == SUCCESS
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
                Toast.makeText(getApplication(), "创建歌单成功", Toast.LENGTH_SHORT).show()
            } catch (e: MusicInsertError) {
                Toast.makeText(getApplication(), "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteSheet(sheetId: String) {
        viewModelScope.launch {
            val id = Uri.parse(sheetId).lastPathSegment
            try {
                if (sheetId.isLocal()) {
                    repository.removeSheet(id!!)
                } else {
                    netRepository.removeSheet(id!!)
                }
                refresh()
            } catch (e: Exception) {
            }

        }
    }
}