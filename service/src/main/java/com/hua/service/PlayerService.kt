package com.hua.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
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
import com.hua.service.preference.PreferenceManager
import com.hua.service.extensions.LibrarySessionCallback
import com.hua.service.usecase.UseCase
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
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var useCase: UseCase

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
        val sessionActivityPendingIntent:PendingIntent = PendingIntent.getActivity(this,0,Intent("com.hua.abstractmusic.NOTIFICATION_START"),PendingIntent.FLAG_IMMUTABLE)
        val sessionPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { intent ->
                PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_IMMUTABLE
                )
            }

        mediaLibrarySession = MediaLibrarySession.Builder(
            this,
            exoplayer.apply { addListener(PlayerListener()) },
            LibrarySessionCallback(itemTree, serviceScope)
        )
            .setMediaItemFiller(CustomMediaItemFiller())
            .setSessionActivity(sessionPendingIntent ?: sessionActivityPendingIntent)
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
                val list = useCase.selectCurrentListCase()
                if (list.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        browser?.setMediaItems(list, index, preferenceManager.mediaPosition)
                        browser?.prepare()
                        browser?.shuffleModeEnabled = preferenceManager.shuffleMode
                        browser?.repeatMode = preferenceManager.repeatMode
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (intent.action) {
                "controller" -> setPlayOrPause()
                "controller_prev" -> exoplayer.seekToPrevious()
                "controller_next" -> exoplayer.seekToNextMediaItem()
            }
        }
        return super.onStartCommand(intent, flags, startId)
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
                    preferenceManager.mediaPosition = 0L
                }
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
//            sendBroadcast(Intent(this@PlayerService, MusicWidgetReceiver::class.java).apply {
//                action = "tran"
//                putExtra("mediaItem", exoplayer.currentMediaItem?.toParcel())
//                putExtra("state", playWhenReady)
//            })
            if (!playWhenReady) {
                stopForeground(false)
            }
            preferenceManager.mediaPosition = browser?.currentPosition ?: 0L
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            preferenceManager.lastMediaIndex = exoplayer.currentMediaItemIndex
//            sendBroadcast(Intent(this@PlayerService, MusicWidgetReceiver::class.java).apply {
//                action = "tran"
//                putExtra("mediaItem", mediaItem?.toParcel())
//                putExtra("state", exoplayer.isPlaying)
//            })
        }

        override fun onPlayerError(error: PlaybackException) {
            Toast.makeText(this@PlayerService, "当前资源丢失无法播放", Toast.LENGTH_SHORT).show()
            exoplayer.removeMediaItem(exoplayer.currentMediaItemIndex)
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            preferenceManager.repeatMode = repeatMode
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            preferenceManager.shuffleMode = shuffleModeEnabled
        }
    }

    private fun setPlayOrPause() {
        if (exoplayer.isPlaying) {
            exoplayer.pause()
        } else {
            exoplayer.play()
        }
    }
}
