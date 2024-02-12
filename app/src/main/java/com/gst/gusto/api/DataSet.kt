package com.gst.gusto.api

import com.google.gson.annotations.SerializedName


// 내 루트 조회
data class ResponseCheckMyRoute(
    @SerializedName("success") val success : Boolean,
    @SerializedName("code") val code : Int,
    @SerializedName("message") val message : String,
    @SerializedName("result") val result : ResultRoutes
)
data class ResultRoutes(
    @SerializedName("routes") val routes : List<Routes>
)
data class Routes(
    @SerializedName("routeId") val routeId : Int,
    @SerializedName("routeName") val routeName : String,
    @SerializedName("numStore") val numStore : Int
)

// 루트 생성
data class RequestCreateRoute(
    @SerializedName("routeName") val routeName : String,
    @SerializedName("groupId") val groupId : Long?,
    @SerializedName("routeList") val routeList : List<RouteList>
)
data class RouteList(
    @SerializedName("storeId") val storeId : Long,
    @SerializedName("ordinal") val ordinal : Int,
    // 루트 상세 조회
    @SerializedName("routeListId") val routeListId : Int,
    @SerializedName("storeName") val storeName : String,
    @SerializedName("address") val address : String,
    // 루트 지도 조회
    @SerializedName("longtitude") val longtitude : Double,
    @SerializedName("latitude") val latitude : Double
)
// 루트 상세 조회
data class ResponseRouteDetail(
    @SerializedName("routeName") val routeName : Int,
    @SerializedName("routes") val routes : List<RouteList>
)