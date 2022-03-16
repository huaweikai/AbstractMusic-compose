package com.hua.abstractmusic.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.hua.abstractmusic.preference.PreferenceManager
import com.hua.abstractmusic.services.extensions.LibrarySessionCallback
import com.hua.abstractmusic.ui.MainActivity
import com.hua.abstractmusic.use_case.UseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2021/11/26
 * @Desc   : 播放服务
 */
@AndroidEntryPoint
@SuppressLint("UnsafeOptInUsageError")
class PlayerService : MediaLibraryService() {

    //    private lateinit var player: SessionPlayerConnector
    private lateinit var mediaLibrarySession: MediaLibrarySession

    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    val browser: MediaBrowser?
        get() = if (browserFuture.isDone) browserFuture.get() else null

    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    lateinit var itemTree: MediaItemTree

    @Inject
    lateinit var exoplayer: ExoPlayer

    @Inject
    lateinit var useCase: UseCase

    @Inject
    lateinit var preferenceManager:PreferenceManager


    private lateinit var notificationManager: MusicNotificationManager
    var isForegroundService = false

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onCreate() {
        super.onCreate()
        initSessionAndPlayer()
    }


    private fun initSessionAndPlayer() {
        val parentScreenIntent = Intent(
            this, MainActivity::class.java
        )
        val intent = Intent(this, PlayerService::class.java)
        val sessionPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { intent ->
                PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_IMMUTABLE
                )
            }

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntent(parentScreenIntent)
            addNextIntent(intent)

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                getPendingIntent(
                    0,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }

        }

        mediaLibrarySession = MediaLibrarySession.Builder(
            this,
            exoplayer.apply { addListener(PlayerListener()) },
            LibrarySessionCallback(itemTree, serviceScope)
        )
            .setMediaItemFiller(CustomMediaItemFiller())
            .setSessionActivity(sessionPendingIntent?:pendingIntent)
            .build()


//        生成自定义的通知管理
        notificationManager = MusicNotificationManager(
            this,
            mediaLibrarySession,
            PlayerNotificationListener(this)
        )

        connectBrowser()
    }

    private fun connectBrowser() {
        browserFuture = MediaBrowser.Builder(
            this,
            mediaLibrarySession.token
        )
            .buildAsync()
        browserFuture.addListener({
            serviceScope.launch(Dispatchers.IO) {
                val index = preferenceManager.lastMediaIndex
                val list = useCase.getCurrentListCase()
                if (list.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        browser?.setMediaItems(list, index, 0)
                        browser?.prepare()
                    }
                }
            }
        }, MoreExecutors.directExecutor())
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaLibrarySession.player.release()
        mediaLibrarySession.release()
        MediaBrowser.releaseFuture(browserFuture)
        serviceScope.cancel()
    }


    inner class CustomMediaItemFiller : MediaSession.MediaItemFiller {
        override fun fillInLocalConfiguration(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItem: MediaItem
        ): MediaItem {
            return MediaItem.Builder()
                .setMediaMetadata(mediaItem.mediaMetadata)
                .setUri(mediaItem.mediaMetadata.mediaUri)
                .setMediaId(mediaItem.mediaId)
                .build()
        }
    }

    override fun onUpdateNotification(session: MediaSession): MediaNotification? {
        return null
    }

    private inner class PlayerListener : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
                    notificationManager.showNotification(exoplayer)
                }
                else -> {
                    notificationManager.hideNotification()
                }
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            if (!playWhenReady) {
                stopForeground(false)
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            preferenceManager.lastMediaIndex = exoplayer.currentMediaItemIndex
        }

        override fun onPlayerError(error: PlaybackException) {
            Toast.makeText(this@PlayerService, "当前资源丢失无法播放", Toast.LENGTH_SHORT).show()
            exoplayer.removeMediaItem(exoplayer.currentMediaItemIndex)
        }
    }
}
