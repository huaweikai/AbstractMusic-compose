package com.hua.abstractmusic.net

import com.hua.abstractmusic.bean.net.NetAlbum
import com.hua.abstractmusic.bean.net.NetArtist
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.net.NetMusic
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author : huaweikai
 * @Date   : 2022/01/06
 * @Desc   : music
 */
interface MusicService {

    @GET("/album/list")
    suspend fun getAlbumList():NetData<List<NetAlbum>>

    @GET("/artist/list")
    suspend fun getArtistList():NetData<List<NetArtist>>

    @GET("/album/{id}/music")
    suspend fun getMusicByAlbum(@Path("id")id:String):NetData<List<NetMusic>>

    @GET("artist/{id}/music")
    suspend fun getMusicByArtist(@Path("id")id:String):NetData<List<NetMusic>>

    @GET("sheet/banner")
    suspend fun getBanner():NetData<List<NetAlbum>>

}