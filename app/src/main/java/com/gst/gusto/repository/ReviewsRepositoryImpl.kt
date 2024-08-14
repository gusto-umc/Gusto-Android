package com.gst.gusto.repository

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.datasource.ReviewsDataSource
import com.gst.gusto.model.InstaReviews
import com.gst.gusto.model.TimeLineReviews

class ReviewsRepositoryImpl(
    private val reviewsDataSource: ReviewsDataSource
) : ReviewsRepository {
    override suspend fun getInstaReview(
        token: String,
        reviewId: Long?,
        size: Int
    ): ApiResponse<InstaReviews> {
        return when (val response = reviewsDataSource.getInstaViewReview(token, reviewId, size)) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomainModel())
            is ApiResponse.Error -> ApiResponse.Error(response.errorCode, response.errorMessage)
        }
    }

    override suspend fun getOtherInstaReview(
        token: String,
        nickname: String,
        reviewId: Long?,
        size: Int
    ): ApiResponse<InstaReviews> {
        return when (val response = reviewsDataSource.getOtherInstaViewReview(token, nickname, reviewId, size)) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomainModel())
            is ApiResponse.Error -> ApiResponse.Error(response.errorCode, response.errorMessage)
        }
    }

    override suspend fun getTimeLineReview(
        token: String,
        reviewId: Long?,
        size: Int
    ): ApiResponse<TimeLineReviews> {
        return when (val response = reviewsDataSource.getTimeLineReview(token, reviewId, size)) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomainModel())
            is ApiResponse.Error -> ApiResponse.Error(response.errorCode, response.errorMessage)
        }
    }


}