package com.hua.abstractmusic.bean.net

data class NetMusic(
    val code: Int,
    val `data`: List<Data>,
    val msg: String
){
    data class Data(
        val albumId: Int,
        val albumName: String,
        val artist: String,
        val id: Int,
        val imgUrl: String,
        val musicUrl: String,
        val name: String
    )
}