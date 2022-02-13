package com.hua.abstractmusic.ui.viewmodels

import android.app.Application
import androidx.compose.material.ExperimentalMaterialApi
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
import com.hua.abstractmusic.other.Constant.NETWORK_ALBUM_ID
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
) : BaseBrowserViewModel(application, useCase,mediaItemTree) {

    private val playSate = MutableStateFlow(SessionPlayer.PLAYER_STATE_IDLE)
    val maxValue = mutableStateOf(0F)
    val currentPosition = mutableStateOf(0L)

    //当前播放列表
    private val _currentPlayList = mutableStateOf<List<MediaData>>(emptyList())
    val currentPlayList: State<List<MediaData>> get() = _currentPlayList


    //网络请求的数据，到时候会清理掉，仅仅只是测试使用
    private val _netAlbum = mutableStateOf<List<MediaData>>(emptyList())
    val netAlbum: State<List<MediaData>> get() = _netAlbum

    private val _localMusicList = mutableStateOf(emptyList<MediaData>())
    val localMusicList: State<List<MediaData>> get() = _localMusicList

    private val _localAlbumList = mutableStateOf(emptyList<MediaData>())
    val localAlbumList: State<List<MediaData>> get() = _localAlbumList

    private val _localArtistList = mutableStateOf(emptyList<MediaData>())
    val localArtistList: State<List<MediaData>> get() = _localArtistList

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
//        updateItem(browser?.currentMediaItem)
        currentPosition.value = browser?.currentPosition ?: 0L
        currentDuration.start()
        updatePlayState(browser?.playerState ?: SessionPlayer.PLAYER_STATE_IDLE)
    }

    override fun onMediaDisConnected(controller: MediaController) {
        currentDuration.cancel()
    }

    override fun onMediaPlaylistChanged(
        controller: MediaController,
        list: MutableList<MediaItem>?,
        metadata: MediaMetadata?
    ) {
        updatePlayList(list)
    }

    override fun onMediaPlayerStateChanged(controller: MediaController, state: Int) {
        updatePlayState(state)
    }

    fun refresh() {
        init(ALL_ID)
        init(ALBUM_ID)
        init(ARTIST_ID)
    }

    //根据parentId去拿数据
    override fun onMediaChildrenInit(parentId: String, items: List<MediaData>) {
        when (parentId) {
            ALL_ID -> _localMusicList.value = items
            ALBUM_ID -> _localAlbumList.value = items
            ARTIST_ID -> _localArtistList.value = items
        }
    }

    //更新item的方法，当回调到item改变就调用这个方法
    override fun onMediaCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
        updateItem(item)
    }

    private fun updateItem(item: MediaItem?) {
        browser ?: return
        _netAlbum.value = _netAlbum.value.toMutableList().map {
            val isPlaying = if (item == null) false else it.mediaId == item.metadata?.mediaId
            it.copy(isPlaying = isPlaying)
        }
        _currentItem.value = item ?: NULL_MEDIA_ITEM

        _currentPlayList.value = browser!!.playlist?.map {
            val isPlaying =
                if (item == null) false else it.metadata?.mediaId == item.metadata?.mediaId
            MediaData(it, isPlaying)
        } ?: emptyList()

        _localMusicList.value = _localMusicList.value.toMutableList().map {
            val isPlaying = if (item == null) false else it.mediaId == item.metadata?.mediaId
            it.copy(isPlaying = isPlaying)
        }
        maxValue.value = browser?.currentMediaItem?.metadata?.duration?.toFloat() ?: 0F
    }

    //下一首
    fun prevItem() {
        val browser = browser ?: return
        browser.skipToPreviousPlaylistItem()
    }

    //下一首
    fun nextItem() {
        val browser = browser ?: return
        browser.skipToNextPlaylistItem()
    }

    //播放还是暂停？
    fun playOrPause() {
        val browser = browser ?: return
        if (browser.playerState == SessionPlayer.PLAYER_STATE_PLAYING) {
            browser.pause()
        } else {
            browser.play()
        }
    }

    //更新播放列表的方法
    private fun updatePlayList(mediaItems: List<MediaItem>?) {
        mediaItems ?: return
        val browser = browser ?: return
        _currentPlayList.value = mediaItems.map {
            val isPlaying = it.metadata?.mediaId == browser.currentMediaItem?.metadata?.mediaId
            MediaData(it, isPlaying)
        }
    }

    fun seekTo(position: Long) {
        val browser = browser ?: return
        browser.seekTo(position)
    }

    fun removePlayItem(position: Int) {
        val browser = browser ?: return
        browser.removePlaylistItem(position)
    }

    fun skipTo(position: Int) {
        val browser = browser ?: return
        if (browser.currentMediaItemIndex != position) {
            browser.skipToPlaylistItem(position)
            browser.play()
        }
    }

    fun clearPlayList() {
        val result = browser?.sendCustomCommand(
            SessionCommand(CLEAR_PLAY_LIST, null), null
        )
        if (result!!.get().resultCode == SessionResult.RESULT_SUCCESS) {
            updateItem(null)
        }
    }
}