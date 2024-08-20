package com.gst.gusto.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface LoginApi {


    @Multipart
    @POST("users/sign-up")
    fun signUp(
        @Part profileImg: MultipartBody.Part?,
        @Part("info") info: Singup
    ): Call<ResponseBody>

    // 소셜 로그인
    @POST("users/sign-in")
    fun login(
        @Body body : Login
    ) : Call<ResponseBody>

    @POST("users/random-nickname")
    fun randomNickname(
    ) : Call<Nickname>

    @GET("users/check-nickname/{nickname}")
    fun checkNickname(
        @Path("nickname") nickname : String
    ) :Call<ResponseBody>

    @POST("users/confirm-nickname/{nickname}")
    fun confirmNickname(
        @Path("nickname") nickname : String
    ) :Call<ResponseBody>

    @POST("token")
    @FormUrlEncoded
    fun getAccessToken(
        @Field("grant_type") grantType: String,
        @Field("code") code: String?,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String
    ): Call<AccessTokenResponse>
}
