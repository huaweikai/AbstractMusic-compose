package com.hua.abstractmusic.base.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.services.PlayerService
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.abstractmusic.use_case.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2021/11/26
 * @Desc   : 支持browser的viewmodel基类
 */
@SuppressLint("UnsafeOptInUsageError")
abstract class BaseBrowserViewModel(
    application: Application,
    val useCase: UseCase,
    val itemTree: MediaItemTree
) : AndroidViewModel(application) {

    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    val browser: MediaBrowser?
        get() = if (browserFuture.isDone) browserFuture.get() else null


    val localListMap = HashMap<String, MutableState<List<MediaData>>>()
    val netListMap = HashMap<String, MutableState<List<MediaData>>>()
    val playListMap = HashMap<String, MutableState<List<MediaData>>>()

    private val browserCallback = object : MediaBrowser.Listener {
        override fun onChildrenChanged(
            browser: MediaBrowser,
            parentId: String,
            itemCount: Int,
            params: MediaLibraryService.LibraryParams?
        ) {
            if (parentId in netListMap.keys) {
                when (itemCount) {
                    1 -> {
                        val mediaItems = itemTree.getChildItem(parentId).map {
                            MediaData(
                                it,
                                it.mediaId == browser.currentMediaItem?.mediaId
                            )
                        }
                        //根据parentId去拿数据
                        netListMap[parentId]!!.value = mediaItems
                        _screenState.value = LCE.Success
                    }
                    0 -> {
                        _screenState.value = LCE.Error
                    }
                }
            } else {
                onMediaChildrenChanged(browser, parentId, itemCount, params)
            }
        }

        override fun onDisconnected(controller: MediaController) {
            onMediaDisConnected(controller)
        }
    }

    private val playCallback = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            onMediaPlayBackStateChange(playbackState)
        }

        override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
            Log.d("TAG", "onPlaylistMetadataChanged: $mediaMetadata")
        }

//        override fun onIsPlayingChanged(isPlaying: Boolean) {
//            onMediaPlayerStateChanged(isPlaying)
//        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            onMediaPlayerStateChanged(playWhenReady)
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateItem(mediaItem)
        }

    }

//    init {
//        initializeController()
//    }


    private val _screenState = mutableStateOf<LCE>(LCE.Loading)
    val screenState: State<LCE> get() = _screenState


    fun initializeController() {
        browserFuture = MediaBrowser.Builder(
            getApplication(),
            SessionToken(
                getApplication(),
                ComponentName(getApplication(), PlayerService::class.java)
            )
        )
            .setListener(browserCallback)
            .buildAsync()
        browserFuture.addListener({ browserConnected() }, MoreExecutors.directExecutor())
    }

    open fun browserConnected() {
        val browser = this.browser ?: return
        updateItem(browser.currentMediaItem)
        browser.addListener(playCallback)
        onMediaConnected()
    }

    fun releaseBrowser() {
        MediaBrowser.releaseFuture(browserFuture)
    }


    fun setPlaylist(startIndex: Int, items: List<MediaData>, autoPlay: Boolean = true) {
        val mediaIds = items.map {
            it.mediaItem
        }
        setPlayList(startIndex, mediaIds, autoPlay)
    }

    fun setPlayList(startIndex: Int, mediaItems: List<MediaItem>, autoPlay: Boolean = true) {
        val browser = this.browser ?: return
        browser.setMediaItems(mediaItems, startIndex, 0L)
        browser.prepare()
        if (autoPlay) {
            browser.play()
        }
        viewModelScope.launch(Dispatchers.IO) {
            useCase.clearCurrentListCase()
            useCase.insertMusicToCurrentItemCase(mediaItems)
        }
    }

    //加载音乐列表，根据父ID来进行加载
    open fun init(parentId: String) {
        val browser = browser ?: return
        val childrenFeature = browser.getChildren(
            parentId, 0, Int.MAX_VALUE, null
        )
        childrenFeature.addListener({
            childrenFeature.get().value?.map {
                MediaData(
                    it,
                    it.mediaId == browser.currentMediaItem?.mediaId
                )
            }.apply {
                localListMap[parentId]!!.value = this ?: emptyList()
            }
        }, MoreExecutors.directExecutor())
    }

    open fun delayInit(parentId: String) {
        _screenState.value = LCE.Loading
        val browser = browser ?: return
        viewModelScope.launch {
            delay(1000)
            //订阅
            browser.subscribe(parentId, null)
            //去获取数据
            browser.getChildren(parentId, 0, Int.MAX_VALUE, null)
        }
    }

    open fun updateItem(item: MediaItem?) {
        playListMap.keys.forEach { key ->
            playListMap[key]!!.value = playListMap[key]!!.value.toMutableList().map {
                val isPlaying = if (item == null) false else it.mediaId == item.mediaId
                it.copy(isPlaying = isPlaying)
            }
        }
    }

    open fun refresh() {
        localListMap.keys.forEach {
            init(it)
        }
        netListMap.keys.forEach {
            delayInit(it)
        }
    }

    //上一首
    fun prevItem() {
        val browser = browser ?: return
        browser.seekToPreviousMediaItem()
    }

    //下一首
    fun nextItem() {
        val browser = browser ?: return
        browser.seekToNextMediaItem()
    }

    //
    fun playOrPause() {
        val browser = browser ?: return
        if (browser.isPlaying) {
            browser.pause()
        } else {
            browser.play()
        }
    }

    fun skipTo(position: Int, autoPlay: Boolean = false) {
        val browser = browser ?: return
        if (browser.currentMediaItemIndex != position) {
            browser.seekTo(position, 0L)
            browser.prepare()
            if (autoPlay) browser.play()
        }
    }

    fun clearPlayList() {
        val browser = browser ?: return
        browser.clearMediaItems()
    }


    open fun onMediaConnected() {}
    open fun onMediaChildrenChanged(
        browser: MediaBrowser,
        parentId: String,
        itemCount: Int,
        params: MediaLibraryService.LibraryParams?
    ) {
    }

    open fun onMediaDisConnected(controller: MediaController) {}
    open fun onMediaPlayerStateChanged(isPlaying: Boolean) {}

    open fun onMediaPlayBackStateChange(playerState: Int) {}



}