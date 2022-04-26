package com.hua.network.di

import com.hua.network.api.MusicAPI
import com.hua.network.api.SearchApi
import com.hua.network.api.UserService
import com.hua.network.calladapter.ApiResultCallAdapterFactory
import com.hua.network.converter.asConverterFactory
import com.hua.network.interceptor.BusinessErrorInterceptor
import com.hua.network.utils.globalJson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @author : huaweikai
 * @Date   : 2022/04/09
 * @Desc   :
 */
const val BASE_URL = "http://119.3.175.64:8080"
//const val BASE_URL = "http://192.168.123.199:8080"
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideOkhttp() = OkHttpClient.Builder()
        .addInterceptor(BusinessErrorInterceptor())
        .callTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ) = Retrofit.Builder()
        .addCallAdapterFactory(ApiResultCallAdapterFactory())
        .addConverterFactory(globalJson.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideNetService(
        retrofit: Retrofit
    ) = retrofit.create<MusicAPI>()

    @Provides
    @Singleton
    fun provideUserService(
        retrofit: Retrofit
    ) = retrofit.create<UserService>()

    @Provides
    @Singleton
    fun provideSearchService(
        retrofit: Retrofit
    ) = retrofit.create<SearchApi>()
}