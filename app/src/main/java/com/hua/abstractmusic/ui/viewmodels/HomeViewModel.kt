package com.hua.abstractmusic.ui.viewmodels

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.services.MediaConnect
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/03/25
 * @Desc   :
 */
@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
class HomeViewModel @Inject constructor(
    mediaConnect: MediaConnect
) : BaseViewModel(mediaConnect) {
    private val _localMusicList = mutableStateOf(emptyList<MediaData>())
    val localMusicList: State<List<MediaData>> get() = _localMusicList

    private val _localAlbumList = mutableStateOf(emptyList<MediaData>())
    val localAlbumList: State<List<MediaData>> get() = _localAlbumList

    private val _localArtistList = mutableStateOf(emptyList<MediaData>())
    val localArtistList: State<List<MediaData>> get() = _localArtistList


    private val listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateItem(mediaItem)
        }
    }

    init {
        localListMap[Constant.LOCAL_ALL_ID] = _localMusicList
        localListMap[Constant.LOCAL_ALBUM_ID] = _localAlbumList
        localListMap[Constant.LOCAL_ARTIST_ID] = _localArtistList

        playListMap[Constant.LOCAL_ALL_ID] = _localMusicList
        addListener(listener)
        refresh(false)
    }

    fun refresh(isAction: Boolean) {
        val browser = this.browser ?: return
        if (isAction) {
            refresh()
        } else {
            localListMap.keys.forEach {
                val list = mediaConnect.itemTree.getCacheItems(it)
                if (list.isNotEmpty()) {
                    localListMap[it]!!.value = list.map {
                        MediaData(
                            it,
                            isPlaying = browser.currentMediaItem?.mediaId == it.mediaId
                        )
                    }
                } else {
                    getChildren(it)
                }
            }
        }
    }
}