package com.gst.gusto.dto

import com.gst.gusto.model.InstaReview
import com.gst.gusto.model.InstaReviews

data class ResponseFeedSearch(
    val cursorId: Long,
    val hasNext: Boolean,
    val reviews: List<Review>
) {
    fun toDomainModel(): InstaReviews {
        return InstaReviews(
            reviews = reviews.map { it.toDomainModel() },
            hasNext = hasNext,
            cursorId = cursorId
        )
    }
}

data class Review(
    val images: String,
    val reviewId: Long
) {
    fun toDomainModel(): InstaReview {
        return InstaReview(
            reviewId = reviewId,
            image = images
        )
    }
}
