package com.hua.abstractmusic.ui.home.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
    private val itemTree: MediaItemTree
) : BaseBrowserViewModel(application, useCase) {
    private var artistAlbumId: String? = null
    var artistId: String? = null
     set(value) {
         field = value
         val id = Uri.parse(value).lastPathSegment
         artistAlbumId = "$ARTIST_ID/abAlbum/$id"
     }

    private val browserCallback = object : MediaBrowser.BrowserCallback() {
        override fun onConnected(
            controller: MediaController,
            allowedCommands: SessionCommandGroup
        ) {
            artistId?.let {
                init(it)
                init(artistAlbumId!!)
            }
        }

        override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {

        }

        override fun onChildrenChanged(
            browser: MediaBrowser,
            parentId: String,
            itemCount: Int,
            params: MediaLibraryService.LibraryParams?
        ) {
            getItem(parentId)
        }
    }

    override fun initializeController() {
        connectBrowserService(browserCallback)
    }
    private val _artistDetail = mutableStateOf<List<MediaData>>(emptyList())
    val artistDetail:State<List<MediaData>> get() = _artistDetail

    private val _artistAlbumDetail = mutableStateOf<List<MediaData>>(emptyList())
    val artistAlbumDetail:State<List<MediaData>> get() = _artistAlbumDetail

    private fun getItem(parentId: String) {
        itemTree.getChildItem(parentId).map {
            MediaData(
                it,
                it.metadata?.mediaId == browser?.currentMediaItem?.metadata?.mediaId
            )
        }.apply {
            when(parentId){
                artistId-> _artistDetail.value = this
                artistAlbumId->_artistAlbumDetail.value = this
            }
        }
    }
}