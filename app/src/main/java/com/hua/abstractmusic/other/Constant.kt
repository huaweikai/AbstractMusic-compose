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
    const val LOCAL = "local"
    const val TYPE_LOCAL_ALL = "${LOCAL}_all"
    const val TYPE_LOCAL_ALBUM = "${LOCAL}_album"
    const val TYPE_LOCAL_SHEET = "${LOCAL}_songSheet"
    const val ROOT_SCHEME = "playerDemo://"
    const val TYPE_LOCAL_LASTPLAY = "${LOCAL}_lastPlay"
    const val TYPE_LOCAL_ARTIST = "${LOCAL}_artist"
    const val ARTIST_TO_ALBUM = "abAlbum"
    const val ALL_ID = "${ROOT_SCHEME}${TYPE_LOCAL_ALL}"
    const val ALBUM_ID = "${ROOT_SCHEME}${TYPE_LOCAL_ALBUM}"

    //个人歌单
    const val SHEET_ID = "${ROOT_SCHEME}${TYPE_LOCAL_SHEET}"
    const val LASTPLAY_ID = "${ROOT_SCHEME}${TYPE_LOCAL_LASTPLAY}"
    const val ARTIST_ID = "${ROOT_SCHEME}${TYPE_LOCAL_ARTIST}"

    const val NETWORK = "network"

    //在线专辑
    const val TYPE_NETWORK_ALBUM = "${NETWORK}_album"
    const val NETWORK_ALBUM_ID = "${ROOT_SCHEME}$TYPE_NETWORK_ALBUM"

    //在线所有
    const val TYPE_NETWORK_ALL_MUSIC = "${NETWORK}_all_music"
    const val NETWORK_ALL_MUSIC_ID = "$ROOT_SCHEME$TYPE_NETWORK_ALL_MUSIC"

    //在线歌手
    const val TYPE_NETWORK_ARTIST = "${NETWORK}_artist"
    const val NETWORK_ARTIST_ID = "${ROOT_SCHEME}$TYPE_NETWORK_ARTIST"

    //banner
    const val TYPE_NETWORK_BANNER = "${NETWORK}_banner"
    const val NETWORK_BANNER_ID = "$ROOT_SCHEME$TYPE_NETWORK_BANNER"

    //recommend
    const val TYPE_NETWORK_RECOMMEND = "${NETWORK}_recommend"
    const val NETWORK_RECOMMEND_ID = "$ROOT_SCHEME$TYPE_NETWORK_RECOMMEND"


    const val TOALBUMDESC = "TOALBUMDESC"

    const val LASTMEDIA = "LASTMEDIA"
    const val LASTMEDIAINDEX = "LASTMEDIAINDEX"
    const val LASTMEDIAID = "LASTMEDIAID"
    const val MUSIC_ROOM_NAME = "MUSICDB"

    const val USER_ROOM_NAME = "MUSICDB"


    //    const val BASE_URL = "http://119.3.175.64:8080"
    const val BASE_URL = "http://192.168.123.199:8080"

    const val NOTIFICATION_CHANNEL_ID = "music_notification"
    const val NOTIFICATION_ID = 1

    val NULL_MEDIA_ITEM = MediaItem.Builder().setMetadata(
        MediaMetadata.Builder()
            .apply {
                title = "欢迎进入音乐的世界"
                artist = "暂无选中歌单"
                albumArtUri = ""
            }.build()
    ).build()
    const val CLEAR_PLAY_LIST = "clear_list"

    const val CURRENT_PLAY_LIST = "current_play_list"

    const val BUCKET_NAME = "abstractmusic"
    const val BUCKET_HEAD_IMG = "headImg"


    const val ALL_MUSIC_TYPE = "all_music_type"
    const val NET_ALBUM_TYPE = "net_album_type"
    const val NET_ARTIST_TYPE = "net_artist_type"
}