package com.hua.abstractmusic.db.music

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.hua.abstractmusic.bean.*

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : dao接口
 */
@Dao
interface MusicDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertIntoSheet(sheetMusic: SheetMusic)

    //根据歌单id检索音乐
    @Transaction
    @Query("select * from sheet where sheetId = :id")
    suspend fun selectMusic(id:Int):SheetListWithMusic

    //查找是否有相同的id，有的话不重复添加
    @Query("select musicId from sheettomusic where sheetId = :sheetId")
    suspend fun selectMusicIdBySheetId(sheetId: Int):List<String>

    //获取所有歌单
    @Query("select * from sheet")
    suspend fun selectLocalSheet(): List<Sheet>?

    @Query("select title from sheet")
    suspend fun selectLocalSheetTitle():List<String>?

    @Insert
    suspend fun insertSheetToMusic(sheetToMusic: SheetToMusic)

    @Insert(onConflict = REPLACE)
    suspend fun insertSheet(sheet: Sheet)

    @Query("select * from sheet where sheetId=:id")
    suspend fun selectSheetBySheetId(id:Int):Sheet

    //检索上次播放音乐的歌单 仅用于启动程序时检测
    @Query("select * from currentplayitem")
    suspend fun getLastPlayList():List<CurrentPlayItem>?

    //添加播放歌单到当前播放歌单数据库中
    @Insert
    suspend fun insertCurrentPlayItem(playItem: CurrentPlayItem)

    //删除播放歌单的所有音乐信息
    @Query("delete from currentplayitem")
    suspend fun deleteAllCurrentPlayItem()

    //删除歌单中的歌曲
    @Delete
    suspend fun removeSheetItem(sheetToMusic: SheetToMusic):Int

    @Query("delete from sheettomusic where sheetId =:sheetId and musicId =:musicId")
    suspend fun removeSheetItem(sheetId: Int,musicId:String):Int

    //删除歌单
    @Query("delete from sheet where sheetId = :sheetId")
    suspend fun deleteSheet(sheetId: String)
}