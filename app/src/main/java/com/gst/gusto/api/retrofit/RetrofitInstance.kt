package com.gst.gusto.api.retrofit

import android.util.Log
import com.gst.gusto.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor { message ->
                Log.e("MyOkHttpClient:", message)
            }.setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    // Retrofit 인스턴스 싱글톤으로 관리
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 서비스 인스턴스를 반환하는 함수
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

}