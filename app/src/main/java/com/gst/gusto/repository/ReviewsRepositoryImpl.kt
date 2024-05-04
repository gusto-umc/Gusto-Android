package com.gst.gusto.repository

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.datasource.ReviewsDataSource
import com.gst.gusto.model.InstaReviews

class ReviewsRepositoryImpl(
    private val reviewsDataSource: ReviewsDataSource
) : ReviewsRepository {
    override suspend fun getInstaReview(token: String, reviewId: Long?, size: Int): ApiResponse<out Any> {
        return when (val response = reviewsDataSource.getInstaViewReview(token, reviewId, size)) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomainModel())
            is ApiResponse.Error -> response
        }
    }
}