package com.hua.abstractmusic.services

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media2.session.MediaController
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.hua.abstractmusic.R
import com.hua.abstractmusic.other.Constant.NOTIFICATION_CHANNEL_ID
import com.hua.abstractmusic.other.Constant.NOTIFICATION_ID
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title

/**
 * @ProjectName : 抽象音乐
 * @Author :huaweikai
 * @Time : 2021/9/11  14:22
 * @Description : 自定义的playernotification
 */
class MusicNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener
){
    private val notificationManager:PlayerNotificationManager
    val imageLoader = ImageLoader(context)

    init {
        val mediaControllerCompat = MediaController.Builder(context)
            .setSessionCompatToken(sessionToken)
            .build()
        notificationManager = PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            NOTIFICATION_CHANNEL_ID
        ).apply {
            setMediaDescriptionAdapter(DescriptionAdapter(mediaControllerCompat))
            setNotificationListener(notificationListener)
        }.build()
    }

    fun showNotification(player: Player){
        notificationManager.setPlayer(player)
    }

    fun hideNotification(){
        notificationManager.setPlayer(null)
    }


    //用来控制媒体的我们还需要将session给这个，我们可以用它控制当前音乐，并且在服务中也有这样
    //服务链接中有这样的媒体控制器
    private inner class DescriptionAdapter(
        private val mediaControllerCompat: MediaController
    ):PlayerNotificationManager.MediaDescriptionAdapter{
        //获取当前内容和标题函数
        override fun getCurrentContentTitle(player: Player): CharSequence {
            //在此我们只想返回播放歌曲的标题给我们的媒体
            return mediaControllerCompat.currentMediaItem?.metadata?.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            //需要我们的活动未决意图,这个我们在service中已经设置了
            return mediaControllerCompat.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence {
            return mediaControllerCompat.currentMediaItem?.metadata?.artist.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val request=ImageRequest.Builder(context)
                .data(mediaControllerCompat.currentMediaItem?.metadata?.albumArtUri)
                .target(
                    onSuccess = {
                        callback.onBitmap(it.toBitmap())
                    },
                    onError = {
                        callback.onBitmap(BitmapFactory.decodeResource(context.resources,
                            R.drawable.music
                        ))
                    }
                )
                .build()
            imageLoader.enqueue(request)
            return null
        }
    }
}
