package com.hua.abstractmusic.ui.home.detail.artistdetail

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.hua.abstractmusic.base.viewmodel.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.ARTIST_TO_ALBUM
import com.hua.abstractmusic.other.Constant.LOCAL_ARTIST_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ARTIST_ID
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/01/19
 * @Desc   : view
 */
@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    itemTree: MediaItemTree
) : BaseBrowserViewModel(application, useCase, itemTree) {
    private var artistAlbumId: String? = null
    var isLocal: Boolean = true
    var artistId: String? = null
        set(value) {
            field = value
            val id = Uri.parse(value).lastPathSegment
            artistAlbumId = if(isLocal){
                "$LOCAL_ARTIST_ID/${ARTIST_TO_ALBUM}/$id"
            }else{
                "$NETWORK_ARTIST_ID/$ARTIST_TO_ALBUM/$id"
            }
        }

    override fun onMediaConnected() {
        if (isLocal) {
            localListMap[artistId!!] = _artistDetail
            localListMap[artistAlbumId!!] = _artistAlbumDetail
        } else {
            netListMap[artistId!!] = _artistDetail
            netListMap[artistAlbumId!!] = _artistAlbumDetail
        }
        playListMap[artistId!!] = _artistDetail
        refresh()
//        viewModelScope.launch{
//            delay(200)
//            withContext(Dispatchers.Main){
//
//            }
//        }
    }

    private val _artistDetail = mutableStateOf<List<MediaData>>(emptyList())
    val artistDetail: State<List<MediaData>> get() = _artistDetail

    private val _artistAlbumDetail = mutableStateOf<List<MediaData>>(emptyList())
    val artistAlbumDetail: State<List<MediaData>> get() = _artistAlbumDetail
}