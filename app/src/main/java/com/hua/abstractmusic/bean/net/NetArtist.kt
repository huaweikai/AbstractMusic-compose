package com.hua.abstractmusic.bean.net

data class NetArtist(
    val code: Int,
    val `data`: List<Data>,
    val msg: String
){
    data class Data(
        val artistDesc: String,
        val id: Int,
        val imgUrl: String,
        val name: String
    )
}