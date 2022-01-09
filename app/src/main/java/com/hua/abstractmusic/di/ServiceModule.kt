package com.hua.abstractmusic.di

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Singleton

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 基于服务周期的依赖注入
 */
@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {

    @Provides
    @ServiceScoped
    fun provideExoplayer(
        @ApplicationContext context: Context
    ):ExoPlayer{
        return ExoPlayer.Builder(context)
            .setAudioAttributes(AudioAttributes.DEFAULT,true)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }
}