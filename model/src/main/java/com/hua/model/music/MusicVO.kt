package com.hua.model.music

import kotlinx.serialization.Serializable

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
@Serializable
data class MusicVO(
    val albumId: Int,
    val albumName: String,
    val artist: String,
    val id: Int,
    val imgUrl: String,
    val musicUrl: String,
    val name: String
)