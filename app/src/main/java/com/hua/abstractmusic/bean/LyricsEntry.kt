package com.hua.abstractmusic.bean

/**
 * @author : huaweikai
 * @Date   : 2022/02/25
 * @Desc   :
 */
data class LyricsEntry(
    var isPlaying: Boolean = false,
    val time: Long?,
    val main: String,
//    val lyrics :String
)