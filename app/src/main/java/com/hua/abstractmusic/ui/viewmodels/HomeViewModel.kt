package com.hua.abstractmusic.ui.viewmodels

import android.app.Application
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import androidx.media2.session.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.hua.abstractmusic.base.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.ALBUM_ID
import com.hua.abstractmusic.other.Constant.ALL_ID
import com.hua.abstractmusic.other.Constant.ARTIST_ID
import com.hua.abstractmusic.other.Constant.CLEAR_PLAY_LIST
import com.hua.abstractmusic.other.Constant.CURRENT_PLAY_LIST
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.utils.duration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : viewmodel
 */
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

    init {
        localListMap[ALL_ID] = _localMusicList
        localListMap[ALBUM_ID] = _localAlbumList
        localListMap[ARTIST_ID] = _localArtistList

        playListMap[CURRENT_PLAY_LIST] = _currentPlayList
        playListMap[ALL_ID] = _localMusicList
    }

    private val playSate = MutableStateFlow(SessionPlayer.PLAYER_STATE_IDLE)
    val maxValue = mutableStateOf(0F)
    val currentPosition = mutableStateOf(0L)


    //当前播放的item，用户更新控制栏
    private val _currentItem = mutableStateOf(NULL_MEDIA_ITEM)
    val currentItem: State<MediaItem> get() = _currentItem


    //播放状态
    private val _playerState = mutableStateOf(SessionPlayer.PLAYER_STATE_IDLE)
    val playerState get() = _playerState

    private fun updatePlayState(@SessionPlayer.PlayerState playState: Int) {
        playSate.value = playState
        _playerState.value = playState
    }

    val actionSeekBar = mutableStateOf(false)


    private val currentDuration = viewModelScope.launch {
        playSate.collect {
            if (it == SessionPlayer.PLAYER_STATE_PLAYING) {
                while (true) {
                    delay(1000L)
                    if (!actionSeekBar.value) {
                        currentPosition.value = browser!!.currentPosition
                    }
                }
            }
        }
    }

    override fun onMediaConnected(
        controller: MediaController,
        allowedCommands: SessionCommandGroup
    ) {
        refresh()
        currentPosition.value = browser?.currentPosition ?: 0L
        currentDuration.start()
        updatePlayState(browser?.playerState ?: SessionPlayer.PLAYER_STATE_IDLE)
    }

    override fun onMediaDisConnected(controller: MediaController) {
        currentDuration.cancel()
    }

    override fun onMediaPlayerStateChanged(controller: MediaController, state: Int) {
        updatePlayState(state)
    }

    //更新item的方法，当回调到item改变就调用这个方法
    override fun updateItem(item: MediaItem?) {
        super.updateItem(item)
        browser ?: return
        _currentItem.value = item ?: NULL_MEDIA_ITEM
        maxValue.value = browser?.currentMediaItem?.metadata?.duration?.toFloat() ?: 0F
    }
}