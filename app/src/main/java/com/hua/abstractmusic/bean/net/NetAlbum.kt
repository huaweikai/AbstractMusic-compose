package com.hua.abstractmusic.bean.net

//data class NetAlbum(
//    val code: Int,
//    val `data`: List<Data>,
//    val msg: String
//) {
//    data class Data(
//        val albumDesc: String,
//        val artistId: Int,
//        val id: Int,
//        val imgUrl: String,
//        val name: String,
//        val time: String,
//        val artistName: String
//    )
//}
data class NetAlbum(
    val albumDesc: String,
    val artistId: Int,
    val id: Int,
    val imgUrl: String,
    val name: String,
    val time: String,
    val artistName: String
)