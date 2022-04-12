package com.hua.abstractmusic.other

import android.annotation.SuppressLint
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata


/**
 * @author : huaweikai
 * @Date   : 2021/11/26
 * @Desc   : 常量类
 */
@SuppressLint("UnsafeOptInUsageError")
object Constant {



    const val TOALBUMDESC = "TOALBUMDESC"

    const val LAST_MEDIA = "LASTMEDIA"
    const val LAST_MEDIA_INDEX = "LASTMEDIAINDEX"

    const val THEME_COLOR = "THEMECOLOR"
    const val LASTMEDIAID = "LASTMEDIAID"
    const val MUSIC_ROOM_NAME = "MUSICDB"

    const val USER_ROOM_NAME = "USERDB"



    val NULL_MEDIA_ITEM = MediaItem.Builder()
        .setMediaId("0")
        .setMediaMetadata(MediaMetadata.EMPTY).build()
    const val CLEAR_PLAY_LIST = "clear_list"

    const val CURRENT_PLAY_LIST = "current_play_list"

    const val BUCKET_NAME = "abstractmusic"
    const val BUCKET_HEAD_IMG = "headImg"
    const val BUCKET_SHEET_IMG = "sheetImg"


    const val ALL_MUSIC_TYPE = "all_music_type"
    const val NET_ALBUM_TYPE = "net_album_type"
    const val NET_ARTIST_TYPE = "net_artist_type"
}