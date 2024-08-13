package com.gst.gusto.dto

import com.google.gson.annotations.SerializedName
import com.gst.gusto.model.MyPublishData

data class RequestMyPublish (
    @SerializedName("publishReview") val publishReview: Boolean,
    @SerializedName("publishPin") val publishPin: Boolean,
    @SerializedName("publishRoute") val publishRoute: Boolean,
)

fun MyPublishData.toRequestModel() = RequestMyPublish(
    publishReview = publishReview,
    publishPin = publishPin,
    publishRoute = publishRoute
)