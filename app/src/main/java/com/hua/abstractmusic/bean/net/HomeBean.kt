package com.hua.abstractmusic.bean.net

import androidx.media3.common.MediaItem

/**
 * @author : huaweikai
 * @Date   : 2022/03/25
 * @Desc   :
 */
data class HomeBean(
    val banners: List<MediaItem>? = emptyList(),
    val songs: List<MediaItem>? = emptyList(),
    val sheets: List<MediaItem>? = emptyList(),
    val albums: List<MediaItem>? = emptyList()
) {
    fun isEmpty(): Boolean {
        return banners.isNullOrEmpty() && songs.isNullOrEmpty() && sheets.isNullOrEmpty() && albums.isNullOrEmpty()
    }
}

