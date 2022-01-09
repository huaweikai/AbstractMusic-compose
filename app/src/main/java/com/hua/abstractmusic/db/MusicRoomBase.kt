package com.hua.abstractmusic.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mediasession2demo.ui.data.SongSheet
import com.hua.abstractmusic.bean.CurrentPlayItem

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : Roombase
 */
@Database(
    entities = [SongSheet::class,CurrentPlayItem::class],
    version = 1,
    exportSchema = false
)
abstract class MusicRoomBase :RoomDatabase(){
    abstract val dao:MusicDao
}