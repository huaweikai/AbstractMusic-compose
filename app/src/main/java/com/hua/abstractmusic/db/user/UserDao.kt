package com.hua.abstractmusic.db.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hua.abstractmusic.bean.user.SearchHistory
import com.hua.abstractmusic.bean.user.UserBean
import kotlinx.coroutines.flow.Flow

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   :
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userBean: UserBean)

    @Query("delete from userBean")
    suspend fun deleteUser()

    @Query("select token from userBean")
    suspend fun getToken():String

    @Query("select count(*) from userBean")
    suspend fun userInRoom():Int

    @Query("select * from userbean")
    suspend fun getUserInfo():UserBean?

    @Insert
    suspend fun insertHistory(history: SearchHistory)

    @Query("select * from searchhistory")
    fun selectHistory(): Flow<List<SearchHistory>>
}