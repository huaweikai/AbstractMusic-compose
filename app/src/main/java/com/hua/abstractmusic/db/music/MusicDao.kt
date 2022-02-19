package com.hua.abstractmusic.db.music

import androidx.room.*
import com.hua.abstractmusic.bean.SongSheet
import com.hua.abstractmusic.bean.CurrentPlayItem

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : dao接口
 */
@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntoSheet(songSheet: SongSheet)

    @Delete
    suspend fun deleteOutSheet(songSheet: SongSheet)

    //删除歌单
    @Query("delete from songsheet where :sheetName=sheetName")
    suspend fun deleteSheet(sheetName:String)

    //通过歌单名搜索音乐
    @Query("select * from songsheet where :sheetName=sheetName")
    fun getSheet(sheetName:String):List<SongSheet>?

    //查找是否有相同的id，有的话不重复添加
    @Query("select musicId from songsheet where :sheetName=sheetName")
    fun getSongIdBySheetName(sheetName: String):List<String>

    //获取歌单名
    @Query("select sheetName from songsheet")
    fun getSheetName():List<String>?

    //检索上次播放音乐的歌单 仅用于启动程序时检测
    @Query("select * from currentplayitem")
    suspend fun getLastPlayList():List<CurrentPlayItem>?

    //添加播放歌单到当前播放歌单数据库中
    @Insert
    suspend fun insertCurrentPlayItem(playItem: CurrentPlayItem)

    //删除播放歌单的所有音乐信息
    @Query("delete from currentplayitem")
    suspend fun deleteAllCurrentPlayItem()
}