package com.hua.abstractmusic.ui.home.detail.artistdetail

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.repository.NetWorkRepository
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.abstractmusic.utils.isLocal
import com.hua.model.music.MediaData
import com.hua.model.other.Constants
import com.hua.model.other.Constants.PARCEL_ITEM_ID
import com.hua.model.parcel.ParcelizeMediaItem
import com.hua.network.ApiResult
import com.hua.network.get
import com.hua.network.onFailure
import com.hua.network.onSuccess
import com.hua.service.MediaConnect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/01/19
 * @Desc   : view
 */
@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    mediaConnect: MediaConnect,
    private val netRepository: NetWorkRepository,
    savedStateHandle: SavedStateHandle?
) : BaseViewModel(mediaConnect) {
    private var artistAlbumId: String? = null
    var item:ParcelizeMediaItem ?= null
    var isLocal: Boolean = true
    var artistId: String? = null
        set(value) {
            field = value
            val id = Uri.parse(value).lastPathSegment
            artistAlbumId = if(isLocal){
                "${Constants.LOCAL_ARTIST_ID}/${Constants.ARTIST_TO_ALBUM}/$id"
            }else{
                "${Constants.NETWORK_ARTIST_ID}/${Constants.ARTIST_TO_ALBUM}/$id"
            }
        }
    private val listener = object : Player.Listener{
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateItem(mediaItem)
        }
    }
    private val _artistDetail = mutableStateOf<List<MediaData>>(emptyList())
    val artistDetail: State<List<MediaData>> get() = _artistDetail

    private val _artistAlbumDetail = mutableStateOf<List<MediaData>>(emptyList())
    val artistAlbumDetail: State<List<MediaData>> get() = _artistAlbumDetail
    init {
        savedStateHandle?.let { saveState ->
            item = saveState.get<ParcelizeMediaItem>(PARCEL_ITEM_ID)
            item?.let {
                isLocal = it.mediaId.isLocal()
                artistId = it.mediaId
                loadData()
            }
        }
        addListener(listener)
    }

    override fun refresh() {
        localListMap[artistId!!] = _artistDetail
        localListMap[artistAlbumId!!] = _artistAlbumDetail
        super.refresh()
        playListMap[artistId!!] = _artistDetail
    }

    private fun netRefresh(){
        val browser = mediaConnect.browser?:return
        val artistId = artistId?:return
        viewModelScope.launch {
            _screenState.value = LCE.Loading
            val albums = netRepository.selectAlbumByArtist(Uri.parse(artistAlbumId))
            val list = netRepository.selectMusicByType(Uri.parse(artistId))
            _artistDetail.value = list.get { emptyList() }.map { MediaData(it, isPlaying = it.mediaId == browser.currentMediaItem?.mediaId) }
            _artistAlbumDetail.value = albums.get { emptyList() }.map { MediaData(it) }
            list.onSuccess {
                playListMap[artistId] = _artistDetail
                _screenState.value = LCE.Success
            }
            list.onFailure {
                _screenState.value = LCE.Error
            }
        }
    }

    fun loadData(){
        if(isLocal){
            refresh()
        }else{
            netRefresh()
        }
    }
}