package com.gst.gusto.dto

import com.google.gson.annotations.SerializedName

data class ResponseRefreshToken(
    @SerializedName("xAuthToken") val xAuthToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)
