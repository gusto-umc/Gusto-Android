package com.gst.gusto.api

import com.google.gson.annotations.SerializedName


// 내 루트 조회
data class responseCheckMyRoute(
    @SerializedName("success") val success : Boolean,
    @SerializedName("code") val code : Int,
    @SerializedName("message") val message : String,
    @SerializedName("result") val result : resultRoutes
)
data class resultRoutes(
    @SerializedName("routes") val routes : List<routes>
)
data class routes(
    @SerializedName("routeId") val routeId : Int,
    @SerializedName("routeName") val routeName : String,
    @SerializedName("numStore") val numStore : Int
)