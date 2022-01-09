package com.hua.abstractmusic.services

import android.app.Notification
import android.content.Intent
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.hua.abstractmusic.other.Constant.NOTIFICATION_ID

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
        musicService.apply {
            //如果是持续存在的，且不在后台
            if(ongoing &&!isForegroundService){
                ContextCompat.startForegroundService(
                    this,
                    Intent(this,PlayerService::class.java)
                )
                startForeground(NOTIFICATION_ID,notification)
                isForegroundService=true
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