package com.hua.abstractmusic.services.extensions

import android.annotation.SuppressLint
import androidx.media3.common.MediaItem
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.utils.isLocal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author Xiaoc
 * @since 2021-11-14
 *
 * 用于构建[MediaLibraryService.MediaLibrarySession]的回调扩展
 */
@SuppressLint("UnsafeOptInUsageError")
class LibrarySessionCallback(
    private val itemTree: MediaItemTree,
    private val scope: CoroutineScope
) : MediaLibraryService.MediaLibrarySession.MediaLibrarySessionCallback {
    override fun onGetLibraryRoot(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {
        return Futures.immediateFuture(LibraryResult.ofItem(itemTree.getRootItem(), params))
    }

    override fun onGetItem(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): ListenableFuture<LibraryResult<MediaItem>> {
        val item = itemTree.getItem(mediaId) ?: return Futures.immediateFuture(
            LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
        )
        return Futures.immediateFuture(LibraryResult.ofItem(item, null))
    }

    override fun onGetChildren(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        if (parentId.isLocal()) {
            val children = itemTree.getChildren(parentId)
            return if (children == null) {
                Futures.immediateFuture(
                    LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                )
            } else {
                Futures.immediateFuture(LibraryResult.ofItemList(children, params))
            }
        } else {
            scope.launch {
                if (itemTree.networkGetChildren(parentId).isNullOrEmpty()) {
                    session.notifyChildrenChanged(parentId, 0, null)
                } else {
                    session.notifyChildrenChanged(parentId, 1, null)
                }
            }
            return Futures.immediateFuture(
                LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
            )
        }
    }

    override fun onSubscribe(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<Void>> {
        return Futures.immediateFuture(
            LibraryResult.ofVoid()
        )
    }
}