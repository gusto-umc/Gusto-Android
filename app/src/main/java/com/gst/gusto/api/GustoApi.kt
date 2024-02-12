package com.gst.gusto.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface GustoApi {

    @GET("routes/") // 내 루트 조회
    fun checkMyRoute(
        @Header("X-AUTH-TOKEN") token : String
    ):Call<ResponseCheckMyRoute>

    @POST("routes/") // 루트 생성 or 그룹 내 루트 추가
    fun createRoute(
        @Header("X-AUTH-TOKEN") token : String,
        @Body requestBody : RequestCreateRoute
    ):Call<ResponseBody>
    @GET("routeLists/{routeId}/order") // 내 or 그룹 루트 거리 조회(공통)
    fun checkRouteMap(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long
    ):Call<List<RouteList>>

    @GET("routeLists/{routeId}") // 내 루트 상세 조회
    fun checkRouteDetail(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long
    ):Call<ResponseRouteDetail>
    @DELETE("routes/{routeId}") // 내 루트 삭제
    fun deleteRoute(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long
    ):Call<ResponseBody>
    @DELETE("routeLists/{routeListId}") // 루트 내 식당 삭제
    fun deleteRouteStore(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeListId") routeListId : Int
    ):Call<ResponseBody>

}
