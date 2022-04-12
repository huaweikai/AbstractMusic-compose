package com.hua.abstractmusic.base.repository

import android.net.Uri
import androidx.media3.common.MediaItem
import com.hua.model.sheet.SheetVO
import com.hua.network.ApiResult

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
interface BaseRepository {
    //检索所有类型的列表
    suspend fun selectTypeList(parentId: Uri): ApiResult<List<MediaItem>>
    //根据id去找音乐列表
    suspend fun selectMusicByType(parentId: Uri):ApiResult<List<MediaItem>>
    //检索歌词
    suspend fun selectLyrics(id:Uri?):String
    //根据音乐的ID去检索歌手
    suspend fun selectArtistByMusicId(item: MediaItem):ApiResult<List<MediaItem>>
    //根据专辑ID去获取专辑
    suspend fun selectAlbumByMusicId(item: MediaItem):ApiResult<MediaItem>
    //检索歌手的专辑
    suspend fun selectAlbumByArtist(parentId: Uri):ApiResult<List<MediaItem>>
    //创建歌单
    suspend fun createSheet(title:String):ApiResult<Unit>
    //在歌单中插入音乐
    suspend fun insertMusicToSheet(sheetId: String, mediaItem: MediaItem):ApiResult<Unit>
    //更新歌单
    suspend fun updateSheet(sheet:SheetVO):ApiResult<Unit>
    //通过歌单id检索歌单
    suspend fun selectSheetById(parentId: Uri):ApiResult<MediaItem>
    //移除歌单中的音乐
    suspend fun removeSheetItem(sheetId: String, musicId: String):ApiResult<Unit>
    //删除歌单
    suspend fun deleteSheet(sheetId: String):ApiResult<Unit>
    //检索用户歌单
    suspend fun selectUserSheet():ApiResult<List<MediaItem>>

}