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
interface MusicRepository {
    /**
     * 检索所有类型的列表
     * @param parentId
     */
    suspend fun selectTypeList(parentId: Uri): ApiResult<List<MediaItem>>
    /**
     * 根据id去找音乐列表
     * @param parentId
     */
    suspend fun selectMusicByType(parentId: Uri):ApiResult<List<MediaItem>>
    /**
     * 检索歌曲的歌词
     * @param id
     */
    suspend fun selectLyrics(id:Uri?):String
    /**
     * 根据音乐的ID去检索歌手
     * @param item
     */
    suspend fun selectArtistByMusicId(item: MediaItem):ApiResult<List<MediaItem>>
    /**
     * 根据专辑ID去获取专辑
     * @param item
     */
    suspend fun selectAlbumByMusicId(item: MediaItem):ApiResult<MediaItem>
    /**
     * 检索歌手的专辑
     * @param parentId
     */
    suspend fun selectAlbumByArtist(parentId: Uri):ApiResult<List<MediaItem>>
    /**
     * 创建歌单
     * @param title
     */
    suspend fun createSheet(title:String):ApiResult<Unit>
    /**
     * 在歌单中插入音乐
     * @param sheetId 被插入的歌单id
     * @param mediaItem 歌曲
     */
    suspend fun insertMusicToSheet(sheetId: String, mediaItem: MediaItem):ApiResult<Unit>
    /**
     * 更新歌单
     * @param sheet
     */
    suspend fun updateSheet(sheet:SheetVO):ApiResult<Unit>
    /**
     * 通过歌单id检索歌单
     * @param parentId
     */
    suspend fun selectSheetById(parentId: Uri):ApiResult<MediaItem>
    /**
     * 根据音乐的ID去检索歌手
     * @param sheetId 歌单id
     * @param musicId 歌曲id
     */
    suspend fun removeSheetItem(sheetId: String, musicId: String):ApiResult<Unit>
    /**
     * 根据歌单ID删除歌单
     * @param sheetId
     */
    suspend fun deleteSheet(sheetId: String):ApiResult<Unit>
    /**
     * 检索用户歌单
     */
    suspend fun selectUserSheet():ApiResult<List<MediaItem>>

}