package com.gst.gusto.dto

import com.google.gson.annotations.SerializedName
import com.gst.gusto.model.InstaReview

data class ResponseFeedReview(
    @SerializedName("reviewId") val reviewId: Long,
    @SerializedName("images") val image: String,
) {
    fun toDomainModel(): InstaReview {
        return InstaReview(
            reviewId = reviewId,
            image = image
        )
    }
}

