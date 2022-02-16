package com.hua.abstractmusic.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media2.common.MediaItem
import androidx.media2.session.MediaBrowser
import androidx.media2.session.MediaController
import androidx.media2.session.MediaLibraryService
import androidx.media2.session.SessionCommandGroup
import com.hua.abstractmusic.base.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/01/13
 * @Desc   : viewmodel
 */
@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    itemTree: MediaItemTree
) : BaseBrowserViewModel(application, useCase, itemTree) {
    var id: String? = null
    override fun onMediaConnected(
        controller: MediaController,
        allowedCommands: SessionCommandGroup
    ) {
        listMap[id!!] = _albumDetail
        playListMap[id!!] = _albumDetail
        refresh()
//        listMap.keys.forEach {
//            detailInit(it)
//        }
    }

    private val _albumDetail = mutableStateOf<List<MediaData>>(emptyList())
    val albumDetail: State<List<MediaData>> get() = _albumDetail
}