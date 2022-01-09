package com.hua.abstractmusic.bean

import androidx.media2.common.MediaMetadata
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author : huaweikai
 * @Date   : 2021/11/24
 * @Desc   : 使用数据库来保存当前的播放列表
 *
 */
@Entity
data class CurrentPlayItem(
    val musicId:String,
    val title:String,
    val displayTitle :String,
    val displaySubtitle :String,
    val album :String,
    val artist:String,
    val duration :Long,
    val trackerNumber:Long,
    val mediaUri :String,
    val albumUri :String,
    val isPlayable:Boolean = true,
    val browserType :Long = MediaMetadata.BROWSABLE_TYPE_NONE
){
    @PrimaryKey(autoGenerate = true) var id :Int = 0
}