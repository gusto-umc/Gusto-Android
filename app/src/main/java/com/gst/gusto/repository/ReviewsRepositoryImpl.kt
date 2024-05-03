package com.gst.gusto.repository

import com.gst.gusto.datasource.ReviewsDataSource
import com.gst.gusto.model.InstaReviews

class ReviewsRepositoryImpl(
    private val reviewsDataSource: ReviewsDataSource
) : ReviewsRepository {
    override suspend fun getInstaReview(token: String, reviewId: Long?, size: Int): InstaReviews {
        return reviewsDataSource
            .getInstaViewReview(token, reviewId, size)
            .toDomainModel()
    }
}