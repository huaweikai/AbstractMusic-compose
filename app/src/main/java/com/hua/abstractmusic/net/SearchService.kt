package com.hua.abstractmusic.net

import com.hua.abstractmusic.bean.net.*
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author : huaweikai
 * @Date   : 2022/03/17
 * @Desc   :
 */
interface SearchService {

    @GET("music/search/{name}")
    suspend fun searchMusic(
        @Path("name") name:String
    ):NetData<List<NetMusic>>

    @GET("album/search/{name}")
    suspend fun searchAlbum(
        @Path("name") name:String
    ):NetData<List<NetAlbum>>

    @GET("artist/search/{name}")
    suspend fun searchArtist(
        @Path("name") name:String
    ):NetData<List<NetArtist>>

    @GET("sheet/search/{name}")
    suspend fun searchSheet(
        @Path("name") name:String
    ):NetData<List<NetSheet>>
}