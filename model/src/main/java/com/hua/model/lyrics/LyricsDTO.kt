package com.hua.model.lyrics


/**
 * @author : huaweikai
 * @Date   : 2022/04/11
 * @Desc   :
 */
data class LyricsDTO(
    var isPlaying: Boolean = false,
    val time: Long?,
    val main: String,
)