package com.hua.abstractmusic.net

import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.user.NetUser
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author : huaweikai
 * @Date   : 2022/01/25
 * @Desc   : service
 */
interface UserService {

    @GET("/user/emailCode/register")
    suspend fun getEmailCode(
        @Query("email") email: String
    ): NetData<Unit>

    @GET("/user/register")
    suspend fun register(
        @Query("email") email: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("code") code: Int
    ): NetData<String>

    @GET("/user/code/login")
    suspend fun loginWithCode(
        @Query("email") email: String,
        @Query("code") code: Int
    ): NetData<String>

    @GET("/user/email/login")
    suspend fun loginWithEmail(
        @Query("email") email: String,
        @Query("password") password: String
    ): NetData<String>

    @GET("/user/testToken")
    suspend fun testToken(
        @Query("token")token:String
    ):NetData<Unit>

    @GET("/user/getInfo")
    suspend fun getUser(
        @Query("token")token:String
    ):NetData<NetUser>

    @GET("/user/logout")
    suspend fun logoutUser(
        @Query("token")token: String
    ):NetData<Unit>

    @GET("/user/emailCode/login")
    suspend fun getEmailCodeWithLogin(
        @Query("email")email: String
    ):NetData<Unit>

    @POST("/user/setInfo")
    suspend fun setUser(
        @Query("token")token:String,
        @Body netUser: NetUser
    ):NetData<Unit>
}