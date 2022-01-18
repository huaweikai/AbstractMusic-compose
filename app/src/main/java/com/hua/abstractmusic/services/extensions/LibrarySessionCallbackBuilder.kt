package com.hua.abstractmusic.services.extensions

import android.content.Context
import android.os.Bundle
import androidx.media2.common.BaseResult.RESULT_ERROR_NOT_SUPPORTED
import androidx.media2.session.*
import com.google.android.exoplayer2.ext.media2.SessionCallbackBuilder
import com.google.android.exoplayer2.ext.media2.SessionPlayerConnector

/**
 * @author Xiaoc
 * @since 2021-11-14
 *
 * [LibrarySessionCallback]创建的构建器
 * 用于构建[LibrarySessionCallback]的实例，你可以自定义要使用的功能清单
 * 具体见[build]方法
 */
class LibrarySessionCallbackBuilder(
    private val context: Context,
    private val sessionPlayerConnector: SessionPlayerConnector,
    private val libraryItemProvider: LibraryItemProvider? = null
) {

    companion object{
        const val DEFAULT_SEEK_TIMEOUT_MS = 1500
    }

    private var fastForwardMs = 0
    private var rewindMs = 0
    private var seekTimeoutMs = DEFAULT_SEEK_TIMEOUT_MS

    var ratingCallback: SessionCallbackBuilder.RatingCallback? = null
    var customCommandProvider: SessionCallbackBuilder.CustomCommandProvider? = null
    var mediaItemProvider: SessionCallbackBuilder.MediaItemProvider? = null
    var allowedCommandProvider: SessionCallbackBuilder.AllowedCommandProvider? = null
    var skipCallback: SessionCallbackBuilder.SkipCallback? = null
    var postConnectCallback: SessionCallbackBuilder.PostConnectCallback? = null
    var disconnectedCallback: SessionCallbackBuilder.DisconnectedCallback? = null

    fun build(): MediaLibraryService.MediaLibrarySession.MediaLibrarySessionCallback{
        return LibrarySessionCallback(
            SessionCallbackBuilder(context,sessionPlayerConnector).apply {
                setFastForwardIncrementMs(fastForwardMs)
                setRewindIncrementMs(rewindMs)
                setSeekTimeoutMs(seekTimeoutMs)
                setRatingCallback(ratingCallback)
                setCustomCommandProvider(customCommandProvider)
                setMediaItemProvider(mediaItemProvider)
                setAllowedCommandProvider(allowedCommandProvider)
                setSkipCallback(skipCallback)
                setPostConnectCallback(postConnectCallback)
                setDisconnectedCallback(disconnectedCallback)
            },
            libraryItemProvider ?: DefaultLibraryItemProvider()
        )
    }



    /**
     * 默认的LibraryItem提供器
     * 所有命令都返回不支持
     */
    class DefaultLibraryItemProvider: LibraryItemProvider{

        override fun onGetLibraryRoot(
            session: MediaLibraryService.MediaLibrarySession,
            controller: MediaSession.ControllerInfo,
            params: MediaLibraryService.LibraryParams?
        ): LibraryResult {
            return LibraryResult(RESULT_ERROR_NOT_SUPPORTED)
        }

        override fun onGetItem(
            session: MediaLibraryService.MediaLibrarySession,
            controller: MediaSession.ControllerInfo,
            mediaId: String
        ): LibraryResult {
            return LibraryResult(RESULT_ERROR_NOT_SUPPORTED)
        }

        override fun onGetChildren(
            session: MediaLibraryService.MediaLibrarySession,
            controller: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: MediaLibraryService.LibraryParams?
        ): LibraryResult {
            return LibraryResult(RESULT_ERROR_NOT_SUPPORTED)
        }

        override fun onSubscribe(
            session: MediaLibraryService.MediaLibrarySession,
            controller: MediaSession.ControllerInfo,
            parentId: String,
            params: MediaLibraryService.LibraryParams?
        ): Int {
            return RESULT_ERROR_NOT_SUPPORTED
        }

        override fun onUnsubscribe(
            session: MediaLibraryService.MediaLibrarySession,
            controller: MediaSession.ControllerInfo,
            parentId: String
        ): Int {
            return RESULT_ERROR_NOT_SUPPORTED
        }

        override fun onSearch(
            session: MediaLibraryService.MediaLibrarySession,
            controller: MediaSession.ControllerInfo,
            query: String,
            params: MediaLibraryService.LibraryParams?
        ): Int {
            return RESULT_ERROR_NOT_SUPPORTED
        }

        override fun onGetSearchResult(
            session: MediaLibraryService.MediaLibrarySession,
            controller: MediaSession.ControllerInfo,
            query: String,
            page: Int,
            pageSize: Int,
            params: MediaLibraryService.LibraryParams?
        ): LibraryResult {
            return LibraryResult(RESULT_ERROR_NOT_SUPPORTED)
        }

    }

}