package com.hua.model.album

import kotlinx.serialization.Serializable

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
@Serializable
data class AlbumVO(
    val albumDesc: String,
    val artistId: Int,
    val id: Int,
    val imgUrl: String,
    val name: String,
    val time: String,
    val artistName: String,
    val num:Int,
)