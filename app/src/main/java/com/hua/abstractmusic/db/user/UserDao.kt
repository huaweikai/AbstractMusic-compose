package com.hua.abstractmusic.db.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hua.model.user.HistoryPO
import com.hua.model.user.UserPO
import kotlinx.coroutines.flow.Flow

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   :
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userBean: UserPO)

    @Query("delete from UserPO")
    suspend fun deleteUser()

//    @Query("select token from userBean")
//    suspend fun getToken():String

    @Query("select count(*) from UserPO")
    suspend fun userInRoom():Int

    @Query("select * from userpo")
    suspend fun getUserInfo():UserPO?

    @Insert
    suspend fun insertHistory(history: HistoryPO)

    @Query("delete from historypo where :id=id")
    suspend fun deleteHistory(id:Int)

    @Query("select * from historypo order by id desc")
    fun selectHistory(): Flow<List<HistoryPO>>

    @Query("select * from historypo")
    suspend fun selectHistoryList():List<HistoryPO>

    @Query("select * from UserPO")
    fun selectUser():Flow<UserPO>
}