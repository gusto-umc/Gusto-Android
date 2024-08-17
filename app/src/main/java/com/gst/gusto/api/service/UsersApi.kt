package com.gst.gusto.api.service

import com.gst.gusto.dto.RequestMyPublish
import com.gst.gusto.dto.ResponseMyProfile
import com.gst.gusto.dto.ResponseMyPublish
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH

interface UsersApi {
    @GET("users/my-info/publishing")
    suspend fun getMyPublish(
        @Header("X-AUTH-TOKEN") token: String
    ): Response<ResponseMyPublish>

    @PATCH("users/my-info/publishing")
    suspend fun setMyPublish(
        @Header("X-AUTH-TOKEN") token: String,
        @Body data: RequestMyPublish
    ): Response<ResponseBody>

    @GET("users/my-info")
    suspend fun getMyProfile(
        @Header("X-AUTH-TOKEN") token: String
    ): Response<ResponseMyProfile>
}