package com.hua.abstractmusic.ui.viewmodels

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.google.common.util.concurrent.MoreExecutors
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.repository.LocalRepository
import com.hua.abstractmusic.repository.NetWorkRepository
import com.hua.abstractmusic.repository.UserRepository
import com.hua.service.usecase.events.MusicInsertError
import com.hua.abstractmusic.utils.isLocal
import com.hua.abstractmusic.utils.toDate
import com.hua.model.music.MediaData
import com.hua.model.other.Constants
import com.hua.network.ApiResult
import com.hua.network.onFailure
import com.hua.network.onSuccess
import com.hua.service.MediaConnect
import com.hua.service.MediaItemTree
import com.hua.service.usecase.UseCase
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
    private val netRepository: NetWorkRepository,
    private val userRepository: UserRepository,
    private val repository: LocalRepository,
    private val userInfoData: UserInfoData
) : BaseViewModel(mediaConnect) {
    val userInfo get() = userInfoData.userInfo

    suspend fun checkUser(): ApiResult<Unit> {
        return userRepository.hasUser()
    }


    val sheetList = mutableStateOf<List<MediaData>>(emptyList())
    val netSheetList = mutableStateOf<List<MediaItem>>(emptyList())

    init {
        localListMap[Constants.LOCAL_SHEET_ID] = sheetList
    }

    fun selectNetWork() {
        viewModelScope.launch {
            val result = netRepository.selectUserSheet()
            netSheetList.value = if (result is ApiResult.Success) {
                result.data
            } else {
                emptyList()
            }
            itemTree.addMusicToTree(
                "${Constants.ROOT_SCHEME}${userInfo.value.userToken}",
                netSheetList.value
            )
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

    fun createSheet(sheetName: String, isLocal: Boolean) {
        viewModelScope.launch {
            val result = if (isLocal) {
                repository.createSheet(sheetName)
            }else{
                netRepository.createSheet(sheetName)
            }
            result.onSuccess {
                refresh()
                selectNetWork()
                Toast.makeText(mediaConnect.context, "创建歌单成功", Toast.LENGTH_SHORT).show()
            }
            result.onFailure {
                Toast.makeText(mediaConnect.context, "${it.errorMsg}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteSheet(sheetId: String) {
        viewModelScope.launch {
            val id = Uri.parse(sheetId).lastPathSegment
            try {
                if (sheetId.isLocal()) {
                    userRepository.removeLocalSheet(id!!)
                } else {
                    netRepository.deleteSheet(id!!)
                }
                refresh()
            } catch (e: Exception) {
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun listPlay(id:String){
        val browser = mediaConnect.browser?:return
        val childFeature = browser.getChildren(
            id,0, Int.MAX_VALUE,null
        )
        childFeature.addListener({
            setPlayList(0,childFeature.get()?.value ?: emptyList())
        },MoreExecutors.directExecutor())
    }
}