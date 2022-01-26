package com.hua.abstractmusic.repository

import com.example.mediasession2demo.ui.data.SongSheet
import com.hua.abstractmusic.db.music.MusicDao
import com.hua.abstractmusic.bean.CurrentPlayItem

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 仓库类，用于连接viewmodel和本地数据库和数据
 */
class Repository(
    private val dao: MusicDao
) {

    suspend fun insertIntoSheet(songSheet: SongSheet){
        dao.insertIntoSheet(songSheet)
    }

    suspend fun deleteMusicOutSheet(songSheet: SongSheet){
        dao.deleteOutSheet(songSheet)
    }

    suspend fun deleteSheet(sheetName:String){
        dao.deleteSheet(sheetName)
    }

    fun getSheet(sheetName:String):List<SongSheet>?{
        return dao.getSheet(sheetName)
    }

    fun getSongIdBySheetName(sheetName: String):List<String>{
        return dao.getSongIdBySheetName(sheetName)
    }

    fun getSheetName():List<String>?{
        return dao.getSheetName()
    }

    suspend fun getLastPlayList():List<CurrentPlayItem>?{
        return dao.getLastPlayList()
    }

    suspend fun insertIntoCurrentPlayList(playItem: CurrentPlayItem){
        dao.insertCurrentPlayItem(playItem)
    }

    suspend fun clearCurrentPlayItems(){
        dao.deleteAllCurrentPlayItem()
    }
}