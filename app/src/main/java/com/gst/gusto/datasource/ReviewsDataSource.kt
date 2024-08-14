package com.gst.gusto.datasource

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.api.service.ReviewsApi
import com.gst.gusto.dto.ResponseInstaReviews
import com.gst.gusto.dto.ResponseTimeLineReviews

class ReviewsDataSource(
    private val reviewsService: ReviewsApi
) {
    suspend fun getInstaViewReview(
        token: String,
        reviewId: Long?,
        size: Int
    ): ApiResponse<ResponseInstaReviews> {
        val response = reviewsService.getInstaView(token, reviewId, size)
        return if (response.isSuccessful) {
            response.body()?.let {
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(),"Response body is null + ${response.message()}")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }

    suspend fun getOtherInstaViewReview(
        token: String,
        nickname: String,
        reviewId: Long?,
        size: Int
    ): ApiResponse<ResponseInstaReviews> {
        val response = reviewsService.getOtherInstaView(token, nickname, reviewId, size)
        return if (response.isSuccessful) {
            response.body()?.let {
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(),"Response body is null + ${response.message()}")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }

    suspend fun getTimeLineReview(
        token: String,
        reviewId: Long?,
        size: Int
    ): ApiResponse<ResponseTimeLineReviews> {
        val response = reviewsService.getTimelineView(token, reviewId, size)
        return if (response.isSuccessful){
            response.body()?.let {
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(),"Response body is null + ${response.message()}")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }
}
