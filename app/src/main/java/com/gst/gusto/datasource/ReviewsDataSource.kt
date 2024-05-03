package com.gst.gusto.datasource

import com.gst.gusto.api.service.ReviewsApi
import com.gst.gusto.dto.ResponseInstaReviews
import retrofit2.await

class ReviewsDataSource(
    private val reviewsService: ReviewsApi
) {

    suspend fun getInstaViewReview(token: String, reviewId: Long?, size: Int): ResponseInstaReviews {
        return reviewsService.instaView(token, reviewId, size)
            .await()
    }
}