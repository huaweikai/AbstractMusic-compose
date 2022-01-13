package com.hua.abstractmusic.ui.home.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
    private val itemTree: MediaItemTree
) :BaseBrowserViewModel(application, useCase){
    private val browserCallback = object :MediaBrowser.BrowserCallback(){
        override fun onConnected(
            controller: MediaController,
            allowedCommands: SessionCommandGroup
        ) {
//            init()
        }

        override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
            updateItem(item)
        }

        override fun onChildrenChanged(
            browser: MediaBrowser,
            parentId: String,
            itemCount: Int,
            params: MediaLibraryService.LibraryParams?
        ) {
            getItem(parentId)
        }

    }
    override fun initializeController() {
        connectBrowserService(browserCallback)
    }

    private val _albumDetail = mutableStateOf<List<MediaData>>(emptyList())
    val albumDetail: State<List<MediaData>> get() = _albumDetail

    fun getItem(parentId:String){
        itemTree.getChildItem(parentId).map {
            MediaData(
                it,
                it.metadata?.mediaId == browser?.currentMediaItem?.metadata?.mediaId
            )
        }.apply {
            _albumDetail.value = this
        }
    }

    fun updateItem(item: MediaItem?){
        _albumDetail.value = _albumDetail.value.toMutableList().map {
            it.copy(
                isPlaying = if(item == null) false else it.mediaId == item.metadata?.mediaId
            )
        }
    }
}