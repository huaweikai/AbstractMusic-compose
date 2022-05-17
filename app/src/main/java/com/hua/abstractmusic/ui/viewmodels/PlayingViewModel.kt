package com.hua.abstractmusic.ui.viewmodels

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.hua.abstractmusic.R
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.repository.LocalRepository
import com.hua.abstractmusic.repository.NetWorkRepository
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.abstractmusic.utils.ComposeUtils
import com.hua.abstractmusic.utils.LyricsUtils
import com.hua.abstractmusic.utils.PaletteUtils
import com.hua.abstractmusic.utils.isLocal
import com.hua.model.lyrics.LyricsDTO
import com.hua.model.music.MediaData
import com.hua.service.BrowserListener
import com.hua.service.MediaConnect
import com.hua.service.preference.PreferenceManager
import com.hua.service.usecase.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/03/25
 * @Desc   :
 */
@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
class PlayingViewModel @Inject constructor(
    mediaConnect: MediaConnect,
    private val netRepository: NetWorkRepository,
    private val useCase: UseCase,
    private val repository: LocalRepository,
    private val preferenceManager: PreferenceManager,
    private val composeUtils: ComposeUtils
) : BaseViewModel(mediaConnect) {

    //当前播放的item
    private val _currentPlayItem = mutableStateOf(Constant.NULL_MEDIA_ITEM)
    val currentPlayItem: State<MediaItem> get() = _currentPlayItem

    // 歌词
    private val _lyricsList = mutableStateOf<List<LyricsDTO>>(emptyList())
    val lyricList: State<List<LyricsDTO>> get() = _lyricsList

    val lyricsCanScroll = mutableStateOf(false)

    val currentPlayIndex = mutableStateOf(0)
    val currentPlayTotal = mutableStateOf(0)

    private val _lyricsLoadState = MutableStateFlow<LCE>(LCE.Loading)
    val lyricsLoadState: StateFlow<LCE> get() = _lyricsLoadState.asStateFlow()

    val shuffleUI = mutableStateOf(false)

    val repeatModeUI = mutableStateOf<Int>(Player.REPEAT_MODE_OFF)

    private val _currentPlayList = mutableStateOf<List<MediaData>>(emptyList())
    val currentPlayList: State<List<MediaData>> get() = _currentPlayList

    private val _playerState = MutableStateFlow(false)
    val playerState = _playerState.asStateFlow()
    val maxValue = mutableStateOf(0F)

    val currentPosition = mutableStateOf(0F)

    private var screenIsDark = false

    private val _hasNextOrPrev = mutableStateOf(hasNextOrPrev())
    val hasNextOrPrev :State<Pair<Boolean,Boolean>> get() = _hasNextOrPrev

    init {
        viewModelScope.launch {
            mediaConnect.isConnected.collectLatest {
                if (it) setController() else releaseListener()
            }
        }

    }


    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val browser = mediaConnect.browser ?: return
            when (playbackState) {
                Player.STATE_READY -> {
                    _playerState.value = browser.playWhenReady
                    currentPosition.value = browser.currentPosition.toFloat()
                    setLyricsItem(getStartIndex(browser.currentPosition))
                    maxValue.value = (browser.duration).toFloat()
                }
                Player.STATE_ENDED -> _playerState.value = false

                else -> {}
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            _playerState.value = playWhenReady
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateItem(mediaItem)
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            shuffleUI.value = shuffleModeEnabled
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            repeatModeUI.value = repeatMode
        }
    }

    private val browserListener = object : BrowserListener {
        override fun playListUpdate() {
            updateCurrentPlayList()
        }
    }

    fun playOrPause() {
        val browser = mediaConnect.browser ?: return
        if (browser.isPlaying) {
            browser.pause()
        } else {
            browser.play()
        }
        if (browser.playbackState == Player.STATE_ENDED) {
            browser.seekTo(_currentPlayList.value.size - 1, 0)
        }
    }

    private fun setController() {
        addListener(listener)
        addBrowserListener(browserListener)
        val browser = this.browser ?: return
        maxValue.value = (browser.duration).toFloat() ?: 0F
        _playerState.value = browser.isPlaying == true
        updateItem(browser.currentMediaItem)
        shuffleUI.value = browser.shuffleModeEnabled
        repeatModeUI.value = browser.repeatMode
        _hasNextOrPrev.value = hasNextOrPrev()
    }

    private var positionJob: Job? = null

    fun startUpdatePosition() {
        cancelUpdatePosition()
        positionJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                withContext(Dispatchers.Main) {
                    currentPosition.value = browser?.currentPosition?.toFloat() ?: 0F
                }
                delay(1000L)
            }
        }
    }

    fun cancelUpdatePosition() {
        positionJob?.cancel()
    }

    private var lyricsJob: Job? = null

    fun startUpdateLyrics() {
        cancelUpdateLyrics()
        lyricsJob = viewModelScope.launch(Dispatchers.Main) {
            while (lyricsCanScroll.value) {
                val startTime = getMusicDuration()
                val start = getStartIndex(startTime)
                setLyricsItem(start)
                val next = getNextIndex(startTime)
                withContext(Dispatchers.Default) {
                    delay(getStartToNext(next, startTime))
                }
            }
        }
    }

    fun cancelUpdateLyrics() {
        lyricsJob?.cancel()
    }


    override fun updateItem(item: MediaItem?) {
        val browser = this.browser ?: return
        _currentPlayItem.value = item ?: Constant.NULL_MEDIA_ITEM
        viewModelScope.launch {
            getLyrics(browser.currentMediaItem)
            if (lyricsCanScroll.value) {
                setLyricsItem(getStartIndex(browser.currentPosition))
            }
        }
        transformColor(item)
        _hasNextOrPrev.value = hasNextOrPrev()
        maxValue.value = browser.duration.toFloat()
        updateCurrentPlayList()
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

    private fun getStartToNext(nextIndex: Int, start: Long): Long {
        return if (nextIndex >= lyricList.value.size) {
            Long.MAX_VALUE
        } else {
            lyricList.value[nextIndex].time!! - start
        }
    }

    suspend fun getLyrics(item: MediaItem?) {
        item ?: return
        _lyricsLoadState.value = LCE.Loading
        withContext(Dispatchers.IO) {
            val lyrics = if (item.mediaId.isLocal()) {
                repository.selectLyrics(item.mediaMetadata.mediaUri)
            } else {
                netRepository.selectLyrics(Uri.parse(item.mediaId))
            }
            if (lyrics.isBlank()) {
                _lyricsList.value = emptyList()
                _lyricsLoadState.value = LCE.Error
                lyricsCanScroll.value = false
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

    private fun updateCurrentPlayList() {
        val browser = this.browser ?: return
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
        currentPlayIndex.value = browser.currentMediaItemIndex + 1
        currentPlayTotal.value = list.size
        viewModelScope.launch(Dispatchers.IO) {
            useCase.clearCurrentListCase()
            useCase.insertMusicToCurrentItemCase(list)
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

    fun setRepeatMode(): Int? {
        val browser = this.browser ?: return null
        when (browser.repeatMode) {
            Player.REPEAT_MODE_ALL -> browser.repeatMode = Player.REPEAT_MODE_OFF
            Player.REPEAT_MODE_OFF -> browser.repeatMode = Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> browser.repeatMode = Player.REPEAT_MODE_ALL
        }
        return browser.repeatMode
    }

    fun setShuffle(): Boolean {
        val browser = this.browser ?: return false
        browser.shuffleModeEnabled = !browser.shuffleModeEnabled
        return browser.shuffleModeEnabled
    }

    fun removePlayItem(position: Int) {
        val browser = this.browser ?: return
        browser.removeMediaItem(position)
        updateCurrentPlayList()
    }

    fun getLastMediaIndex(): Int {
        return preferenceManager.lastMediaIndex
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

    fun seekTo(position: Long) {
        val browser = this.browser ?: return
        browser.seekTo(position)
        startUpdateLyrics()
    }

    val itemColor = MutableStateFlow(Pair(Color.Black, Color.Black))

    fun putTransDark(isDark: Boolean) {
        screenIsDark = isDark
        transformColor(mediaConnect.browser?.currentMediaItem,isDark)
    }

    private fun transformColor(item: MediaItem?, isDark: Boolean = screenIsDark) {
        item ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val result = PaletteUtils.resolveBitmap(
                isDark,
                composeUtils.coilToBitmap(item.mediaMetadata.artworkUri),
                mediaConnect.context.getColor(R.color.black)
            )
            itemColor.value = Pair(Color(result.first), Color(result.second))
        }
    }

    private fun hasNextOrPrev():Pair<Boolean,Boolean>{
        val browser = mediaConnect.browser ?: return Pair(true,true)
        return Pair(
            browser.hasNextMediaItem(),browser.hasPreviousMediaItem()
        )
    }
}