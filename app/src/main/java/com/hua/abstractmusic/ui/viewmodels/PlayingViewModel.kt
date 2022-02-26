package com.hua.abstractmusic.ui.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.hua.abstractmusic.base.viewmodel.BaseBrowserViewModel
import com.hua.abstractmusic.bean.LyricsEntry
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.utils.LyricsUtils
import com.hua.abstractmusic.utils.isLocal
import com.hua.taglib.TaglibLibrary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/02/24
 * @Desc   :
 */
@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
class PlayingViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    itemTree: MediaItemTree,
    private val repository: NetRepository,
    private val taglibLibrary: TaglibLibrary
) : BaseBrowserViewModel(application, useCase, itemTree) {

    private val _currentPlayItem = mutableStateOf(NULL_MEDIA_ITEM)
    val currentPlayItem: State<MediaItem> get() = _currentPlayItem

    private val _lyricsList = mutableStateOf<List<LyricsEntry>>(emptyList())
    val lyricList: State<List<LyricsEntry>> get() = _lyricsList


    val lyricsCanScroll = mutableStateOf(false)


    private val _currentPlayList = mutableStateOf<List<MediaData>>(emptyList())
    val currentPlayList: State<List<MediaData>> get() = _currentPlayList

    val actionSeekBar = mutableStateOf(false)
    private val _playerState = MutableStateFlow(false)
    val playerState = _playerState.asStateFlow()
    val maxValue = mutableStateOf(0F)
    val currentPosition = mutableStateOf(0L)


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

    override fun updateItem(item: MediaItem?) {
        super.updateItem(item)
        updateCurrentPlayList()
        _currentPlayItem.value = item ?: NULL_MEDIA_ITEM
        currentPosition.value = 0
        getLyrics(item)
    }

    override fun onMediaConnected() {
        maxValue.value = (browser?.duration)?.toFloat() ?: 0F
        if (browser?.currentPosition != null && browser?.currentPosition!! >= 0) {
            currentPosition.value = browser?.currentPosition!!
            setLyricsItem(getStartIndex(browser?.currentPosition!!))
        } else {
            currentPosition.value = 0L
        }
        _playerState.value = browser?.isPlaying == true
        currentDuration.start()
    }

    override fun onMediaDisConnected(controller: MediaController) {
        currentDuration.cancel()
    }

    override fun onMediaPlayerStateChanged(isPlaying: Boolean) {
        _playerState.value = isPlaying
    }

    override fun onMediaPlayBackStateChange(playerState: Int) {
        val browser = browser ?: return
        when (playerState) {
            Player.STATE_READY -> {
                maxValue.value = (browser.duration).toFloat()
            }
        }
    }

    private fun getLyrics(item: MediaItem?) {
        item ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val lyrics = if (item.mediaId.isLocal()) {
                getLocalLyrics(item.mediaMetadata.mediaUri)
            } else {
                getNetLyrics(Uri.parse(item.mediaId).lastPathSegment ?: "")
            }
            stringToLyrics(lyrics)
        }
    }

    private fun stringToLyrics(lyrics: String?) {
        if (lyrics == null || lyrics.isBlank()) return
        val lyricResult = LyricsUtils.stringToLyrics(lyrics)
        lyricsCanScroll.value = lyricResult.first
        _lyricsList.value = lyricResult.second
    }

    private fun getLocalLyrics(uri: Uri?): String? {
        return if (taglibLibrary.isAvailable) {
            try {
                getApplication<Application>().contentResolver.openFileDescriptor(
                    uri!!,
                    "r"
                )?.use {
                    taglibLibrary.getLyricsByTaglib(it.detachFd())
                }
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        }
    }

    private suspend fun getNetLyrics(id: String): String? {
        return repository.selectLyrics(id)
    }


    private fun updateCurrentPlayList() {
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

    fun seekTo(position: Long) {
        val browser = browser ?: return
        browser.seekTo(position)
        currentPosition.value = position
        setLyricsItem(getStartIndex(position))
    }

    fun getNextIndex(position: Long): Int {
        val startIndex = getStartIndex(position)
        return when {
            startIndex >= lyricList.value.size -> {
                lyricList.value.size - 1
            }
            else -> {
                startIndex + 1
            }
        }
    }

    fun getStartIndex(position: Long): Int {
        return if (lyricList.value.isNotEmpty()) {
            val item = lyricList.value.findLast {
                it.time!! <= position
            }
            if (item == null) {
                -1
            } else {
                lyricList.value.indexOf(item)
            }
        } else {
            0
        }
    }

    fun setLyricsItem(startIndex: Int) {
        if (lyricList.value.isNotEmpty()) {
            if (startIndex == -1) {
                _lyricsList.value = lyricList.value.toMutableList().map {
                    it.copy(
                        isPlaying = it.time == lyricList.value[0].time
                    )
                }
            } else {
                _lyricsList.value = lyricList.value.toMutableList().map {
                    it.copy(
                        isPlaying = it.time == lyricList.value[startIndex].time
                    )
                }
            }
        }
    }

    fun getStartToNext(nextIndex: Int, start: Long): Long {
        return if (nextIndex >= lyricList.value.size) {
            Long.MAX_VALUE
        } else {
            lyricList.value[nextIndex].time!! - start
        }

    }

    fun getMusicDuration(): Long {
        val browser = this.browser ?: return 0L
        return if (browser.currentPosition < 0) {
            0L
        } else {
            browser.currentPosition
        }
    }
}