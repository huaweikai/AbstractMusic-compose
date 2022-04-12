package com.hua.abstractmusic.ui.home.detail.albumdetail

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.repository.NetWorkRepository
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.model.music.MediaData
import com.hua.network.ApiResult
import com.hua.service.MediaConnect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/01/13
 * @Desc   : viewmodel
 */
@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    mediaConnect: MediaConnect,
    private val netRepository: NetWorkRepository
) : BaseViewModel(mediaConnect) {
    var item: MediaItem? = null
    var id: String? = null
    var isLocal: Boolean = true

    private val _albumDetail = mutableStateOf<List<MediaData>>(emptyList())
    val albumDetail: State<List<MediaData>> get() = _albumDetail

    private val listener = object :Player.Listener{
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateItem(mediaItem)
        }
    }

    init {
        addListener(listener)
    }

    fun loadData(){
        if(isLocal){
            refresh()
        }else{
            netRefresh()
        }
    }

    override fun refresh() {
        if (id == null) return
        localListMap[id!!] = _albumDetail
        super.refresh()
        playListMap[id!!] = _albumDetail
    }

    private fun netRefresh() {
        if (id == null) return
        viewModelScope.launch {
            _screenState.value = LCE.Loading
            val result = netRepository.selectMusicByType(Uri.parse(id))
            if (result is ApiResult.Success) {
                _albumDetail.value = result.data.map { MediaData(it) }
                playListMap[id!!] = _albumDetail
                _screenState.value = LCE.Success
            }else{
                _screenState.value = LCE.Error
            }
        }
    }
}