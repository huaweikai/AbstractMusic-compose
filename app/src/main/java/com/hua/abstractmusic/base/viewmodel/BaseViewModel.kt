package com.hua.abstractmusic.base.viewmodel

import android.annotation.SuppressLint
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.google.common.util.concurrent.MoreExecutors
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.services.BrowserListener
import com.hua.abstractmusic.services.MediaConnect
import com.hua.abstractmusic.ui.utils.LCE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * @author : huaweikai
 * @Date   : 2022/03/25
 * @Desc   :
 */
@SuppressLint("UnsafeOptInUsageError")
abstract class BaseViewModel(
    val mediaConnect: MediaConnect
) : ViewModel() {
    val localListMap = HashMap<String, MutableState<List<MediaData>>>()
    val playListMap = HashMap<String, MutableState<List<MediaData>>>()

    private var listener: Player.Listener? = null
    private var browserListener :BrowserListener ?= null

    val _screenState = MutableStateFlow<LCE>(LCE.Loading)
    val screenState get() = _screenState.asStateFlow()


    fun addListener(listener:Player.Listener){
        this.listener = listener
        mediaConnect.addListener(listener)
    }

    fun addBrowserListener(browserListener:BrowserListener){
        this.browserListener = browserListener
        mediaConnect.addBrowserListener(browserListener)
    }

    val browser get() = mediaConnect.browser



    open fun updateItem(item: MediaItem?) {
        playListMap.keys.forEach { key ->
            playListMap[key]!!.value = playListMap[key]!!.value.toMutableList().map {
                val isPlaying = if (item == null) false else it.mediaId == item.mediaId
                it.copy(isPlaying = isPlaying)
            }
        }
    }

    open fun refresh() {
        localListMap.keys.forEach { parentId ->
            getChildren(parentId)
        }
    }
    fun getChildren(parentId:String){
        val browser = mediaConnect.browser ?: return
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

    fun removeListener(){
        mediaConnect.removeBrowserListener(browserListener)
        mediaConnect.removeListener(listener)
    }

    fun setPlayList(startIndex: Int, mediaItems: List<MediaItem>, autoPlay: Boolean = true) {
        val browser = mediaConnect.browser ?: return
        browser.setMediaItems(mediaItems, startIndex, 0L)
        browser.prepare()
        if (autoPlay) {
            browser.play()
        }
    }

    //上一首
    fun prevItem() {
        val browser = mediaConnect.browser ?: return
        browser.seekToPreviousMediaItem()
    }

    //下一首
    fun nextItem() {
        val browser = mediaConnect.browser ?: return
        browser.seekToNextMediaItem()
    }

    //
    fun playOrPause() {
        val browser = mediaConnect.browser ?: return
        if (browser.isPlaying) {
            browser.pause()
        } else {
            browser.play()
        }
    }

    fun skipTo(position: Int, autoPlay: Boolean = false) {
        val browser = mediaConnect.browser ?: return
        if (browser.currentMediaItemIndex != position) {
            browser.seekTo(position, 0L)
            browser.prepare()
            if (autoPlay) browser.play()
        }
    }

    fun clearPlayList() {
        val browser = mediaConnect.browser ?: return
        browser.clearMediaItems()
    }

    fun addQueue(item: MediaItem, nextPlay: Boolean = false): Int {
        val browser = mediaConnect.browser ?: return 0
        val index = if (nextPlay) browser.currentMediaItemIndex + 1 else browser.mediaItemCount
        browser.addMediaItem(index, item)
        // 没有相关回调，直接手动更新
        mediaConnect.updatePlayList()
        return index
    }

}