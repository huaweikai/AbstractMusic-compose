package com.hua.abstractmusic.repository

import com.hua.abstractmusic.bean.*
import com.hua.abstractmusic.db.music.MusicDao

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 仓库类，用于连接viewmodel和本地数据库和数据
 */
class Repository(
    private val dao: MusicDao
) {

    suspend fun insertIntoSheet(sheetMusic: SheetMusic){
        dao.insertIntoSheet(sheetMusic)
    }

    suspend fun selectSheetName():List<String>?{
        return dao.selectLocalSheetTitle()
    }

    suspend fun insertSheet(sheet: Sheet){
        dao.insertSheet(sheet)
    }

    suspend fun selectMusicIdBySheetId(sheetId:Int):List<String>?{
        return dao.selectMusicIdBySheetId(sheetId)
    }

    suspend fun insertMusicToSheet(sheetToMusic: SheetToMusic){
        dao.insertSheetToMusic(sheetToMusic)
    }

    suspend fun selectMusicBySheetId(sheetId: Int):SheetListWithMusic{
        return dao.selectMusic(sheetId)
    }

    suspend fun selectSheets(): List<Sheet>? {
        return dao.selectLocalSheet()
    }

    suspend fun selectSheetBySheetId(id:Int):Sheet{
        return dao.selectSheetBySheetId(id)
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

    suspend fun removeSheetItem(sheetId: String,musicId:String):Int{
        return dao.removeSheetItem(sheetId.toInt(),musicId)
    }

    suspend fun removeSheet(sheetId:String){
        dao.deleteSheet(sheetId)
    }

    suspend fun updateSheetDesc(sheet: Sheet){
        dao.insertSheet(sheet)
    }
}