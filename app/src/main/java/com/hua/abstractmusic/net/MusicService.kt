package com.hua.abstractmusic.net

import com.hua.abstractmusic.bean.net.*
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author : huaweikai
 * @Date   : 2022/01/06
 * @Desc   : music
 */
interface MusicService {

    @GET("album/list")
    suspend fun getAlbumList(): NetData<List<NetAlbum>>

    @GET("artist/list")
    suspend fun getArtistList(): NetData<List<NetArtist>>

    @GET("album/{id}/music")
    suspend fun getMusicByAlbum(@Path("id") id: String): NetData<List<NetMusic>>

    @GET("artist/{id}/music")
    suspend fun getMusicByArtist(@Path("id") id: String): NetData<List<NetMusic>>

    @GET("sheet/banner")
    suspend fun getBanner(): NetData<List<NetAlbum>>

    @GET("sheet/banner/{id}/list")
    suspend fun getBannerById(@Path("id")id:String):NetData<List<NetMusic>>

    @GET("sheet/recommend")
    suspend fun getRecommend(): NetData<List<NetSheet>>

    @GET("music/list")
    suspend fun getAllMusic():NetData<List<NetMusic>>

    @GET("sheet/recommend/{id}/list")
    suspend fun getRecommendList(
        @Path("id")id:String
    ):NetData<List<NetMusic>>

    @GET("music/{id}/lyrics")
    suspend fun getMusicLyrics(
        @Path("id") id :String
    ):NetData<String>

}