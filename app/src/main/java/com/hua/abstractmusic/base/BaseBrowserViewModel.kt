package com.hua.abstractmusic.base

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import androidx.media2.session.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.common.util.concurrent.MoreExecutors
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.Constant.CURRENT_PLAY_LIST
import com.hua.abstractmusic.other.Constant.LASTMEDIA
import com.hua.abstractmusic.other.Constant.LASTMEDIAINDEX
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.services.PlayerService
import com.hua.abstractmusic.use_case.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2021/11/26
 * @Desc   : 支持browser的viewmodel基类
 */

abstract class BaseBrowserViewModel(
    application: Application,
    val useCase: UseCase,
    val itemTree: MediaItemTree
) : AndroidViewModel(application) {

    val listMap = HashMap<String, MutableState<List<MediaData>>>()
    val playListMap = HashMap<String, MutableState<List<MediaData>>>()

    private val browserCallback = object : MediaBrowser.BrowserCallback() {
        override fun onConnected(
            controller: MediaController,
            allowedCommands: SessionCommandGroup
        ) {
            onMediaConnected(controller, allowedCommands)
            onCurrentMediaItemChanged(controller, browser?.currentMediaItem)
        }

        override fun onChildrenChanged(
            browser: MediaBrowser,
            parentId: String,
            itemCount: Int,
            params: MediaLibraryService.LibraryParams?
        ) {
            val mediaItems = itemTree.getChildItem(parentId).map {
                MediaData(
                    it,
                    it.metadata?.mediaId == browser.currentMediaItem?.metadata?.mediaId
                )
            }
            //根据parentId去拿数据
            listMap[parentId]!!.value = mediaItems
            _state.value = true
        }

        //更新播放列表的方法
        override fun onPlaylistChanged(
            controller: MediaController,
            list: MutableList<MediaItem>?,
            metadata: MediaMetadata?
        ) {
            val items = list ?: return
            if (items.size == 0) {
                updateItem(null)
            }
            playListMap[CURRENT_PLAY_LIST]?.let {
                it.value = list.map {
                    val isPlaying =
                        it.metadata?.mediaId == browser?.currentMediaItem?.metadata?.mediaId
                    MediaData(it, isPlaying)
                }
            }
        }

        override fun onDisconnected(controller: MediaController) {
            onMediaDisConnected(controller)
        }

        override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
            viewModelScope.launch {
                delay(200L)
                updateItem(browser?.currentMediaItem)
            }
        }

        override fun onPlayerStateChanged(controller: MediaController, state: Int) {
            onMediaPlayerStateChanged(controller, state)
        }
    }

    init {
        connectBrowserService(browserCallback)
    }

    open fun onMediaConnected(controller: MediaController, allowedCommands: SessionCommandGroup) {}

    open fun onMediaDisConnected(controller: MediaController) {}
    open fun onMediaPlayerStateChanged(controller: MediaController, state: Int) {}


    var browser: MediaBrowser? = null
        private set


    protected val _state = mutableStateOf(false)
    val state: State<Boolean> get() = _state

    private fun connectBrowserService(browserCallback: MediaBrowser.BrowserCallback) {
        val context = getApplication<Application>().applicationContext
        browser = MediaBrowser.Builder(context)
            .setSessionToken(
                SessionToken(
                    context,
                    ComponentName(context, PlayerService::class.java)
                )
            )
            .setControllerCallback(Dispatchers.Default.asExecutor(), browserCallback)
            .build()
    }

    fun releaseBrowser() {
        browser?.close()
    }

    fun setPlaylist(startIndex: Int, items: List<MediaData>, autoPlay: Boolean = true) {
        val browser = this.browser ?: return
        val mediaIds = items.map {
            it.mediaItem.metadata?.mediaId ?: ""
        }
        browser.setPlaylist(mediaIds, null).addListener({
            browser.skipToPlaylistItem(startIndex)
            if (autoPlay) {
                browser.play()
            } else {
                browser.prepare()
            }
        }, MoreExecutors.directExecutor())
        viewModelScope.launch(Dispatchers.IO) {
            useCase.clearCurrentListCase()
            useCase.insertMusicToCurrentItemCase(items)
        }
    }

    open fun detailInit(parentId: String) {
        viewModelScope.launch {
            _state.value = false
            delay(1000L)
            init(parentId)
        }
    }

    //加载音乐列表，根据父ID来进行加载
    open fun init(parentId: String) {
        _state.value = false
        val browser = browser ?: return
        //订阅
        browser.subscribe(parentId, null)
        //去获取数据
        browser.getChildren(parentId, 0, Int.MAX_VALUE, null)
    }

    open fun updateItem(item: MediaItem?) {
        playListMap.keys.forEach { key ->
            playListMap[key]!!.value = playListMap[key]!!.value.toMutableList().map {
                val isPlaying = if (item == null) false else it.mediaId == item.metadata?.mediaId
                it.copy(isPlaying = isPlaying)
            }
        }
    }

    //上一首
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
        browser?.sendCustomCommand(SessionCommand(Constant.CLEAR_PLAY_LIST, null), null)
    }
}