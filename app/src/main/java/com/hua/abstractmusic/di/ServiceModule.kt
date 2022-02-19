package com.hua.abstractmusic.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.ForwardingPlayer
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 基于服务周期的依赖注入
 */
@SuppressLint("UnsafeOptInUsageError")
@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {

    @Provides
    @ServiceScoped
    fun provideExoplayer(
        @ApplicationContext context: Context
    ): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setAudioAttributes(AudioAttributes.DEFAULT,true)
            .setHandleAudioBecomingNoisy(true)
            .build()
    }

    @Provides
    @ServiceScoped
    fun provideForwardingPlayer(
        player: ExoPlayer
    ): ForwardingPlayer {
        return ForwardingPlayer(player).apply {

        }
    }
}