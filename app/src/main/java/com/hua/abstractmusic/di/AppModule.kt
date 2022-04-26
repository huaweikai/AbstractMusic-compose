package com.hua.abstractmusic.di

import android.content.Context
import android.graphics.BitmapFactory
import coil.ImageLoader
import com.hua.abstractmusic.R
import com.hua.abstractmusic.db.user.UserDao
import com.hua.network.api.SearchApi
import com.hua.network.api.UserService
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.repository.LocalRepository
import com.hua.abstractmusic.repository.NetWorkRepository
import com.hua.abstractmusic.repository.UserRepository
import com.hua.service.usecase.currentlist.ClearCurrentListCase
import com.hua.service.usecase.currentlist.InsertMusicToCurrentItemCase
import com.hua.service.usecase.sheet.InsertSheetCase
import com.hua.abstractmusic.utils.ComposeUtils
import com.hua.abstractmusic.utils.KEY
import com.hua.abstractmusic.utils.UpLoadFile
import com.hua.blur.BlurLibrary
import com.hua.network.api.MusicAPI
import com.hua.service.MediaConnect
import com.hua.service.MediaItemTree
import com.hua.service.MediaStoreScanner
import com.hua.service.room.dao.MusicDao
import com.hua.service.usecase.UseCase
import com.hua.taglib.TaglibLibrary
import com.obs.services.ObsClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : 全局单例，依赖注入
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        service: UserService,
        dao: UserDao,
        musicDao: MusicDao,
        userInfoData: UserInfoData,
        upLoadFile: UpLoadFile
    ) = UserRepository(service, dao, musicDao, userInfoData, upLoadFile)


    @Provides
    @Singleton
    fun provideScanner(
        useCase: UseCase
    ): MediaStoreScanner =
        MediaStoreScanner(useCase)

    @Provides
    @Singleton
    fun provideItemTree(
        @ApplicationContext context: Context,
        scanner: MediaStoreScanner
    ): MediaItemTree =
        MediaItemTree(context, scanner)

    @Provides
    @Singleton
    fun provideObsClient() = ObsClient(
        KEY.AccessKeyId,
        KEY.SecretAccessKey,
        "obs.cn-north-4.myhuaweicloud.com"
    )

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ) = ImageLoader(context)

    @Provides
    @Singleton
    fun provideMediaTaglib() = TaglibLibrary()


    @Provides
    @Singleton
    fun provideBlurLib() = BlurLibrary()

    @Provides
    @Singleton
    fun provideLocalRepository(
        dao: MusicDao,
        mediaConnect: MediaConnect,
        taglibLibrary: TaglibLibrary
    ): LocalRepository = LocalRepository(mediaConnect, taglibLibrary, dao)

    @Provides
    @Singleton
    fun provideNetRepository(
        service: MusicAPI,
        searchApi: SearchApi,
        userInfoData: UserInfoData,
        itemTree: MediaItemTree,
    ) = NetWorkRepository(service, searchApi, itemTree, userInfoData)

    @Singleton
    @Provides
    @Named("ErrorBitmap")
    fun providerErrorBitmap(
        @ApplicationContext context: Context
    ) = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.music
    )
}