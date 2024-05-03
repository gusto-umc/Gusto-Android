package com.gst.gusto.repository

import com.gst.gusto.model.InstaReviews

interface ReviewsRepository {
    suspend fun getInstaReview(token: String, reviewId: Long?, size: Int): InstaReviews
}