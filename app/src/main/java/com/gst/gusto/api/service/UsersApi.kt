package com.gst.gusto.api.service

import com.gst.gusto.dto.ResponseGetMyPublish
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface UsersApi {
    @GET("users/my-info/publishing")
    suspend fun getMyPublish(
        @Header("X-AUTH-TOKEN") token: String
    ): Response<ResponseGetMyPublish>
}