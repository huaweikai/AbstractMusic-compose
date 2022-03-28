package com.hua.abstractmusic.ui.home.detail.albumdetail

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.NetWork
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.services.MediaConnect
import com.hua.abstractmusic.ui.utils.LCE
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
    private val netRepository: NetRepository
) : BaseViewModel(mediaConnect) {
    var id: String? = null
    var isLocal: Boolean = true
    var item: MediaItem? = null

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

    fun netRefresh() {
        if (id == null) return
        viewModelScope.launch {
            _screenState.value = LCE.Loading
            val result = netRepository.selectMusicById(Uri.parse(id))
            if (result?.code == NetWork.SUCCESS) {
                _albumDetail.value = result.data?.map { MediaData(it) } ?: emptyList()
                playListMap[id!!] = _albumDetail
                _screenState.value = LCE.Success
            }else{
                _screenState.value = LCE.Error
            }
        }
    }
}