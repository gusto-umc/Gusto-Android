package com.gst.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File


interface LoginRetrofit {


    @POST("users/sign-up")
    fun signUp(
        @Header("Temp-Token") tempToken: String,
        @Body info: signData
    ): Call<String>

    data class signData(
        @SerializedName("profileImg") val profileImg : File?,
        @SerializedName("info") val info : info
    )
    data class info(
        @SerializedName("nickname") val nickname : String,
        @SerializedName("age") val age: String,
        @SerializedName("gender") val gender: String,
        @SerializedName("profileImg") val profileImg: String?
    )
}