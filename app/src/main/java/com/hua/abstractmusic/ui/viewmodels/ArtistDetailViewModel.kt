package com.hua.abstractmusic.ui.viewmodels

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.media2.common.MediaItem
import androidx.media2.session.MediaBrowser
import androidx.media2.session.MediaController
import androidx.media2.session.MediaLibraryService
import androidx.media2.session.SessionCommandGroup
import com.hua.abstractmusic.base.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.ARTIST_ID
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/01/19
 * @Desc   : view
 */
@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    itemTree: MediaItemTree
) : BaseBrowserViewModel(application, useCase, itemTree) {
    private var artistAlbumId: String? = null
    var artistId: String? = null
        set(value) {
            field = value
            val id = Uri.parse(value).lastPathSegment
            artistAlbumId = "$ARTIST_ID/abAlbum/$id"
        }

    override fun onMediaConnected(
        controller: MediaController,
        allowedCommands: SessionCommandGroup
    ) {
        listMap[artistId!!] = _artistDetail
        listMap[artistAlbumId!!] = _artistAlbumDetail
        playListMap[artistId!!] = _artistDetail
        listMap.keys.forEach {
            detailInit(it)
        }
    }

    private val _artistDetail = mutableStateOf<List<MediaData>>(emptyList())
    val artistDetail: State<List<MediaData>> get() = _artistDetail

    private val _artistAlbumDetail = mutableStateOf<List<MediaData>>(emptyList())
    val artistAlbumDetail: State<List<MediaData>> get() = _artistAlbumDetail
}