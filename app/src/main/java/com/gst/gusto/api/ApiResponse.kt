package com.gst.gusto.api

sealed class ApiResponse<T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error<T>(val errorCode: Int, val errorMessage: String) : ApiResponse<T>()
}
