package com.gst.gusto.api.service

import com.gst.gusto.dto.ResponseInstaReview
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ReviewsApi {
    @GET("reviews/instaView") // 리뷰 모아보기 - 1 (insta view)
    fun instaView(
        @Header("X-AUTH-TOKEN") token: String,
        @Query("reviewId") reviewId: Long?,
        @Query("size") size: Int
    ): Call<ResponseInstaReview>
}