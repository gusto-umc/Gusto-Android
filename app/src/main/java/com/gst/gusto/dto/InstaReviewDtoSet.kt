package com.gst.gusto.dto

import com.google.gson.annotations.SerializedName
import com.gst.gusto.model.InstaReview
import com.gst.gusto.model.InstaReviews

// insta (gallery) review 조회- reviews & MyReviewFragment와 OtherReviewFragment
data class ResponseInstaReview(
    @SerializedName("reviewId") val reviewId: Long,
    @SerializedName("images") val image: String?,
) {
    fun toDomainModel(): InstaReview {
        return InstaReview(
            reviewId = reviewId,
            image = image
        )
    }
}

// insta (gallery) review 조회 & MyReviewFragment와 OtherReviewFragment
data class ResponseInstaReviews(
    @SerializedName("reviews") val reviews: List<ResponseInstaReview>,
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("cursorId") val cursorId: Long,
) {
    fun toDomainModel(): InstaReviews {
        return InstaReviews(
            reviews = reviews.map { it.toDomainModel() },
            hasNext = hasNext,
            cursorId = cursorId
        )
    }
}
