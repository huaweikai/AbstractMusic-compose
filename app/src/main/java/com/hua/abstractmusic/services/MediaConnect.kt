package com.hua.abstractmusic.services

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.hua.abstractmusic.other.Constant
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author : huaweikai
 * @Date   : 2022/03/25
 * @Desc   :
 */

@SuppressLint("UnsafeOptInUsageError")
class MediaConnect(
    val context: Context,
    private val componentName: ComponentName,
    val itemTree: MediaItemTree
) {
    val isConnected = MutableStateFlow(false)

    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    val browser: MediaBrowser?
        get() = if (browserFuture.isDone) browserFuture.get() else null

//    private lateinit var controllerFuture: ListenableFuture<MediaController>
//    val controller: MediaController?
//        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    val browserListeners = arrayListOf<BrowserListener>()

    init {
        initializeController()
    }

    fun initializeController() {
        browserFuture = MediaBrowser.Builder(
            context,
            SessionToken(context, componentName)
        ).setListener(MediaBrowserConnectionCallback()).buildAsync()
        browserFuture.addListener({
            isConnected.value = true
            //初始化本地
            listOf(Constant.LOCAL_ALL_ID, Constant.LOCAL_ALBUM_ID, Constant.LOCAL_ARTIST_ID).forEach {
                browser?.getChildren(it, Int.MAX_VALUE, Int.MAX_VALUE, null)
            }
        }, MoreExecutors.directExecutor())
    }

    fun releaseController() {
        MediaBrowser.releaseFuture(browserFuture)
    }


    fun addListener(listener: Player.Listener) {
        browser?.addListener(listener)
    }

    fun removeListener(listener: Player.Listener?) {
        listener?.let {
            browser?.removeListener(it)
        }
    }

    fun addBrowserListener(browserListener: BrowserListener) {
        browserListeners.add(browserListener)
    }

    fun removeBrowserListener(browserListener: BrowserListener?) {
        browserListener?.let {
            browserListeners.remove(it)
        }
    }

    fun updatePlayList() {
        browserListeners.forEach {
            it.playListUpdate()
        }
    }


    private inner class MediaBrowserConnectionCallback : MediaBrowser.Listener {

        override fun onDisconnected(controller: MediaController) {
            isConnected.value = false
        }

        override fun onChildrenChanged(
            browser: MediaBrowser,
            parentId: String,
            itemCount: Int,
            params: MediaLibraryService.LibraryParams?
        ) {
            browserListeners.forEach {
                it.onChildrenChanged(browser, parentId, itemCount, params)
            }
        }
    }
}

interface BrowserListener {
    fun onChildrenChanged(
        browser: MediaBrowser,
        parentId: String,
        itemCount: Int,
        params: MediaLibraryService.LibraryParams?
    ) {
    }

    fun playListUpdate() {}
}