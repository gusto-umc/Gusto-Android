package com.gst.gusto.datasource

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.api.service.ReviewsApi
import com.gst.gusto.dto.ResponseInstaReviews
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.await

class ReviewsDataSource(
    private val reviewsService: ReviewsApi
) {
    suspend fun getInstaViewReview(token: String, reviewId: Long?, size: Int): ApiResponse<ResponseInstaReviews> {
        val response = reviewsService.instaView(token, reviewId, size)
        return if (response.isSuccessful) {
            response.body()?.let {
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(),"Response body is null")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }
}
