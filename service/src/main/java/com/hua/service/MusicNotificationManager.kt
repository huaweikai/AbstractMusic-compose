package com.hua.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import coil.ImageLoader
import coil.request.ImageRequest

/**
 * @ProjectName : 抽象音乐
 * @Author :huaweikai
 * @Time : 2021/9/11  14:22
 * @Description : 自定义的playernotification
 */

@SuppressLint("UnsafeOptInUsageError")
class MusicNotificationManager(
    private val context: Context,
    mediaSession: MediaSession,
    notificationListener: PlayerNotificationManager.NotificationListener
) {
    private val notificationManager: PlayerNotificationManager

    //    @Inject
//    lateinit var imageLoader: ImageLoader
    private val imageLoader = ImageLoader(context)

    init {
        val mediaControllerCompat =
            MediaController.Builder(context, mediaSession.token).buildAsync().get()
        notificationManager = PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            NOTIFICATION_CHANNEL_ID
        ).apply {
            setChannelNameResourceId(R.string.notification_channel_name)
            setChannelDescriptionResourceId(R.string.notification_channel_description)
            setMediaDescriptionAdapter(DescriptionAdapter(mediaControllerCompat))
            setNotificationListener(notificationListener)
            setNextActionIconResourceId(R.drawable.ic_play_next)
            setPreviousActionIconResourceId(R.drawable.ic_play_prev)
            setSmallIconResourceId(R.drawable.music)
        }.build().apply {
            setMediaSessionToken(mediaSession.sessionCompatToken as MediaSessionCompat.Token)
            setUseFastForwardAction(false)
            setUseRewindAction(false)
        }
    }


    fun showNotification(player: Player) {
        notificationManager.setPlayer(player)
    }

    fun hideNotification() {
        notificationManager.setPlayer(null)
    }


    //用来控制媒体的我们还需要将session给这个，我们可以用它控制当前音乐，并且在服务中也有这样
    //服务链接中有这样的媒体控制器
    private inner class DescriptionAdapter(
        private val mediaControllerCompat: MediaController
    ) : PlayerNotificationManager.MediaDescriptionAdapter {
        //获取当前内容和标题函数
        override fun getCurrentContentTitle(player: Player): CharSequence {
            //在此我们只想返回播放歌曲的标题给我们的媒体
            return mediaControllerCompat.currentMediaItem?.mediaMetadata?.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            //需要我们的活动未决意图,这个我们在service中已经设置了
            return mediaControllerCompat.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence {
            return mediaControllerCompat.currentMediaItem?.mediaMetadata?.artist.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            var bitmap: Bitmap? = null
            val request = ImageRequest.Builder(context)
                .data(mediaControllerCompat.currentMediaItem?.mediaMetadata?.artworkUri)
                .target(
                    onSuccess = {
                        bitmap = it.toBitmap()
                        callback.onBitmap(bitmap!!)
                    },
                    onError = {
                        bitmap = BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.music
                        )
                        callback.onBitmap(bitmap!!)
                    }
                )
                .build()
            imageLoader.enqueue(request)
            return null
        }
    }
}
const val NOTIFICATION_CHANNEL_ID = "music_notification"
const val NOTIFICATION_ID = 1
