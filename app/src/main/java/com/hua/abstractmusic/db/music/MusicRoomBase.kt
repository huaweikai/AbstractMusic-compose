package com.hua.abstractmusic.db.music

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hua.abstractmusic.bean.CurrentPlayItem
import com.hua.abstractmusic.bean.Sheet
import com.hua.abstractmusic.bean.SheetMusic
import com.hua.abstractmusic.bean.SheetToMusic

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : Roombase
 */
@Database(
    entities = [SheetMusic::class,CurrentPlayItem::class,Sheet::class,SheetToMusic::class],
    version = 1,
    exportSchema = false
)
abstract class MusicRoomBase :RoomDatabase(){
    abstract val dao: MusicDao
}