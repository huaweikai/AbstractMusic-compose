package com.hua.abstractmusic.ui.viewmodels

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.bean.net.HomeBean
import com.hua.abstractmusic.other.Constant.NETWORK_MUSIC_ID
import com.hua.abstractmusic.other.NetWork
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.services.MediaConnect
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.ui.utils.LCE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val netRepository: NetRepository,
    private val itemTree: MediaItemTree
) : BaseViewModel(mediaConnect) {

    private val listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateItem(mediaItem)
        }
    }

    private val _homeData = MutableStateFlow(
        HomeBean()
    )
    val homeData get() = _homeData.asStateFlow()
    val playLists = mutableStateOf(emptyList<MediaData>())

    init {
        mediaConnect.addListener(listener)
        playListMap[NETWORK_MUSIC_ID] = playLists
        refresh()
    }

    override fun refresh() {
        viewModelScope.launch {
            _screenState.value = LCE.Loading
            val result = netRepository.loadHomeData()
            if (result.isSuccess) {
                _homeData.value = result.getOrNull() ?: HomeBean()
                itemTree.addOnLineToTree(_homeData.value)
                playLists.value = itemTree.getCacheItems(NETWORK_MUSIC_ID).map {
                    MediaData(
                        it,
                        isPlaying = it.mediaId == browser?.currentMediaItem?.mediaId
                    )
                }
                _screenState.value = LCE.Success
            } else {
                _homeData.value = HomeBean()
                _screenState.value = LCE.Error
            }
        }
    }

    fun listPlay(parentId: String) {
        viewModelScope.launch {
            val result = netRepository.selectMusicById(Uri.parse(parentId)) ?: return@launch
            if (result.code == NetWork.SUCCESS) {
                itemTree.addMusicToTree(parentId, result.data)
                setPlayList(0, result.data ?: emptyList())
            }
        }
    }
}