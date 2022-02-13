package com.hua.abstractmusic.ui.viewmodels

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.media2.common.MediaItem
import androidx.media2.session.MediaBrowser
import androidx.media2.session.MediaController
import androidx.media2.session.MediaLibraryService
import androidx.media2.session.SessionCommandGroup
import com.hua.abstractmusic.base.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.Constant.NETWORK_BANNER_ID
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/02/12
 * @Desc   :
 */
@HiltViewModel
class NetViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    private val itemTree: MediaItemTree
) : BaseBrowserViewModel(application, useCase) {

    private val browserCallback = object : MediaBrowser.BrowserCallback() {
        override fun onConnected(
            controller: MediaController,
            allowedCommands: SessionCommandGroup
        ) {
            init(NETWORK_BANNER_ID)
        }

        override fun onChildrenChanged(
            browser: MediaBrowser,
            parentId: String,
            itemCount: Int,
            params: MediaLibraryService.LibraryParams?
        ) {
            childrenInit(parentId)
            _state.value = true
        }
    }

    private fun childrenInit(parentId: String) {
        itemTree.getChildItem(parentId).map {
            MediaData(
                it,
                it.metadata?.mediaId == browser?.currentMediaItem?.metadata?.mediaId
            )
        }.apply {
            when (parentId) {
                NETWORK_BANNER_ID -> _bannerList.value = this
            }
        }
    }

    private val _bannerList = mutableStateOf<List<MediaData>>(emptyList())
    val bannerList: State<List<MediaData>> get() = _bannerList

    override fun initializeController() {
        connectBrowserService(browserCallback)
    }
}