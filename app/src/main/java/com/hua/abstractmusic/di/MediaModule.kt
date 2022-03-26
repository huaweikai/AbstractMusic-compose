package com.hua.abstractmusic.di

import android.content.ComponentName
import android.content.Context
import com.hua.abstractmusic.services.MediaConnect
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.services.PlayerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author : huaweikai
 * @Date   : 2022/03/25
 * @Desc   :
 */
@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @Singleton
    @Provides
    fun provideMediaConnect(
        @ApplicationContext context: Context,
        itemTree: MediaItemTree
    ) = MediaConnect(context, ComponentName(context,PlayerService::class.java), itemTree )
}