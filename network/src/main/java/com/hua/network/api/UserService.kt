package com.hua.network.api

import com.hua.model.user.UserVO
import com.hua.network.ApiResult
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
    ): ApiResult<String>

    @GET("/user/register")
    suspend fun register(
        @Query("email") email: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("code") code: Int
    ): ApiResult<String>

    @GET("/user/code/login")
    suspend fun loginWithCode(
        @Query("email") email: String,
        @Query("code") code: Int
    ): ApiResult<String>

    @GET("/user/email/login")
    suspend fun loginWithEmail(
        @Query("email") email: String,
        @Query("password") password: String
    ): ApiResult<String>

    @GET("/user/testToken")
    suspend fun testToken(
        @Query("token")token:String
    ):ApiResult<Unit>

    @GET("/user/getInfo")
    suspend fun getUser(
        @Query("token")token:String
    ):ApiResult<UserVO>

    @GET("/user/logout")
    suspend fun logoutUser(
        @Query("token")token: String
    ):ApiResult<String>

    @GET("/user/emailCode/login")
    suspend fun getEmailCodeWithLogin(
        @Query("email")email: String
    ):ApiResult<String>

    @POST("/user/setInfo")
    suspend fun setUser(
        @Query("token")token:String,
        @Body netUser: UserVO
    ):ApiResult<Unit>

    @GET("/user/delete")
    suspend fun deleteUser(
        @Query("token")token:String
    ):ApiResult<Unit>
}