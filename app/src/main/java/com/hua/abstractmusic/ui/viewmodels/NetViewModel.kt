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
import com.hua.abstractmusic.other.Constant.NETWORK_ALBUM_ID
import com.hua.abstractmusic.other.Constant.NETWORK_BANNER_ID
import com.hua.abstractmusic.other.Constant.NETWORK_RECOMMEND_ID
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
) : BaseBrowserViewModel(application, useCase, itemTree) {

    private val _bannerList = mutableStateOf<List<MediaData>>(emptyList())
    val bannerList: State<List<MediaData>> get() = _bannerList

    private val _recommendList = mutableStateOf<List<MediaData>>(emptyList())
    val recommendList: State<List<MediaData>> get() = _recommendList

    private val _albumList = mutableStateOf<List<MediaData>>(emptyList())
    val albumList: State<List<MediaData>> get() = _albumList

    init {
        listMap[NETWORK_BANNER_ID] = _bannerList
        listMap[NETWORK_ALBUM_ID] = _albumList
        listMap[NETWORK_RECOMMEND_ID] = _recommendList
    }

    override fun onMediaConnected(
        controller: MediaController,
        allowedCommands: SessionCommandGroup
    ) {
        listMap.keys.forEach {
            init(it)
        }
    }
}