package com.gst.gusto.api.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/reissue-token") // 현재 지역의 카테고리 별 찜한 가게 목록(필터링)
    suspend fun refreshToken(
        @Header("X-AUTH-TOKEN") access : String,
        @Header("refresh-Token") refresh : String
    ): Response<ResponseBody>
}