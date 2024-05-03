package com.gst.gusto.dto

import com.google.gson.annotations.SerializedName

// insta (gallery) review 조회- reviews & MyReviewFragment와 OtherReviewFragment
data class ResponseInstaReview(
    @SerializedName("reviewId") val reviewId: Long,
    @SerializedName("images") val image: String,
)

// insta (gallery) review 조회 & MyReviewFragment와 OtherReviewFragment
data class ResponseInstaReviews(
    @SerializedName("reviews") val reviews: List<ResponseInstaReview>,
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("cursorId") val cursorId: Long,
)
