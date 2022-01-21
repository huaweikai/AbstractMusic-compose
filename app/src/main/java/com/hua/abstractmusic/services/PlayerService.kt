package com.hua.abstractmusic.services

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.os.Build
import android.content.*
import android.util.Log
import androidx.media2.common.SessionPlayer
import androidx.media2.session.MediaLibraryService
import androidx.media2.session.MediaSession
import androidx.media2.session.SessionCommand
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.media2.SessionPlayerConnector
import com.google.common.util.concurrent.MoreExecutors
import com.hua.abstractmusic.ui.MainActivity
import com.hua.abstractmusic.other.Constant.CLEAR_PLAY_LIST
import com.hua.abstractmusic.other.Constant.LASTMEDIA
import com.hua.abstractmusic.other.Constant.LASTMEDIAINDEX
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.services.extensions.*
import com.hua.abstractmusic.use_case.UseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2021/11/26
 * @Desc   : 播放服务
 */
@AndroidEntryPoint
class PlayerService : MediaLibraryService() {

    private lateinit var player: SessionPlayerConnector
    private lateinit var mediaLibrarySession: MediaLibrarySession

    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    @Inject
    lateinit var itemTree: MediaItemTree

    @Inject
    lateinit var exoplayer: ExoPlayer

    @Inject
    lateinit var useCase: UseCase

    lateinit var sessionCallback: MediaLibrarySession.MediaLibrarySessionCallback

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
        player = SessionPlayerConnector(exoplayer.apply {
            addListener(PlayerListener())
        })
        sessionCallback = LibrarySessionCallbackBuilder(
            this,
            player,
            PlayerLibraryItemProvider(itemTree, serviceScope, this)
        ).apply {
            mediaItemProvider = PlayerMediaItemProvider(itemTree)
            customCommandProvider = MediaCommand(this@PlayerService, serviceScope)
        }
            .build()
        mediaLibrarySession = MediaLibrarySession
            .Builder(
                this,
                player,
                Executors.newSingleThreadExecutor(),
                sessionCallback
            )
            .setSessionActivity(pendingIntent)
            .build()


        //生成自定义的通知管理
        notificationManager = MusicNotificationManager(
            this,
            mediaLibrarySession.sessionCompatToken,
            PlayerNotificationListener(this)
        )

        serviceScope.launch(Dispatchers.IO) {
            val sp = applicationContext.getSharedPreferences(LASTMEDIA, Context.MODE_PRIVATE)
            val index = sp.getInt(LASTMEDIAINDEX, 0)
            val list = useCase.getCurrentListCase()
            if (list.isNotEmpty()) {
                player.setPlaylist(list, null).addListener({
                    player.skipToPlaylistItem(index)
                    player.prepare()
                }, MoreExecutors.directExecutor())
            } else {
                player.addPlaylistItem(
                    0, NULL_MEDIA_ITEM
                )
            }
        }
    }


    override fun onUpdateNotification(session: MediaSession): MediaNotification? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        player.close()
        mediaLibrarySession.close()
    }

    fun removeAllMusic() {
            exoplayer.clearMediaItems()
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
    }
}
