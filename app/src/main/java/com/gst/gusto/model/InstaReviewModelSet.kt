package com.gst.gusto.model

data class InstaReview(
    val reviewId: Long,
    val image: String?
)

data class InstaReviews(
    val reviews: List<InstaReview>,
    val hasNext: Boolean,
    val cursorId: Long
)