package com.hua.service

import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.media3.ui.PlayerNotificationManager

/**
 * @author : huaweikai
 * @Date   : 2022/01/09
 * @Desc   : notification的监听？
 */
@androidx.media3.common.util.UnstableApi
class PlayerNotificationListener(
    private val musicService: PlayerService
): PlayerNotificationManager.NotificationListener {

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        musicService.apply {
            //如果是持续存在的，且不在后台
            if(ongoing &&!musicService.isForegroundService){
                ContextCompat.startForegroundService(
                    musicService,
                    Intent(musicService,PlayerService::class.java)
                )
                startForeground(notificationId,notification)
                isForegroundService = true
            }
        }
    }

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        //通知取消时，要做的动作
        musicService.apply {
            stopForeground(true)
            isForegroundService=false
            stopSelf()
        }
    }
}