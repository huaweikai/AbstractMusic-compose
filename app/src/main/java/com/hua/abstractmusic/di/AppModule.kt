package com.hua.abstractmusic.di

import android.content.Context
import androidx.room.Room
import coil.request.ImageRequest
import com.hua.abstractmusic.db.MusicDao
import com.hua.abstractmusic.db.MusicRoomBase
import com.hua.abstractmusic.net.MusicService
import com.hua.abstractmusic.other.Constant.BASE_URL
import com.hua.abstractmusic.other.Constant.ROOM_NAME
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.repository.Repository
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.services.MediaStoreScanner
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.use_case.currentlist.ClearCurrentListCase
import com.hua.abstractmusic.use_case.currentlist.GetCurrentListCase
import com.hua.abstractmusic.use_case.currentlist.InsertMusicToCurrentItemCase
import com.hua.abstractmusic.use_case.net.SelectNetAlbumCase
import com.hua.abstractmusic.use_case.net.SelectNetArtistCase
import com.hua.abstractmusic.use_case.sheet.GetSheetMusicListCase
import com.hua.abstractmusic.use_case.sheet.GetSheetNameCase
import com.hua.abstractmusic.use_case.sheet.InsertSheetCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
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
    fun provideRoomDatabase(
        @ApplicationContext context: Context
    ):MusicRoomBase= Room.databaseBuilder(
        context,MusicRoomBase::class.java,ROOM_NAME
    ).build()

    @Provides
    @Singleton
    fun provideRoomDao(
        roomBase: MusicRoomBase
    ):MusicDao=roomBase.dao

    @Provides
    @Singleton
    fun provideRepository(
        dao: MusicDao
    ):Repository = Repository(dao)

    @Provides
    @Singleton
    fun provideNetRepository(
        service: MusicService
    ) = NetRepository(service)

    @Provides
    @Singleton
    fun provideUseCase(
        repository: Repository,
        netRepository: NetRepository
    ): UseCase =
        UseCase(
            InsertMusicToCurrentItemCase(repository),
            ClearCurrentListCase(repository),
            GetCurrentListCase(repository),
            GetSheetNameCase(repository),
            GetSheetMusicListCase(repository),
            InsertSheetCase(repository),
            SelectNetAlbumCase(netRepository),
            SelectNetArtistCase(netRepository)
        )

    @Provides
    @Singleton
    fun provideScanner(
        useCase: UseCase
    ):MediaStoreScanner =
        MediaStoreScanner(useCase)

    @Provides
    @Singleton
    fun provideItemTree(
        @ApplicationContext context: Context,
        scanner: MediaStoreScanner
    ):MediaItemTree =
        MediaItemTree(context, scanner)


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideService(
        retrofit: Retrofit
    ) = retrofit.create<MusicService>()

}