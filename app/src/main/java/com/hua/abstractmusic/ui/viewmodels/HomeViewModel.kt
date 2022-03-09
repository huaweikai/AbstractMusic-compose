package com.hua.abstractmusic.ui.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.hua.abstractmusic.base.viewmodel.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.LOCAL_ALBUM_ID
import com.hua.abstractmusic.other.Constant.LOCAL_ALL_ID
import com.hua.abstractmusic.other.Constant.LOCAL_ARTIST_ID
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : viewmodel
 */
@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    mediaItemTree: MediaItemTree
) : BaseBrowserViewModel(application, useCase, mediaItemTree) {

    private val _localMusicList = mutableStateOf(emptyList<MediaData>())
    val localMusicList: State<List<MediaData>> get() = _localMusicList

    private val _localAlbumList = mutableStateOf(emptyList<MediaData>())
    val localAlbumList: State<List<MediaData>> get() = _localAlbumList

    private val _localArtistList = mutableStateOf(emptyList<MediaData>())
    val localArtistList: State<List<MediaData>> get() = _localArtistList

    init {
        localListMap[LOCAL_ALL_ID] = _localMusicList
        localListMap[LOCAL_ALBUM_ID] = _localAlbumList
        localListMap[LOCAL_ARTIST_ID] = _localArtistList

        playListMap[LOCAL_ALL_ID] = _localMusicList
    }

    override fun onMediaConnected() {
        refresh()
    }

//    fun onGetItem(mediaId: String): MediaItem {
//        return itemTree.getItem(mediaId) ?: NULL_MEDIA_ITEM
//    }
}