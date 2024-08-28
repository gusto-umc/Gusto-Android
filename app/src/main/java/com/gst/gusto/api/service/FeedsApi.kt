package com.gst.gusto.api.service

import com.gst.gusto.dto.ResponseFeedReview
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface FeedsApi {
    @GET("feeds") // 먹스또 랜덤 피드
    suspend fun getFeed(
        @Header("X-AUTH-TOKEN") token: String
    ): Response<List<ResponseFeedReview>>
}