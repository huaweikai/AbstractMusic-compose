package com.hua.abstractmusic.db.music

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mediasession2demo.ui.data.SongSheet
import com.hua.abstractmusic.bean.CurrentPlayItem
import com.hua.abstractmusic.bean.user.UserBean
import com.hua.abstractmusic.db.user.UserDao

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : Roombase
 */
@Database(
    entities = [SongSheet::class,CurrentPlayItem::class,UserBean::class],
    version = 1,
    exportSchema = false
)
abstract class MusicRoomBase :RoomDatabase(){
    abstract val dao: MusicDao
    abstract val userDao:UserDao
}