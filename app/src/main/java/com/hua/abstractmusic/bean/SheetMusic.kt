package com.hua.abstractmusic.bean

import androidx.media3.common.MediaMetadata
import androidx.room.*

/**
 * @author : huaweikai
 * @Date   : 2021/11/24
 * @Desc   : 自定义歌单数据类
 *
 */
//@SuppressLint("UnsafeOptInUsageError")
@Entity
data class SheetMusic(
    @PrimaryKey val musicId: String,
    val title: String,
    val displayTitle: String,
    val displaySubtitle: String,
    val album: String,
    val artist: String,
    val trackerNumber: Int? = null,
    val mediaUri: String,
    val albumUri: String,
    val artistId:Long,
    val albumId:Long,
    val isPlayable: Boolean = true,
    val browserType: Int = MediaMetadata.FOLDER_TYPE_NONE
)

@Entity
data class Sheet(
    @PrimaryKey(autoGenerate = true) var sheetId:Int,
    val title:String,
    val artUri: String? = null,
    val desc:String? = null
)

@Entity(primaryKeys = ["sheetId","musicId"])
class SheetToMusic(
//    @PrimaryKey(autoGenerate = true) var id:Int,
    val sheetId: Int,
    @ColumnInfo(index = true)
    val musicId: String
)

data class SheetListWithMusic(
    @Embedded val sheet: Sheet,

    @Relation(
        parentColumn = "sheetId",
        entityColumn = "musicId",
        associateBy = Junction(SheetToMusic::class)
    )
    val music: List<SheetMusic>?
)

