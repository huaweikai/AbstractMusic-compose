package com.hua.service.room.dao

import androidx.room.*
import com.hua.model.music.LastMusicPO
import com.hua.model.sheet.SheetPO
import com.hua.model.sheet.SheetListWithMusic
import com.hua.model.sheet.SheetMusicPO
import com.hua.model.sheet.SheetToMusicPO

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntoSheet(sheetMusicPO: SheetMusicPO)

    //根据歌单id检索音乐
    @Transaction
    @Query("select * from sheetpo where sheetId = :id")
    suspend fun selectMusic(id:Int): SheetListWithMusic

    //查找是否有相同的id，有的话不重复添加
    @Query("select musicId from sheettomusicpo where sheetId = :sheetId")
    suspend fun selectMusicIdBySheetId(sheetId: Int):List<String>?

    //获取所有歌单
    @Query("select * from sheetpo")
    suspend fun selectLocalSheet(): List<SheetPO>?

    @Query("select title from sheetpo")
    suspend fun selectLocalSheetTitle():List<String>?

    @Insert
    suspend fun insertMusicToSheet(sheetToMusicPO: SheetToMusicPO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSheet(sheetPO: SheetPO)

    @Query("select * from sheetpo where sheetId=:id")
    suspend fun selectSheetBySheetId(id:Int): SheetPO

    //检索上次播放音乐的歌单 仅用于启动程序时检测
    @Query("select * from lastmusicpo")
    suspend fun getLastPlayList():List<LastMusicPO>?

    //添加播放歌单到当前播放歌单数据库中
    @Insert
    suspend fun insertCurrentPlayItem(playItem: LastMusicPO)

    //删除播放歌单的所有音乐信息
    @Query("delete from lastmusicpo")
    suspend fun clearCurrentList()

    @Delete
    suspend fun removeSheetItem(sheetToMusicPO: SheetToMusicPO):Int

    //删除歌单中的歌曲
    @Query("delete from sheettomusicpo where sheetId =:sheetId and musicId =:musicId")
    suspend fun removeSheetMusicItem(sheetId: Int,musicId:String):Int

    //删除歌单
    @Query("delete from sheetpo where sheetId = :sheetId")
    suspend fun deleteSheet(sheetId: String)
}