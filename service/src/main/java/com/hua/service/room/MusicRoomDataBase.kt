package com.hua.service.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hua.model.music.LastMusicPO
import com.hua.model.sheet.SheetPO
import com.hua.model.sheet.SheetMusicPO
import com.hua.model.sheet.SheetToMusicPO
import com.hua.service.room.dao.MusicDao

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
@Database(
    entities = [SheetMusicPO::class,LastMusicPO::class, SheetPO::class, SheetToMusicPO::class],
    version = 1,
    exportSchema = false
)
abstract class MusicRoomDataBase:RoomDatabase() {
    abstract val dao: MusicDao
}