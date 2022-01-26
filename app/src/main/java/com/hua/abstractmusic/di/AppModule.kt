package com.hua.abstractmusic.di

import android.content.Context
import androidx.room.Room
import com.hua.abstractmusic.db.music.MusicDao
import com.hua.abstractmusic.db.music.MusicRoomBase
import com.hua.abstractmusic.db.user.UserDao
import com.hua.abstractmusic.db.user.UserRoomBase
import com.hua.abstractmusic.net.MusicService
import com.hua.abstractmusic.net.UserService
import com.hua.abstractmusic.other.Constant.BASE_URL
import com.hua.abstractmusic.other.Constant.MUSIC_ROOM_NAME
import com.hua.abstractmusic.other.Constant.USER_ROOM_NAME
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.repository.Repository
import com.hua.abstractmusic.repository.UserRepository
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
import com.hua.abstractmusic.use_case.user.UserLoginCase
import com.hua.abstractmusic.use_case.user.UserRegisterCase
import com.hua.abstractmusic.use_case.user.UserTokenOut
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
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
    fun provideMusicRoomDatabase(
        @ApplicationContext context: Context
    ): MusicRoomBase = Room.databaseBuilder(
        context, MusicRoomBase::class.java, MUSIC_ROOM_NAME
    ).build()

    @Provides
    @Singleton
    fun provideMusicRoomDao(
        roomBase: MusicRoomBase
    ): MusicDao = roomBase.dao

//    @Provides
//    @Singleton
//    fun provideUserRoomDatabase(
//        @ApplicationContext context: Context
//    ): UserRoomBase = Room.databaseBuilder(
//        context, UserRoomBase::class.java, USER_ROOM_NAME
//    ).build()

    @Provides
    @Singleton
    fun provideUserRoomDao(
        musicRoomBase: MusicRoomBase
    ): UserDao = musicRoomBase.userDao

    @Provides
    @Singleton
    fun provideRepository(
        dao: MusicDao
    ): Repository = Repository(dao)

    @Provides
    @Singleton
    fun provideNetRepository(
        service: MusicService
    ) = NetRepository(service)

    @Provides
    @Singleton
    fun provideUserRepository(
        service: UserService,
        dao: UserDao
    ) = UserRepository(service,dao)

    @Provides
    @Singleton
    fun provideUseCase(
        repository: Repository,
        netRepository: NetRepository,
        userRepository: UserRepository,
        dao: UserDao
    ): UseCase =
        UseCase(
            InsertMusicToCurrentItemCase(repository),
            ClearCurrentListCase(repository),
            GetCurrentListCase(repository),
            GetSheetNameCase(repository),
            GetSheetMusicListCase(repository),
            InsertSheetCase(repository),
            SelectNetAlbumCase(netRepository),
            SelectNetArtistCase(netRepository),
            UserRegisterCase(userRepository),
            UserTokenOut(userRepository),
            UserLoginCase(userRepository, dao)
        )

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
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideNetService(
        retrofit: Retrofit
    ) = retrofit.create<MusicService>()

    @Provides
    @Singleton
    fun provideUserService(
        retrofit: Retrofit
    ) = retrofit.create<UserService>()

}