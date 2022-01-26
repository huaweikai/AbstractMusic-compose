package com.hua.abstractmusic.db.user

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hua.abstractmusic.bean.user.UserBean

/**
 * @author : huaweikai
 * @Date   : 2022/01/24
 * @Desc   :
 */
@Database(
    entities = [UserBean::class],
    version = 1,
    exportSchema = false
)
abstract class UserRoomBase:RoomDatabase() {
    abstract val userDao:UserDao
}