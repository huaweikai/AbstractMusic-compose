package com.hua.service.di

import com.hua.service.preference.*
import com.tencent.mmkv.MMKV
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author : huaweikai
 * @Date   : 2022/03/11
 * @Desc   :
 */
@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {
    @Provides
    @Singleton
    fun provideMMKV() = MMKV.defaultMMKV() ?: throw RuntimeException("Can't get default mmkv")

    @Provides
    @Singleton
    fun providePreferenceManager(
        mmkv: MMKV
    ) = PreferenceManager(
        MediaIndexPreference(mmkv),
        ThemeColorPreference(mmkv),
        UserTokenPreference(mmkv),
        RepeatModePreference(mmkv),
        ShuffleModePreference(mmkv),
        MediaPositionPreference(mmkv)
    )
}