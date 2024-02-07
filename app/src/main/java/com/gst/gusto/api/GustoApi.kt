package com.gst.gusto.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface GustoApi {


    // 내 루트 조회
    @GET("routes/")
    fun checkMyRoute(
        @Header("X-AUTH-TOKEN") token : String
    ) :Call<responseCheckMyRoute>

}
