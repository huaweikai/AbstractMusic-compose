package com.hua.abstractmusic.other

import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import com.hua.abstractmusic.utils.albumArtUri
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title

/**
 * @author : huaweikai
 * @Date   : 2021/11/26
 * @Desc   : 常量类
 */
object Constant {
    const val DURATION = "duration"
    const val ALBUM_ART_URI = "content://media/external/audio/albumart"
    const val TYPE_ROOT = "root"
    const val TYPE_ALBUM = "album"
    const val TYPE_SHEET ="songSheet"
    const val ROOT_SCHEME = "playerDemo://"
    const val TYPE_LASTPLAY="lastPlay"
    const val TYPE_ARTIST = "artist"
    const val ALL_ID = "${ROOT_SCHEME}${TYPE_ROOT}"
    const val ALBUM_ID = "${ROOT_SCHEME}${TYPE_ALBUM}"
    //个人歌单
    const val SHEET_ID="${ROOT_SCHEME}${TYPE_SHEET}"
    const val LASTPLAY_ID = "${ROOT_SCHEME}${TYPE_LASTPLAY}"
    const val ARTIST_ID = "${ROOT_SCHEME}${TYPE_ARTIST}"

    //在线专辑
    const val TYPE_NETWORK_ALBUM = "network_album"
    const val NETWORK_ALBUM_ID = "${ROOT_SCHEME}$TYPE_NETWORK_ALBUM"

    //在线歌手
    const val TYPE_NETWORK_ARTIST = "network_artist"
    const val NETWORK_ARTIST_ID = "${ROOT_SCHEME}$TYPE_NETWORK_ARTIST"


    const val TOALBUMDESC = "TOALBUMDESC"

    const val LASTMEDIA = "LASTMEDIA"
    const val LASTMEDIAINDEX = "LASTMEDIAINDEX"
    const val LASTMEDIAID = "LASTMEDIAID"
    const val ROOM_NAME = "MUSICDB"


    const val BASE_URL = "http://192.168.123.199:8080"

    const val NOTIFICATION_CHANNEL_ID = "music_notification"
    const val NOTIFICATION_ID = 1

    val NULL_MEDIA_ITEM = MediaItem.Builder().setMetadata(
        MediaMetadata.Builder()
            .apply {
                title = "欢迎进入音乐的世界"
                artist = "暂无选中歌单"
                albumArtUri = ""
            }.build()).build()
    const val CLEAR_PLAY_LIST = "clear_list"
}