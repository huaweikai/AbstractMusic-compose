package com.hua.abstractmusic.services.extensions

import androidx.media2.common.MediaItem
import androidx.media2.session.LibraryResult
import androidx.media2.session.MediaLibraryService
import androidx.media2.session.MediaSession
import com.hua.abstractmusic.services.MediaItemTree
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author : huaweikai
 * @Date   : 2021/11/26
 * @Desc   : MediaSessionCallback
 */
class MediaSessionCallback(
    private val mediaItemTree :MediaItemTree
): MediaLibraryService.MediaLibrarySession.MediaLibrarySessionCallback() {

    override fun onGetLibraryRoot(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult {
        return LibraryResult(LibraryResult.RESULT_SUCCESS,mediaItemTree.getRootItem(),null)
    }

    override fun onGetItem(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        mediaId: String
    ): LibraryResult {
        return LibraryResult(LibraryResult.RESULT_ERROR_NOT_SUPPORTED)
    }

    override fun onGetChildren(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult {
        val children = mediaItemTree.getChildren(parentId)
        return if(children != null){
            LibraryResult(LibraryResult.RESULT_SUCCESS,children,null)
        }else{
            LibraryResult(LibraryResult.RESULT_ERROR_NOT_SUPPORTED)
        }
    }
    override fun onSubscribe(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        parentId: String,
        params: MediaLibraryService.LibraryParams?
    ): Int {
        return LibraryResult.RESULT_ERROR_NOT_SUPPORTED
    }

    override fun onUnsubscribe(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        parentId: String
    ): Int {
        return LibraryResult.RESULT_ERROR_NOT_SUPPORTED
    }

    override fun onSearch(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        query: String,
        params: MediaLibraryService.LibraryParams?
    ): Int {
        return LibraryResult.RESULT_ERROR_NOT_SUPPORTED
    }

    override fun onGetSearchResult(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        query: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult {
        return LibraryResult(LibraryResult.RESULT_ERROR_NOT_SUPPORTED)
    }

    override fun onCreateMediaItem(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaId: String
    ): MediaItem? {
        return mediaItemTree.getItem(mediaId)
    }
}