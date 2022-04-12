package com.hua.network.api

import com.hua.model.album.AlbumVO
import com.hua.model.artist.ArtistVO
import com.hua.model.music.MusicVO
import com.hua.model.sheet.SheetVO
import com.hua.network.ApiResult
import retrofit2.http.*

/**
 * @author : huaweikai
 * @Date   : 2022/04/11
 * @Desc   :
 */
interface MusicAPI {
    //专辑列表
    @GET("album/list")
    suspend fun getAlbumList(): ApiResult<List<AlbumVO>>

    //专辑列表
    @GET("album/recommend/list")
    suspend fun getRecommendAlbumList(): ApiResult<List<AlbumVO>>

    //歌手列表
    @GET("artist/list")
    suspend fun getArtistList(): ApiResult<List<ArtistVO>>

    //某音乐的歌手列表
    @GET("artist/{musicId}/list")
    suspend fun getArtistByMusicId(@Path("musicId")musicId: String): ApiResult<List<ArtistVO>>

    //某音乐的专辑
    @GET("artist/{musicId}/list")
    suspend fun getAlbumByMusicId(@Path("musicId")musicId: String): ApiResult<List<ArtistVO>>

    //专辑的音乐
    @GET("album/{id}/music")
    suspend fun getMusicByAlbum(@Path("id") id: String): ApiResult<List<MusicVO>>

    //歌手的音乐
    @GET("artist/{id}/music")
    suspend fun getMusicByArtist(@Path("id") id: String): ApiResult<List<MusicVO>>

    //歌手的音乐
    @GET("artist/{id}/album")
    suspend fun getAlbumByArtist(@Path("id") id: String): ApiResult<List<AlbumVO>>

    //主页banner
    @GET("sheet/banner")
    suspend fun getBanner(): ApiResult<List<AlbumVO>>

    //推荐歌单
    @GET("sheet/recommend")
    suspend fun getRecommend(): ApiResult<List<SheetVO>>

    //所有在线音乐
    @GET("music/list")
    suspend fun getAllMusic(): ApiResult<List<MusicVO>>

    //歌单的具体音乐列表
    @GET("sheet/{id}/list")
    suspend fun getMusicBySheetId(
        @Path("id") id: String
    ): ApiResult<List<MusicVO>>

    //音乐的歌词
    @GET("music/{id}/lyrics")
    suspend fun getMusicLyrics(
        @Path("id") id: String
    ): ApiResult<String>

    //用户歌单
    @GET("sheet/userSheet")
    suspend fun getUserSheet(
        @Query("token") token: String
    ): ApiResult<List<SheetVO>>

    //在歌单中添加音乐
    @GET("sheet/addSheet")
    suspend fun insertMusicToSheet(
        @Query("sheetId") sheetId: String,
        @Query("musicId") musicId: String,
        @Query("token") token: String
    ): ApiResult<Unit>

    //创建歌单
    @GET("sheet/createSheet")
    suspend fun createNewSheet(
        @Query("title") title: String,
        @Query("token") token: String
    ): ApiResult<Unit>

    //从歌单中删除某首歌
    @GET("sheet/delete/{sheetId}/{musicId}")
    suspend fun deleteMusicFromSheet(
        @Path("sheetId") sheetId: String,
        @Path("musicId") musicId: String,
        @Query("token") token: String
    ): ApiResult<Unit>

    //删除歌单
    @GET("sheet/delete/{sheetId}")
    suspend fun deleteSheet(
        @Path("sheetId") sheetId: String,
        @Query("token") token: String
    ): ApiResult<Unit>

    //更新sheet
    @POST("sheet/update")
    suspend fun updateSheet(
        @Query("token")token: String,
        @Body sheet: SheetVO
    ):ApiResult<Unit>

    @GET("sheet/{id}")
    suspend fun selectSheetById(
        @Path("id") id: String
    ):ApiResult<SheetVO>

    @GET("album/{id}")
    suspend fun selectAlbumById(
        @Path("id") id: String
    ):ApiResult<AlbumVO>

    @GET("artist/{id}")
    suspend fun selectArtistById(
        @Path("id") id: String
    ):ApiResult<ArtistVO>
}