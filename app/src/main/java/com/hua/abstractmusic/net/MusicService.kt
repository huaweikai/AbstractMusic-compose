package com.hua.abstractmusic.net

import com.hua.abstractmusic.bean.net.*
import retrofit2.http.*

/**
 * @author : huaweikai
 * @Date   : 2022/01/06
 * @Desc   : music
 */
interface MusicService {

    //专辑列表
    @GET("album/list")
    suspend fun getAlbumList(): NetData<List<NetAlbum>>

    //歌手列表
    @GET("artist/list")
    suspend fun getArtistList(): NetData<List<NetArtist>>

    //某音乐的歌手列表
    @GET("artist/{musicId}/list")
    suspend fun getArtistByMusicId(@Path("musicId")musicId: String): NetData<List<NetArtist>>

    //专辑的音乐
    @GET("album/{id}/music")
    suspend fun getMusicByAlbum(@Path("id") id: String): NetData<List<NetMusic>>

    //歌手的音乐
    @GET("artist/{id}/music")
    suspend fun getMusicByArtist(@Path("id") id: String): NetData<List<NetMusic>>

    //歌手的音乐
    @GET("artist/{id}/album")
    suspend fun getAlbumByArtist(@Path("id") id: String): NetData<List<NetAlbum>>

    //主页banner
    @GET("sheet/banner")
    suspend fun getBanner(): NetData<List<NetAlbum>>

    //具体的banner音乐
    @GET("sheet/banner/{id}/list")
    suspend fun getBannerById(@Path("id") id: String): NetData<List<NetMusic>>

    //推荐歌单
    @GET("sheet/recommend")
    suspend fun getRecommend(): NetData<List<NetSheet>>

    //所有在线音乐
    @GET("music/list")
    suspend fun getAllMusic(): NetData<List<NetMusic>>

    //歌单的具体音乐列表
    @GET("sheet/{id}/list")
    suspend fun getMusicBySheetId(
        @Path("id") id: String
    ): NetData<List<NetMusic>>

    //音乐的歌词
    @GET("music/{id}/lyrics")
    suspend fun getMusicLyrics(
        @Path("id") id: String
    ): NetData<String>

    //用户歌单
    @GET("sheet/userSheet")
    suspend fun getUserSheet(
        @Query("token") token: String
    ): NetData<List<NetSheet>>

    //在歌单中添加音乐
    @GET("sheet/addSheet")
    suspend fun insertMusicToSheet(
        @Query("sheetId") sheetId: String,
        @Query("musicId") musicId: String,
        @Query("token") token: String
    ): NetData<Unit>

    //创建歌单
    @GET("sheet/createSheet")
    suspend fun createNewSheet(
        @Query("title") title: String,
        @Query("token") token: String
    ): NetData<Unit>

    //从歌单中删除某首歌
    @GET("sheet/delete/{sheetId}/{musicId}")
    suspend fun deleteMusicFromSheet(
        @Path("sheetId") sheetId: String,
        @Path("musicId") musicId: String,
        @Query("token") token: String
    ): NetData<Unit>

    //删除歌单
    @GET("sheet/delete/{sheetId}")
    suspend fun deleteSheet(
        @Path("sheetId") sheetId: String,
        @Query("token") token: String
    ): NetData<Unit>

    //更新sheet
    @POST("sheet/update")
    suspend fun updateSheet(
        @Query("token")token: String,
        @Body sheet: NetSheet
    ):NetData<Unit>

    @GET("sheet/{id}/detail")
    suspend fun selectSheetById(
        @Path("id") id: String
    ):NetData<NetSheet>
}