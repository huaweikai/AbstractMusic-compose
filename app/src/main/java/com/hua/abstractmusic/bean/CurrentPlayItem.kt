package com.hua.abstractmusic.bean

import androidx.media3.common.MediaMetadata
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author : huaweikai
 * @Date   : 2021/11/24
 * @Desc   : 使用数据库来保存当前的播放列表
 *
 */
@androidx.media3.common.util.UnstableApi
@Entity
data class CurrentPlayItem(
    val musicId:String,
    val title:String,
    val displayTitle :String,
    val displaySubtitle :String,
    val album :String,
    val artist:String,
    val trackerNumber:Long,
    val mediaUri :String,
    val albumUri :String,
    val isPlayable:Boolean = true,
    val browserType :Int = MediaMetadata.FOLDER_TYPE_NONE
){
    @PrimaryKey(autoGenerate = true) var id :Int = 0
}