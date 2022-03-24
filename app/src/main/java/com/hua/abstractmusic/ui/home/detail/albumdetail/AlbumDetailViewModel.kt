package com.hua.abstractmusic.ui.home.detail.albumdetail

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import com.hua.abstractmusic.base.viewmodel.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import com.hua.blur.BlurLibrary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/01/13
 * @Desc   : viewmodel
 */
@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    itemTree: MediaItemTree,
    private val blurLibrary: BlurLibrary
) : BaseBrowserViewModel(application, useCase, itemTree) {
    var id: String? = null
    var isLocal: Boolean = true
    var item: MediaItem? = null
    override fun onMediaConnected() {
        if (isLocal) {
            localListMap[id!!] = _albumDetail
        } else {
            netListMap[id!!] = _albumDetail
        }
        playListMap[id!!] = _albumDetail
        refresh()
//        viewModelScope.launch(Dispatchers.IO){
//            delay(200)
//            withContext(Dispatchers.Main){
//
//            }
//        }
    }


    private val _albumDetail = mutableStateOf<List<MediaData>>(emptyList())
    val albumDetail: State<List<MediaData>> get() = _albumDetail
}