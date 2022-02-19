package com.hua.abstractmusic.ui.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.hua.abstractmusic.base.viewmodel.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.Constant.ALBUM_ID
import com.hua.abstractmusic.other.Constant.ALL_ID
import com.hua.abstractmusic.other.Constant.ARTIST_ID
import com.hua.abstractmusic.other.Constant.LASTMEDIA
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import com.tencent.mmkv.MMKV
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : viewmodel
 */
@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
class HomeViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    mediaItemTree: MediaItemTree
) : BaseBrowserViewModel(application, useCase, mediaItemTree) {

    //当前播放列表
    private val _currentPlayList = mutableStateOf<List<MediaData>>(emptyList())
    val currentPlayList: State<List<MediaData>> get() = _currentPlayList

    private val _localMusicList = mutableStateOf(emptyList<MediaData>())
    val localMusicList: State<List<MediaData>> get() = _localMusicList

    private val _localAlbumList = mutableStateOf(emptyList<MediaData>())
    val localAlbumList: State<List<MediaData>> get() = _localAlbumList

    private val _localArtistList = mutableStateOf(emptyList<MediaData>())
    val localArtistList: State<List<MediaData>> get() = _localArtistList

    val mmkv = MMKV.mmkvWithID(LASTMEDIA)
    val currentPager: PagerState

    init {
        localListMap[ALL_ID] = _localMusicList
        localListMap[ALBUM_ID] = _localAlbumList
        localListMap[ARTIST_ID] = _localArtistList

//        playListMap[CURRENT_PLAY_LIST] = _currentPlayList
        playListMap[ALL_ID] = _localMusicList
        currentPager = PagerState(mmkv.decodeInt(Constant.LASTMEDIAINDEX, 0))
    }

    private val _playerState = MutableStateFlow(false)
    val playerState = _playerState.asStateFlow()
    val maxValue = mutableStateOf(0F)
    val currentPosition = mutableStateOf(0L)


    //当前播放的item，用户更新控制栏
    private val _currentItem = mutableStateOf(
        MediaItem.Builder()
            .setMediaMetadata(MediaMetadata.EMPTY)
            .build()
    )
    val currentItem: State<MediaItem> get() = _currentItem


//    //播放状态
//    private val _playerState = mutableStateOf(false)
//    val playerState get() = this._playerState

    val actionSeekBar = mutableStateOf(false)


    private val currentDuration = viewModelScope.launch {
        _playerState.collect {
            if (it) {
                while (true) {
                    delay(1000L)
                    if (!actionSeekBar.value) {
                        currentPosition.value = browser!!.currentPosition
                    }
                }
            }
        }
    }

    override fun onMediaConnected() {
        refresh()
        currentPosition.value = browser?.currentPosition ?: 0L
        currentDuration.start()
        _playerState.value = browser?.isPlaying == true
    }

    override fun onMediaDisConnected(controller: MediaController) {
        currentDuration.cancel()
    }

    override fun onMediaPlayerStateChanged(isPlaying: Boolean) {
        _playerState.value = isPlaying
    }

    //更新item的方法，当回调到item改变就调用这个方法
    override fun updateItem(item: MediaItem?) {
        val browser = browser ?: return
        updateCurrentPlayList()
        super.updateItem(item)
        _currentItem.value = item ?: NULL_MEDIA_ITEM
    }

    fun updateCurrentPlayList() {
        val browser = browser ?: return
        val list = mutableListOf<MediaData>()
        for (i in 0 until browser.mediaItemCount) {
            val item = browser.getMediaItemAt(i)
            list.add(
                MediaData(
                    item,
                    browser.currentMediaItem?.mediaId == item.mediaId
                )
            )
        }
        _currentPlayList.value = list
    }

    override fun onMediaPlayBackStateChange(playerState: Int) {
        val browser = browser ?: return
        when (playerState) {
            Player.STATE_READY -> {
                maxValue.value = (browser.duration).toFloat()
            }
        }
    }
}