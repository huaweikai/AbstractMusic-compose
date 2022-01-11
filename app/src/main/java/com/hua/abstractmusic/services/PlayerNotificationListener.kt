package com.hua.abstractmusic.services

import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.offline.DownloadService.startForeground
import com.google.android.exoplayer2.ui.PlayerNotificationManager

/**
 * @author : huaweikai
 * @Date   : 2022/01/09
 * @Desc   : notification的监听？
 */
class PlayerNotificationListener(
    private val musicService: PlayerService
): PlayerNotificationManager.NotificationListener {

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
//        musicService.apply {
            //如果是持续存在的，且不在后台
            if(ongoing &&!musicService.isForegroundService){
                ContextCompat.startForegroundService(
                    musicService,
                    Intent(musicService,PlayerService::class.java)
                )
                musicService.startForeground(notificationId,notification)
                musicService.isForegroundService=true
            }
//        }
    }

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        //通知取消时，要做的动作
//        musicService.apply {
            musicService.stopForeground(true)
            musicService.isForegroundService=false
            musicService.stopSelf()
//        }
    }
}