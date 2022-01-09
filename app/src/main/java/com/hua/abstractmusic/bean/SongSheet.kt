package com.example.mediasession2demo.ui.data

import androidx.media2.common.MediaMetadata
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author : huaweikai
 * @Date   : 2021/11/24
 * @Desc   : 自定义歌单数据类
 *
 */
@Entity
data class SongSheet (
    val sheetName:String,
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