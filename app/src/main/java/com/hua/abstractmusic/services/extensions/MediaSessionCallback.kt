package com.hua.abstractmusic.services.extensions

import android.media.Session2Command.Result.RESULT_SUCCESS
import android.os.Bundle
import android.util.Log
import androidx.media2.common.MediaItem
import androidx.media2.session.*
import androidx.media2.session.MediaLibraryService.MediaLibrarySession.MediaLibrarySessionCallback
import com.hua.abstractmusic.other.Constant.NETWORK_ALBUM_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ARTIST_ID
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.services.PlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author : huaweikai
 * @Date   : 2021/11/26
 * @Desc   : MediaSessionCallback
 */
class MediaSessionCallback(
    private val mediaItemTree: MediaItemTree,
    private val scope: CoroutineScope,
    private val service: PlayerService
) : MediaLibrarySessionCallback() {

    override fun onGetLibraryRoot(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult {
        return LibraryResult(LibraryResult.RESULT_SUCCESS, mediaItemTree.getRootItem(), null)
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
        scope.launch {
            mediaItemTree.getChildren(parentId)
            session.notifyChildrenChanged(parentId, 0, null)
        }
        return LibraryResult(LibraryResult.RESULT_ERROR_NOT_SUPPORTED)
    }

    override fun onSubscribe(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        parentId: String,
        params: MediaLibraryService.LibraryParams?
    ): Int {
        return LibraryResult.RESULT_SUCCESS
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

//    override fun onCustomCommand(
//        session: MediaSession,
//        controller: MediaSession.ControllerInfo,
//        customCommand: SessionCommand,
//        args: Bundle?
//    ): SessionResult {
//        Log.d("TAG", "onCustomCommand: ")
////        return super.onCustomCommand(session, controller, customCommand, args)
//        return SessionResult(SessionResult.RESULT_SUCCESS,null)
//
////        return when (customCommand.customAction) {
////            "null" -> {
////                service.removeAllMusic()
////                SessionResult(SessionResult.RESULT_SUCCESS, null)
////            }
////            else -> SessionResult(SessionResult.RESULT_ERROR_NOT_SUPPORTED, null)
////        }
//    }
//
//    override fun onCommandRequest(
//        session: MediaSession,
//        controller: MediaSession.ControllerInfo,
//        command: SessionCommand
//    ): Int {
//        Log.d("TAG", "onCommandRequest: ")
////        return super.onCommandRequest(session, controller, command)
//        return SessionResult.RESULT_SUCCESS
//    }
}