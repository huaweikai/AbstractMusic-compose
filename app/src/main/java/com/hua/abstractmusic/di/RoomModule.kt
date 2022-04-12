package com.hua.abstractmusic.di

import android.content.Context
import androidx.room.Room
import com.hua.abstractmusic.db.user.UserDao
import com.hua.abstractmusic.db.user.UserRoomBase
import com.hua.abstractmusic.other.Constant.USER_ROOM_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideUserRoomDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context, UserRoomBase::class.java, USER_ROOM_NAME
    ).build()

    @Provides
    @Singleton
    fun provideUserRoomDao(
        userRoomBase: UserRoomBase
    ): UserDao = userRoomBase.userDao
}