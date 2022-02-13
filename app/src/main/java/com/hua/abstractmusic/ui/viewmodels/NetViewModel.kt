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
    itemTree: MediaItemTree
) : BaseBrowserViewModel(application, useCase,itemTree) {

    override fun onMediaConnected(
        controller: MediaController,
        allowedCommands: SessionCommandGroup
    ) {
        init(NETWORK_BANNER_ID)
    }

    override fun onMediaChildrenInit(parentId: String, items: List<MediaData>) {
        when (parentId) {
            NETWORK_BANNER_ID -> _bannerList.value = items
        }
    }

    private val _bannerList = mutableStateOf<List<MediaData>>(emptyList())
    val bannerList: State<List<MediaData>> get() = _bannerList
}