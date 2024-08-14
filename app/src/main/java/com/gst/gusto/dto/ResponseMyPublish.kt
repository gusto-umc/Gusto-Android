package com.gst.gusto.dto

import com.google.gson.annotations.SerializedName
import com.gst.gusto.model.MyPublishData

data class ResponseMyPublish(
    @SerializedName("publishReview") val publishReview: Boolean,
    @SerializedName("publishPin") val publishPin: Boolean,
    @SerializedName("publishRoute") val publishRoute: Boolean
){
    fun toDomainModel(): MyPublishData{
        return MyPublishData(
            publishReview = publishReview,
            publishPin = publishPin,
            publishRoute = publishRoute
        )
    }
}
