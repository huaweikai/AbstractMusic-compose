package com.hua.abstractmusic.services.extensions

import android.net.Uri
import android.os.Bundle
import androidx.media2.common.MediaItem
import androidx.media2.common.Rating
import androidx.media2.session.*
import com.google.android.exoplayer2.ext.media2.SessionCallbackBuilder

/**
 * @author Xiaoc
 * @since 2021-11-14
 *
 * 用于构建[MediaLibraryService.MediaLibrarySession]的回调扩展
 */
class LibrarySessionCallback(
    sessionCallbackBuilder: SessionCallbackBuilder,
    private val libraryItemProvider: LibraryItemProvider,
): MediaLibraryService.MediaLibrarySession.MediaLibrarySessionCallback() {

    private val sessionCallback = sessionCallbackBuilder.build()

    override fun onGetLibraryRoot(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult {
        return libraryItemProvider.onGetLibraryRoot(session, controller, params)
    }

    override fun onGetChildren(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult {
        return libraryItemProvider.onGetChildren(session, controller, parentId, page, pageSize, params)
    }

    override fun onGetItem(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        mediaId: String
    ): LibraryResult {
        return libraryItemProvider.onGetItem(session, controller, mediaId)
    }

    override fun onGetSearchResult(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        query: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): LibraryResult {
        return libraryItemProvider.onGetSearchResult(session, controller, query, page, pageSize, params)
    }

    override fun onSearch(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        query: String,
        params: MediaLibraryService.LibraryParams?
    ): Int {
        return libraryItemProvider.onSearch(session, controller, query, params)
    }

    override fun onSubscribe(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        parentId: String,
        params: MediaLibraryService.LibraryParams?
    ): Int {
        return libraryItemProvider.onSubscribe(session, controller, parentId, params)
    }

    override fun onUnsubscribe(
        session: MediaLibraryService.MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        parentId: String
    ): Int {
        return libraryItemProvider.onUnsubscribe(session, controller, parentId)
    }

    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): SessionCommandGroup? {
        return sessionCallback.onConnect(session,controller)
    }

    override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
        return sessionCallback.onPostConnect(session,controller)
    }

    override fun onDisconnected(session: MediaSession, controller: MediaSession.ControllerInfo) {
        return sessionCallback.onDisconnected(session,controller)
    }

    override fun onCommandRequest(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        command: SessionCommand
    ): Int {
        return sessionCallback.onCommandRequest(session, controller, command)
    }

    override fun onCreateMediaItem(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaId: String
    ): MediaItem? {
        return sessionCallback.onCreateMediaItem(session, controller, mediaId)
    }

    override fun onSetRating(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaId: String,
        rating: Rating
    ): Int {
        return sessionCallback.onSetRating(session, controller, mediaId, rating)
    }

    override fun onSetMediaUri(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        uri: Uri,
        extras: Bundle?
    ): Int {
        return sessionCallback.onSetMediaUri(session, controller, uri, extras)
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle?
    ): SessionResult {
        return sessionCallback.onCustomCommand(session, controller, customCommand, args)
    }

    override fun onFastForward(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): Int {
        return sessionCallback.onFastForward(session, controller)
    }

    override fun onRewind(session: MediaSession, controller: MediaSession.ControllerInfo): Int {
        return sessionCallback.onRewind(session, controller)
    }

    override fun onSkipBackward(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): Int {
        return sessionCallback.onSkipBackward(session, controller)
    }

    override fun onSkipForward(
        session: MediaSession,
        controller: MediaSession.ControllerInfo
    ): Int {
        return sessionCallback.onSkipForward(session, controller)
    }

}