package com.hua.abstractmusic.db.user

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hua.model.user.HistoryPO
import com.hua.model.user.UserPO

/**
 * @author : huaweikai
 * @Date   : 2022/01/24
 * @Desc   :
 */
@Database(
    entities = [UserPO::class,HistoryPO::class],
    version = 1,
    exportSchema = false
)
abstract class UserRoomBase:RoomDatabase() {
    abstract val userDao:UserDao
}