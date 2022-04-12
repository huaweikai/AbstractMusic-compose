package com.hua.network.client

import com.hua.network.calladapter.ApiResultCallAdapterFactory
import com.hua.network.converter.asConverterFactory
import com.hua.network.interceptor.BusinessErrorInterceptor
import com.hua.network.utils.globalJson
import kotlinx.serialization.ExperimentalSerializationApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

/**
 * @author Xiaoc
 * @since 2022-01-08
 *
 * Retrofit 客户端
 */

//const val BASE_URL = "http://119.3.175.64:8080"
////    const val BASE_URL = "http://192.168.123.199:8080"
//
//private val okHttpClient = OkHttpClient.Builder().apply {
//    addInterceptor(HttpLoggingInterceptor().apply {
//        level = HttpLoggingInterceptor.Level.BODY
//    })
//}.addInterceptor(BusinessErrorInterceptor())
//    .callTimeout(30, TimeUnit.SECONDS)
//    .readTimeout(30, TimeUnit.SECONDS)
//    .connectTimeout(30, TimeUnit.SECONDS)
//    .build()
//
//@OptIn(ExperimentalSerializationApi::class)
//val mRetrofit = Retrofit.Builder()
//    .addCallAdapterFactory(ApiResultCallAdapterFactory())
//    .addConverterFactory(globalJson.asConverterFactory("application/json".toMediaType()))
//    .baseUrl(BASE_URL)
//    .client(okHttpClient)
//    .build()