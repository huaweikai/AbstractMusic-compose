package com.hua.abstractmusic.ui.viewmodels

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_OFF
import com.hua.abstractmusic.R
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.bean.LyricsEntry
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.preference.PreferenceManager
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.repository.Repository
import com.hua.abstractmusic.services.BrowserListener
import com.hua.abstractmusic.services.MediaConnect
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.utils.ComposeUtils
import com.hua.abstractmusic.utils.LyricsUtils
import com.hua.abstractmusic.utils.PaletteUtils
import com.hua.abstractmusic.utils.isLocal
import com.hua.taglib.TaglibLibrary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val netRepository: NetRepository,
    private val useCase: UseCase,
    private val repository: Repository,
    private val taglibLibrary: TaglibLibrary,
    private val preferenceManager: PreferenceManager,
    private val composeUtils: ComposeUtils
) : BaseViewModel(mediaConnect) {
    //mediaConnect连接
    val isConnect get() =  mediaConnect.isConnected

    //当前播放的item
    private val _currentPlayItem = mutableStateOf(Constant.NULL_MEDIA_ITEM)
    val currentPlayItem: State<MediaItem> get() = _currentPlayItem

    // 歌词
    private val _lyricsList = mutableStateOf<List<LyricsEntry>>(emptyList())
    val lyricList: State<List<LyricsEntry>> get() = _lyricsList


    val lyricsCanScroll = mutableStateOf(false)
    val lyricsState = LazyListState(firstVisibleItemScrollOffset = -500)
    private val _lyricsLoadState = MutableStateFlow<LCE>(LCE.Loading)
    val lyricsLoadState: StateFlow<LCE> get() = _lyricsLoadState.asStateFlow()

    val shuffleUI = mutableStateOf(false)

    val repeatModeUI = mutableStateOf<Int>(REPEAT_MODE_OFF)

    private val _currentPlayList = mutableStateOf<List<MediaData>>(emptyList())
    val currentPlayList: State<List<MediaData>> get() = _currentPlayList

    val actionSeekBar = mutableStateOf(false)
    private val _playerState = MutableStateFlow(false)
    val playerState = _playerState.asStateFlow()
    val maxValue = mutableStateOf(0F)

    val currentPosition = mutableStateOf(0F)

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val browser = mediaConnect.browser ?: return
            when (playbackState) {
                Player.STATE_READY -> {
                    maxValue.value = (browser.duration).toFloat()
                }
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

    private val browserListener = object :BrowserListener{
        override fun playListUpdate() {
            updateCurrentPlayList()
        }
    }

    fun setController(){
        addListener(listener)
        addBrowserListener(browserListener)
        val browser = this.browser?:return
        maxValue.value = (browser.duration).toFloat() ?: 0F
        _playerState.value = browser.isPlaying == true
        refresh()
        updateItem(browser.currentMediaItem)
        shuffleUI.value = browser.shuffleModeEnabled
        repeatModeUI.value = browser.repeatMode
    }

    init {
        viewModelScope.launch {
            _playerState.collectLatest {
                if (it && browser?.isConnected == true) {
                    while (true) {
                        if (!actionSeekBar.value) {
                            currentPosition.value = browser?.currentPosition?.toFloat() ?: 0F
                        }
                        delay(1000L)
                    }
                }
            }
        }
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
        currentPosition.value = browser.currentPosition.toFloat()
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

    fun getStartToNext(nextIndex: Int, start: Long): Long {
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
                mediaConnect.context.contentResolver.openFileDescriptor(
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
        return netRepository.selectLyrics(id)
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


//    suspend fun removeSheetItem(item: MediaItem, sheetItem: MediaItem): String {
//        val parentId = sheetItem.mediaId
//        val sheetId = Uri.parse(parentId).lastPathSegment
//        val musicId = Uri.parse(item.mediaId).lastPathSegment
//        return try {
//            if (parentId.isLocal()) {
//                val result = repository.removeSheetItem(sheetId!!, musicId!!)
//                if (result == 1) "成功移除" else "移除失败"
//            } else {
//                netRepository.removeSheetItem(sheetId!!, musicId!!).msg
//            }
//        } catch (e: Exception) {
//            "error"
//        }
//    }

    fun setRepeatMode():Int?{
        val browser = this.browser?:return null
        when (browser.repeatMode) {
            Player.REPEAT_MODE_ALL -> browser.repeatMode = Player.REPEAT_MODE_OFF
            Player.REPEAT_MODE_OFF -> browser.repeatMode = Player.REPEAT_MODE_ONE
            Player.REPEAT_MODE_ONE -> browser.repeatMode = Player.REPEAT_MODE_ALL
        }
        return browser.repeatMode
    }

    fun setShuffle():Boolean{
        val browser = this.browser ?: return false
        browser.shuffleModeEnabled = !browser.shuffleModeEnabled
        return browser.shuffleModeEnabled
    }

    fun removePlayItem(position: Int) {
        val browser = this.browser?: return
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
        currentPosition.value = position.toFloat()
        if (lyricsCanScroll.value) {
            setLyricsItem(getStartIndex(position))
        }
    }

    val itemColor = MutableStateFlow(Pair(Color.Black, Color.Black))

    val dark = MutableStateFlow(false)

    fun putTransDark(isDark: Boolean) {
        dark.value = isDark
        transformColor(mediaConnect.browser?.currentMediaItem)
    }

    fun transformColor(item: MediaItem?, isDark: Boolean = dark.value) {
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
}