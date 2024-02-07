package com.gst.gusto.api

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.gst.gusto.BuildConfig
import com.gst.gusto.MainActivity
import com.gst.gusto.list.adapter.GroupItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GustoViewModel: ViewModel() {
    private val retrofit = Retrofit.Builder().baseUrl(BuildConfig.API_BASE)
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val service = retrofit.create(GustoApi::class.java)
    private var xAuthToken = ""
    private var refreshToken = ""

    // 자신의 루트 리스트 - (val title : String, val people : Int, val food : Int, val route : Int)
    val myRouteList = ArrayList<GroupItem>()

    // 리뷰 작성하기에서 위의 진행도 바
    var progress = 0

    // 리스트 화면에서 돌아온 화면 종류
    var listFragment = "group" // group or route
    var groupFragment = 0 // stores = 0 or routes = 1

    // 토큰 얻는 함수
    fun getTokens(activity: MainActivity) {
        xAuthToken = activity.getSharedPref().first
        refreshToken = activity.getSharedPref().second
    }


    // 내 루트 조회
    fun checkMyRoute(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.checkMyRoute(xAuthToken).enqueue(object : Callback<responseCheckMyRoute> {
            override fun onResponse(call: Call<responseCheckMyRoute>, response: Response<responseCheckMyRoute>) {
                if (response.isSuccessful) {
                    // 성공적이라면 일단 서버와의 연결에 성공 했다는 것!
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        for(data in responseBody.result.routes) {
                            myRouteList.add(GroupItem(data.routeName,0,data.numStore,0))
                        }
                    }
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<responseCheckMyRoute>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3)
            }
        })
    }

}