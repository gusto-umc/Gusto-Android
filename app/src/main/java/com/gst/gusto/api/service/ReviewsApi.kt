package com.gst.gusto.api.service

import com.gst.gusto.dto.ResponseInstaReview
import com.gst.gusto.dto.ResponseInstaReviews
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ReviewsApi {
    @GET("reviews/instaView") // 리뷰 모아보기 - 1 (insta view)
    suspend fun instaView(
        @Header("X-AUTH-TOKEN") token: String,
        @Query("reviewId") reviewId: Long?,
        @Query("size") size: Int
    ): Response<ResponseInstaReviews>

    @GET("reviews") // 타인 리뷰 모아보기
    suspend fun otherInstaView(
        @Header("X-AUTH-TOKEN") token: String,
        @Query("nickName") nickname: String,
        @Query("reviewId") reviewId: Long?,
        @Query("size") size: Int
    ): Response<ResponseInstaReviews>
}