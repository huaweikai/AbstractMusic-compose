package com.hua.abstractmusic.ui.home.detail.artistdetail

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.NetWork
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.services.MediaConnect
import com.hua.abstractmusic.ui.utils.LCE
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
    private val netRepository: NetRepository
) : BaseViewModel(mediaConnect) {
    private var artistAlbumId: String? = null
    var isLocal: Boolean = true
    var artistId: String? = null
        set(value) {
            field = value
            val id = Uri.parse(value).lastPathSegment
            artistAlbumId = if(isLocal){
                "${Constant.LOCAL_ARTIST_ID}/${Constant.ARTIST_TO_ALBUM}/$id"
            }else{
                "${Constant.NETWORK_ARTIST_ID}/${Constant.ARTIST_TO_ALBUM}/$id"
            }
        }

    private val listener = object : Player.Listener{
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateItem(mediaItem)
        }
    }

    init {
        addListener(listener)
    }

    override fun refresh() {
        localListMap[artistId!!] = _artistDetail
        localListMap[artistAlbumId!!] = _artistAlbumDetail
        super.refresh()
        playListMap[artistId!!] = _artistDetail
    }

    fun netRefresh(){
        val artistId = artistId?:return
        viewModelScope.launch {
            _screenState.value = LCE.Loading
            val albums = netRepository.selectAlbumByArtist(Uri.parse(artistAlbumId))
            val list = netRepository.selectMusicById(Uri.parse(artistId))
            if(albums?.code == NetWork.SUCCESS && list?.code == NetWork.SUCCESS){
                _artistDetail.value = list.data?.map { MediaData(it) }?: emptyList()
                _artistAlbumDetail.value = albums.data?.map { MediaData(it) }?: emptyList()
                playListMap[artistId] = _artistDetail
                _screenState.value = LCE.Success
            }else{
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

    private val _artistDetail = mutableStateOf<List<MediaData>>(emptyList())
    val artistDetail: State<List<MediaData>> get() = _artistDetail

    private val _artistAlbumDetail = mutableStateOf<List<MediaData>>(emptyList())
    val artistAlbumDetail: State<List<MediaData>> get() = _artistAlbumDetail
}
//@SuppressLint("UnsafeOptInUsageError")
//@HiltViewModel
//class ArtistDetailViewModel @Inject constructor(
//    application: Application,
//    useCase: UseCase,
//    itemTree: MediaItemTree
//) : BaseBrowserViewModel(application, useCase, itemTree) {
//    private var artistAlbumId: String? = null
//    var isLocal: Boolean = true
//    var artistId: String? = null
//        set(value) {
//            field = value
//            val id = Uri.parse(value).lastPathSegment
//            artistAlbumId = if(isLocal){
//                "$LOCAL_ARTIST_ID/${ARTIST_TO_ALBUM}/$id"
//            }else{
//                "$NETWORK_ARTIST_ID/$ARTIST_TO_ALBUM/$id"
//            }
//        }
//
//    override fun onMediaConnected() {
//        if (isLocal) {
//            localListMap[artistId!!] = _artistDetail
//            localListMap[artistAlbumId!!] = _artistAlbumDetail
//        } else {
//            netListMap[artistId!!] = _artistDetail
//            netListMap[artistAlbumId!!] = _artistAlbumDetail
//        }
//        playListMap[artistId!!] = _artistDetail
//        refresh()
////        viewModelScope.launch{
////            delay(200)
////            withContext(Dispatchers.Main){
////
////            }
////        }
//    }
//
//    private val _artistDetail = mutableStateOf<List<MediaData>>(emptyList())
//    val artistDetail: State<List<MediaData>> get() = _artistDetail
//
//    private val _artistAlbumDetail = mutableStateOf<List<MediaData>>(emptyList())
//    val artistAlbumDetail: State<List<MediaData>> get() = _artistAlbumDetail
//}