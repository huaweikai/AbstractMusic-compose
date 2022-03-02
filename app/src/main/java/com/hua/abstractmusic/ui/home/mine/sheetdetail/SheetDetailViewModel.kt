package com.hua.abstractmusic.ui.home.mine.sheetdetail

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import com.hua.abstractmusic.base.viewmodel.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.net.NetSheet
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.NetWork
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.other.NetWork.SUCCESS
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.repository.Repository
import com.hua.abstractmusic.repository.UserRepository
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.utils.toDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/02/28
 * @Desc   :
 */
@HiltViewModel
@SuppressLint("UnsafeOptInUsageError")
class SheetDetailViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    itemTree: MediaItemTree,
    private val userRepository: UserRepository,
    private val netRepository: NetRepository,
    private val repository: Repository
) : BaseBrowserViewModel(application, useCase, itemTree) {
    var sheetId: String? = null
    var isLocal: Boolean = false

    val sheetDetailList = mutableStateOf<List<MediaData>>(emptyList())
    var mediaData: MediaData? = null

    val sheetDetail = MutableStateFlow(NetSheet(0, 0, ""))

    override fun onMediaConnected() {
        if (isLocal) {
            sheetId?.let {
                localListMap[it] = sheetDetailList
            }
        } else {
            sheetId?.let {
                updateSheet()
                netListMap[it] = sheetDetailList
            }
        }
        playListMap[sheetId ?: ""] = sheetDetailList
        refresh()
    }

    fun updateSheet() {
        viewModelScope.launch {
            sheetDetail.value = netRepository.selectSheetById(Uri.parse(sheetId))
        }
    }

    fun putSheetArt(
        url: String,
        contentResolver: ContentResolver,
    ) {
        val uri = Uri.parse(url)
        val byte = contentResolver.openInputStream(uri)
        val file = DocumentFile.fromSingleUri(getApplication(), uri)
        viewModelScope.launch(Dispatchers.IO) {
            val fileName = "${Constant.BUCKET_SHEET_IMG}/${sheetId}-sheet-${
                System.currentTimeMillis().toDate()
            }.png"
            val result = userRepository.putFile(
                fileName,
                byte,
                file
            ) {
                Log.d("TAG", "putHeadPicture: ${it.transferPercentage}")
            }
            if (result.code == NetWork.SUCCESS) {
                netRepository.updateSheet(
                    sheetDetail.value.copy(artUri = result.data)
                ).also {
                    if (it.code == NetWork.SUCCESS) {
                        updateSheet()
                    }
                }
            }
            contentResolver.delete(uri, null, null)
        }
    }

    suspend fun removeNetSheetItem(
        id: String
    ): NetData<Unit> {
        val musicId = Uri.parse(id).lastPathSegment!!
        val sheetId = Uri.parse(sheetId).lastPathSegment!!
        return if (!isLocal) {
            val result = netRepository.removeSheetItem(sheetId, musicId)
            val list = sheetDetailList.value.toMutableList()
            list.remove(list.find { it.mediaId == id })
            sheetDetailList.value = list
            result
        } else {
            if (repository.removeSheetItem(sheetId, musicId) == 1) {
                refresh()
                NetData(SUCCESS, null, "")
            } else {
                NetData(ERROR, null, "移出失败，稍后尝试")
            }
        }
    }
}