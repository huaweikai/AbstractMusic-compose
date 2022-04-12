package com.hua.model.artist

import kotlinx.serialization.Serializable

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
@Serializable
class ArtistVO(
    var id: Int,
    var name: String,
    var imgUrl: String,
    var artistDesc: String,
    var num:Int
)