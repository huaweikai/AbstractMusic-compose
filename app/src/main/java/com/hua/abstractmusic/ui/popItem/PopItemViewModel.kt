package com.hua.abstractmusic.ui.popItem

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.repository.LocalRepository
import com.hua.abstractmusic.repository.NetWorkRepository
import com.hua.abstractmusic.utils.isLocal
import com.hua.abstractmusic.utils.toMediaItem
import com.hua.model.other.Constants.LOCAL_SHEET_ID
import com.hua.model.parcel.ParcelizeMediaItem
import com.hua.network.ApiResult
import com.hua.network.get
import com.hua.network.onFailure
import com.hua.network.onSuccess
import com.hua.service.MediaConnect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/03/26
 * @Desc   :
 */
@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
class PopItemViewModel @Inject constructor(
    private val netRepository: NetWorkRepository,
    private val repository: LocalRepository,
    private val mediaConnect: MediaConnect,
    private val userInfo: UserInfoData
) : ViewModel() {
    val user get() = userInfo.userInfo.value

    val sheetList = mutableStateOf(emptyList<MediaItem>())

    val snackEvent = MutableSharedFlow<SnackData>()

    suspend fun refresh(isLocal: Boolean) {
        if (isLocal) {
            sheetList.value = mediaConnect.getChildren(LOCAL_SHEET_ID)
        } else {
            val result = netRepository.selectUserSheet()
            if (result is ApiResult.Success) {
                sheetList.value = result.data
            }
        }
    }

    val moreArtistList = mutableStateOf(emptyList<MediaItem>())


    fun selectArtistByMusicId(item: MediaItem) {
        viewModelScope.launch {
            val result = if (item.mediaId.isLocal()) {
                repository.selectArtistByMusicId(item)
            } else {
                netRepository.selectArtistByMusicId(item)
            }
            moreArtistList.value = result.get { listOf(NULL_MEDIA_ITEM) }
        }

    }

    fun clearArtist() {
        moreArtistList.value = emptyList()
    }


    fun addQueue(item: MediaItem, nextPlay: Boolean = false): Int {
        val browser = mediaConnect.browser ?: return 0
        val index = if (nextPlay) {
            showSnackBar("${item.mediaMetadata.title}将在下一首播放")
            browser.currentMediaItemIndex + 1
        } else {
            showSnackBar("${item.mediaMetadata.title}已添加到队尾")
            browser.mediaItemCount
        }
        browser.addMediaItem(index, item)
        // 没有相关回调，直接手动更新
        mediaConnect.updatePlayList()
        return index
    }

    fun insertMusicToSheet(
        mediaItem: MediaItem,
        sheetItem: MediaItem
    ) {
        viewModelScope.launch {
            val sheetId = Uri.parse(sheetItem.mediaId).lastPathSegment
            val result = if (sheetItem.mediaId.isLocal()) {
                repository.insertMusicToSheet(sheetId!!, mediaItem)
            } else {
                netRepository.insertMusicToSheet(sheetId!!, mediaItem)
            }
            result.onSuccess {
                showSnackBar("${mediaItem.mediaMetadata.title} 加入歌单 ${sheetItem.mediaMetadata.title} 成功")
            }.onFailure {
                showSnackBar(it.errorMsg ?: "")
            }
        }
    }


    val moreAlbum = mutableStateOf(NULL_MEDIA_ITEM)

    fun selectAlbumByMusicId(item: MediaItem) {
        viewModelScope.launch {
            val result = if (item.mediaId.isLocal()) {
                repository.selectAlbumByMusicId(item)
            } else {
                netRepository.selectAlbumByMusicId(item)
            }
            moreAlbum.value = result.get { NULL_MEDIA_ITEM }
        }
    }

    fun clearAlbum() {
        moreAlbum.value = Constant.NULL_MEDIA_ITEM
    }

    private fun showSnackBar(message: String) {
        viewModelScope.launch {
            snackEvent.emit(
                SnackData(message = message)
            )
        }
    }

}

data class SnackData(
    val time: Long = System.currentTimeMillis(),
    val message: String
)
