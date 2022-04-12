package com.hua.model.other

import android.net.Uri

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
object Constants {
    const val DURATION = "duration"
    const val ALBUM_ART_URI = "content://media/external/audio/albumart"
    const val LOCAL = "local"
    const val TYPE_LOCAL_ALL = "${LOCAL}_all"
    const val TYPE_LOCAL_ALBUM = "${LOCAL}_album"
    const val TYPE_LOCAL_SHEET = "${LOCAL}_songSheet"
    const val ROOT_SCHEME = "playerDemo://"
    const val TYPE_LOCAL_ARTIST = "${LOCAL}_artist"
    const val ARTIST_TO_ALBUM = "abAlbum"

    const val LOCAL_ALL_ID = "${ROOT_SCHEME}${TYPE_LOCAL_ALL}"
    const val LOCAL_ALBUM_ID = "${ROOT_SCHEME}${TYPE_LOCAL_ALBUM}"

    //本地个人歌单
    const val LOCAL_SHEET_ID = "${ROOT_SCHEME}${TYPE_LOCAL_SHEET}"


    const val LOCAL_ARTIST_ID = "${ROOT_SCHEME}${TYPE_LOCAL_ARTIST}"

    const val NETWORK = "network"


    //在线歌单
    const val TYPE_NETWORK_SHEET = "${NETWORK}_sheet"
    const val NET_SHEET_ID = "$ROOT_SCHEME$TYPE_NETWORK_SHEET"

    //在线专辑
    const val TYPE_NETWORK_ALBUM = "${NETWORK}_album"
    const val NETWORK_ALBUM_ID = "${ROOT_SCHEME}$TYPE_NETWORK_ALBUM"

    //在线所有
    const val TYPE_NETWORK_MUSIC = "${NETWORK}_all_music"
    const val NETWORK_MUSIC_ID = "$ROOT_SCHEME$TYPE_NETWORK_MUSIC"

    //在线歌手
    const val TYPE_NETWORK_ARTIST = "${NETWORK}_artist"
    const val NETWORK_ARTIST_ID = "${ROOT_SCHEME}$TYPE_NETWORK_ARTIST"

    //banner
    const val TYPE_NETWORK_BANNER = "${NETWORK}_banner"
    const val NETWORK_BANNER_ID = "$ROOT_SCHEME$TYPE_NETWORK_BANNER"

    //recommend
    const val TYPE_NETWORK_RECOMMEND = "${NETWORK}_recommend"
    const val NETWORK_RECOMMEND_ID = "$ROOT_SCHEME$TYPE_NETWORK_RECOMMEND"

    val bannerId = Uri.parse(NETWORK_BANNER_ID)
    val songId = Uri.parse(NETWORK_MUSIC_ID)
    val sheetId = Uri.parse(NET_SHEET_ID)
    val albumId = Uri.parse(NETWORK_ALBUM_ID)

    val homeList = listOf(
        NETWORK_BANNER_ID, NETWORK_MUSIC_ID, NET_SHEET_ID, NETWORK_ALBUM_ID
    )
}