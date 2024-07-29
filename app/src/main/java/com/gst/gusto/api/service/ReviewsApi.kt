package com.gst.gusto.api.service

import com.gst.gusto.dto.ResponseInstaReviews
import com.gst.gusto.dto.ResponseTimeLineReviews
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ReviewsApi {
    @GET("reviews/instaView") // 리뷰 모아보기 - 1 (insta view)
    suspend fun getInstaView(
        @Header("X-AUTH-TOKEN") token: String,
        @Query("reviewId") reviewId: Long?,
        @Query("size") size: Int
    ): Response<ResponseInstaReviews>

    @GET("reviews") // 타인 리뷰 모아보기
    suspend fun getOtherInstaView(
        @Header("X-AUTH-TOKEN") token: String,
        @Query("nickName") nickname: String,
        @Query("reviewId") reviewId: Long?,
        @Query("size") size: Int
    ): Response<ResponseInstaReviews>

    @GET("reviews/timelineView") // 리뷰 모아보기-3 (timeline view)
    suspend fun getTimelineView(
        @Header("X-AUTH-TOKEN") token: String,
        @Query("reviewId") reviewId: Long?,
        @Query("size") size: Int
    ): Response<ResponseTimeLineReviews>
}