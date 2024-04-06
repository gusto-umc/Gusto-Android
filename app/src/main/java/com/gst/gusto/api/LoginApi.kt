package com.gst.gusto.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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

    @GET("users/check-nickname/{nickname}")
    fun checkNickname(
        @Path("nickname") nickname : String
    ) :Call<ResponseBody>

    @GET("users/confirm-nickname/{nickname}")
    fun confirmNickname(
        @Path("nickname") nickname : String
    ) :Call<ResponseBody>

}
