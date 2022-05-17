package com.hua.abstractmusic.ui.viewmodels

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.repository.NetWorkRepository
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.model.music.MediaData
import com.hua.service.MediaConnect
import com.hua.service.MediaItemTree
import com.hua.model.other.Constants.NETWORK_ALBUM_ID
import com.hua.model.other.Constants.NETWORK_BANNER_ID
import com.hua.model.other.Constants.NETWORK_MUSIC_ID
import com.hua.model.other.Constants.NETWORK_RECOMMEND_ID
import com.hua.network.ApiResult
import com.hua.network.onFailure
import com.hua.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/03/25
 * @Desc   :
 */
@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
class NetViewModel @Inject constructor(
    mediaConnect: MediaConnect,
    private val netRepository: NetWorkRepository,
    private val itemTree: MediaItemTree
) : BaseViewModel(mediaConnect) {

    private val listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateItem(mediaItem)
        }
    }

    private val _bannerList = mutableStateOf<List<MediaItem>>(emptyList())
    val bannerList: State<List<MediaItem>> get() = _bannerList

    private val _recommendMusic = mutableStateOf<List<MediaItem>>(emptyList())

    private val _recommendSheet = mutableStateOf<List<MediaItem>>(emptyList())
    val recommendSheet: State<List<MediaItem>> get() = _recommendSheet

    private val _recommendAlbum = mutableStateOf<List<MediaItem>>(emptyList())
    val recommendAlbum: State<List<MediaItem>> get() = _recommendAlbum

    private val _playList = mutableStateOf<List<MediaData>>(emptyList())
    val playList: State<List<MediaData>> get() = _playList

    val snackBarState = SnackbarHostState()

    init {
        mediaConnect.addListener(listener)
        netListMap[NETWORK_BANNER_ID] = _bannerList
        netListMap[NETWORK_MUSIC_ID] = _recommendMusic
        netListMap[NETWORK_RECOMMEND_ID] = _recommendSheet
        netListMap[NETWORK_ALBUM_ID] = _recommendAlbum

        playListMap[NETWORK_MUSIC_ID] = _playList

        refresh()
    }

    private suspend fun showSnackBar(message: String) {
        snackBarState.showSnackbar(message)
    }

    override fun refresh() {
        viewModelScope.launch {
            _screenState.value = LCE.Loading
            var loadError = false
            netListMap.keys.forEach {
                if (!loadError) {
                    val result = netRepository.selectTypeList(Uri.parse(it))
                    netListMap[it]!!.value = if (result is ApiResult.Success) {
                        result.data
                    } else {
                        loadError = true
                        showSnackBar((result as ApiResult.Failure).error.errorMsg ?: "")
                        emptyList()
                    }
                }
            }
            _playList.value = _recommendMusic.value.map {
                MediaData(
                    it,
                    isPlaying = it.mediaId == browser?.currentMediaItem?.mediaId
                )
            }
            if (loadError) {
                _screenState.value = LCE.Error
            } else {
                _screenState.value = LCE.Success
            }
        }
    }

    fun listPlay(parentId: String) {
        viewModelScope.launch {
            val result = netRepository.selectMusicByType(Uri.parse(parentId))
            if (result is ApiResult.Success) {
                itemTree.addMusicToTree(parentId, result.data)
                setPlayList(0, result.data)
            } else {
                showSnackBar((result as ApiResult.Failure).error.errorMsg ?: "")
            }
        }
    }
}