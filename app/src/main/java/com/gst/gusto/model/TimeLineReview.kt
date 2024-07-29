package com.gst.gusto.model

import com.gst.gusto.review.adapter.ListReviewType

data class TimeLineReviews(
    val reviews: List<TimeLineReview>,
    val hasNext: Boolean,
    val cursorId: Long
)

data class TimeLineReview(
    val date: String,
    val name: String,
    val visit: String,
    val images : List<String>,
    val reviewId: Long,
    var viewType: Int = ListReviewType.LISTREVIEW,
    )
