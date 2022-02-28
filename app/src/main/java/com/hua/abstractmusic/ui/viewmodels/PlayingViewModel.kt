package com.hua.abstractmusic.ui.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.hua.abstractmusic.base.viewmodel.BaseBrowserViewModel
import com.hua.abstractmusic.bean.LyricsEntry
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.use_case.events.MusicInsertError
import com.hua.abstractmusic.utils.LyricsUtils
import com.hua.abstractmusic.utils.isLocal
import com.hua.taglib.TaglibLibrary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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

    val localSheetList = mutableStateOf<List<MediaData>>(emptyList())

    init {
        localListMap[Constant.SHEET_ID] = localSheetList
    }


    val lyricsCanScroll = mutableStateOf(false)
    val lyricsState = LazyListState(firstVisibleItemScrollOffset = -500)
    private val _lyricsLoadState = MutableStateFlow<LCE>(LCE.Loading)
    val lyricsLoadState: StateFlow<LCE> get() = _lyricsLoadState.asStateFlow()

    private val _currentPlayList = mutableStateOf<List<MediaData>>(emptyList())
    val currentPlayList: State<List<MediaData>> get() = _currentPlayList

    val actionSeekBar = mutableStateOf(false)
    private val _playerState = MutableStateFlow(false)
    val playerState = _playerState.asStateFlow()
    val maxValue = mutableStateOf(0F)

    val currentPosition = mutableStateOf(0F)


    private var currentDuration: Job? = null

    override fun updateItem(item: MediaItem?) {
        super.updateItem(item)
        val browser = this.browser ?: return
        _currentPlayItem.value = item ?: NULL_MEDIA_ITEM
        viewModelScope.launch {
            getLyrics(browser.currentMediaItem)
            if (lyricsCanScroll.value) {
                setLyricsItem(getStartIndex(browser.currentPosition))
            }
        }
        currentPosition.value = browser.currentPosition.toFloat()
        updateCurrentPlayList()
    }

    override fun onMediaConnected() {
        val browser = this.browser ?: return
        maxValue.value = (browser.duration).toFloat() ?: 0F
        _playerState.value = browser.isPlaying == true
        doSomething()
        refresh()
    }

    private fun doSomething() {
        currentDuration?.cancel()
        currentDuration = viewModelScope.launch {
            _playerState.collectLatest {
                if (it && browser?.isConnected == true) {
                    while (true) {
                        delay(1000L)
                        if (!actionSeekBar.value) {
                            currentPosition.value = browser?.currentPosition?.toFloat() ?: 0F
                        }
                    }
                }
            }
        }
    }

    override fun onMediaDisConnected(controller: MediaController) {
        currentDuration?.cancel()
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

    suspend fun getLyrics(item: MediaItem?) {
        item ?: return
        _lyricsLoadState.value = LCE.Loading
        withContext(Dispatchers.IO) {
            val lyrics = if (item.mediaId.isLocal()) {
                getLocalLyrics(item.mediaMetadata.mediaUri)
            } else {
                getNetLyrics(Uri.parse(item.mediaId).lastPathSegment ?: "")
            }
            if (lyrics.isBlank()) {
                _lyricsList.value = emptyList()
                _lyricsLoadState.value = LCE.Error
            } else {
                stringToLyrics(lyrics)
                _lyricsLoadState.value = LCE.Success
            }
        }
    }

    private fun stringToLyrics(lyrics: String?) {
        if (lyrics == null || lyrics.isBlank()) return
        val lyricResult = LyricsUtils.stringToLyrics(lyrics)
        lyricsCanScroll.value = lyricResult.first
        _lyricsList.value = lyricResult.second
    }

    private fun getLocalLyrics(uri: Uri?): String {
        return if (taglibLibrary.isAvailable) {
            try {
                getApplication<Application>().contentResolver.openFileDescriptor(
                    uri!!,
                    "r"
                )?.use {
                    taglibLibrary.getLyricsByTaglib(it.detachFd())
                } ?: ""
            } catch (e: Exception) {
                ""
            }
        } else {
            ""
        }
    }

    private suspend fun getNetLyrics(id: String): String {
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
                    i == browser.currentMediaItemIndex
                )
            )
        }
        _currentPlayList.value = list
    }

    fun seekTo(position: Long) {
        val browser = browser ?: return
        browser.seekTo(position)
        currentPosition.value = position.toFloat()
        if (lyricsCanScroll.value) {
            setLyricsItem(getStartIndex(position))
        }
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

    fun setLyricsItem(startIndex: Int, shouldScroll: Boolean = false) {
        if (lyricList.value.isNotEmpty()) {
            val index = if (startIndex == -1) {
                0
            } else {
                startIndex
            }
            _lyricsList.value = lyricList.value.toMutableList().map {
                it.copy(
                    isPlaying = it.time == lyricList.value[index].time
                )
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

    fun addQueue(item: MediaItem, nextPlay: Boolean = false) {
        val browser = this.browser ?: return
        if (nextPlay) {
            browser.addMediaItem(browser.currentMediaItemIndex + 1, item)
        } else {
            browser.addMediaItem(item)
        }
        // 没有相关回调，直接手动更新
        updateCurrentPlayList()
    }

    suspend fun insertMusicToSheet(mediaItem: MediaItem, parentId: String) {
        val sheetId = Uri.parse(parentId).lastPathSegment!!.toInt()
        try {
            useCase.insertSheetCase(mediaItem, sheetId)
        } catch (e: MusicInsertError) {
            withContext(Dispatchers.Main) {
                Toast.makeText(getApplication(), "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun removePlayItem(position: Int) {
        val browser = browser ?: return
        browser.removeMediaItem(position)
        updateCurrentPlayList()
    }
}