package com.hua.service.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import com.hua.service.room.MusicRoomDataBase
import com.hua.service.room.dao.MusicDao
import com.hua.service.usecase.UseCase
import com.hua.service.usecase.currentlist.ClearCurrentListCase
import com.hua.service.usecase.currentlist.InsertMusicToCurrentItemCase
import com.hua.service.usecase.currentlist.SelectCurrentListCase
import com.hua.service.usecase.sheet.InsertSheetCase
import com.hua.service.usecase.sheet.SelectInfoBySheet
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
class ServiceModule {

    @SuppressLint("UnsafeOptInUsageError")
    @Provides
    @Singleton
    fun provideExoplayer(
        @ApplicationContext context: Context
    ): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setAudioAttributes(AudioAttributes.DEFAULT,true)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideMusicRoomDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context,MusicRoomDataBase::class.java,"music_db.db")
        .build()

    @Provides
    @Singleton
    fun provideMusicDao(
        musicRoomDataBase: MusicRoomDataBase
    ) = musicRoomDataBase.dao

    @Provides
    @Singleton
    fun provideUseCase(
        dao: MusicDao
    ) = UseCase(
        InsertSheetCase(dao),
        SelectInfoBySheet(dao),
        ClearCurrentListCase(dao),
        SelectCurrentListCase(dao),
        InsertMusicToCurrentItemCase(dao)
    )
}