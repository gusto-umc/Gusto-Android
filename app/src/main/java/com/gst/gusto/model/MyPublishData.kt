package com.gst.gusto.model

import com.google.gson.annotations.SerializedName

data class MyPublishData (
    @SerializedName("publishReview") val publishReview: Boolean,
    @SerializedName("publishPin") val publishPin: Boolean,
    @SerializedName("publishRoute") val publishRoute: Boolean
)