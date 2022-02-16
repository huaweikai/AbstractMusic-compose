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
import com.hua.abstractmusic.other.Constant.NETWORK_ALL_MUSIC_ID
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

    private val _musicList = mutableStateOf<List<MediaData>>(emptyList())
    val musicList: State<List<MediaData>> get() = _musicList

    init {
        listMap[NETWORK_BANNER_ID] = _bannerList
        listMap[NETWORK_ALBUM_ID] = _albumList
        listMap[NETWORK_RECOMMEND_ID] = _recommendList
        listMap[NETWORK_ALL_MUSIC_ID] = _musicList
    }

    var recommendId: String? = null
    var albumId: String? = null

    override fun onMediaChildrenChanged(
        browser: MediaBrowser,
        parentId: String,
        itemCount: Int,
        params: MediaLibraryService.LibraryParams?
    ) {
        if (itemCount != 0) {
            setPlayList(0, itemTree.getChildItem(parentId))
        }
    }

    fun listInit(parentId: String) {
        val browser = this.browser ?: return
        browser.subscribe(parentId, null)
        browser.getChildren(parentId, 0, Int.MAX_VALUE, null)
    }

    override fun onMediaConnected(
        controller: MediaController,
        allowedCommands: SessionCommandGroup
    ) {
        refresh()
    }
}