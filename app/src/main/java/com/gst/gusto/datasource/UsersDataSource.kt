package com.gst.gusto.datasource

import com.gst.gusto.api.ApiResponse
import com.gst.gusto.api.service.UsersApi
import com.gst.gusto.dto.RequestMyPublish
import com.gst.gusto.dto.ResponseMyProfile
import com.gst.gusto.dto.ResponseMyPublish
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.http.Header
import retrofit2.http.Path

class UsersDataSource(
    private val usersService: UsersApi
) {
    suspend fun getMyPublish(
        token: String
    ): ApiResponse<ResponseMyPublish> {
        val response = usersService.getMyPublish(token)
        return if(response.isSuccessful){
            response.body()?.let{
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(), "Response body is null + ${response.message()}")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }

    suspend fun setMyPublish(
        token: String,
        myPublish: RequestMyPublish
    ): ApiResponse<ResponseBody> {
        val response = usersService.setMyPublish(token, myPublish)
        return if(response.isSuccessful){
            response.body()?.let{
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(), "Response body is null + ${response.message()}")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }

    suspend fun getMyProfile(
        token: String
    ): ApiResponse<ResponseMyProfile> {
        val response = usersService.getMyProfile(token)
        return if(response.isSuccessful){
            response.body()?.let{
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(), "Response body is null + ${response.message()}" )
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }

    suspend fun getCheckNickname(
        token: String,
        nickname: String
    ): ApiResponse<ResponseBody> {
        val response = usersService.getCheckNickname(token, nickname)
        return if(response.isSuccessful){
            response.body()?.let{
                ApiResponse.Success(it)
            } ?: ApiResponse.Error(response.code(), "Response body is null + ${response.message()}")
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }

    suspend fun setMyProfileImg(
        token: String,
        profileImg: MultipartBody.Part
    ): ApiResponse<ResponseBody> {
        val response = usersService.setMyProfileImg(token, profileImg)
        return if(response.isSuccessful){
            val responseBody = response.body() ?: "".toResponseBody(null)
            ApiResponse.Success(responseBody)
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }

    suspend fun setMyProfileInfo(
        token: String,
        setting: RequestBody
    ): ApiResponse<ResponseBody> {
        val response = usersService.setMyProfileInfo(token, setting)
        return if(response.isSuccessful){
            val responseBody = response.body() ?: "".toResponseBody(null)
            ApiResponse.Success(responseBody)
        } else {
            ApiResponse.Error(response.code(), response.message())
        }
    }
}