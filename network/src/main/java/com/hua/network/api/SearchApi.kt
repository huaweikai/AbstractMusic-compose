package com.hua.network.api

import com.hua.model.album.AlbumVO
import com.hua.model.artist.ArtistVO
import com.hua.model.music.MusicVO
import com.hua.model.sheet.SheetVO
import com.hua.network.ApiResult
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author : huaweikai
 * @Date   : 2022/03/17
 * @Desc   :
 */
interface SearchApi {

    @GET("music/search/{name}")
    suspend fun searchMusic(
        @Path("name") name:String
    ):ApiResult<List<MusicVO>>

    @GET("album/search/{name}")
    suspend fun searchAlbum(
        @Path("name") name:String
    ):ApiResult<List<AlbumVO>>

    @GET("artist/search/{name}")
    suspend fun searchArtist(
        @Path("name") name:String
    ):ApiResult<List<ArtistVO>>

    @GET("sheet/search/{name}")
    suspend fun searchSheet(
        @Path("name") name:String
    ):ApiResult<List<SheetVO>>
}