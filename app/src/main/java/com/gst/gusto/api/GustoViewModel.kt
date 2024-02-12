package com.gst.gusto.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gst.gusto.BuildConfig
import com.gst.gusto.MainActivity
import com.gst.gusto.Util.mapUtil
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.RestItem
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
    // 자신의 그룹 리스트
    val myGroupList = ArrayList<GroupItem>()
    // 현재 지도에 보이는 마커 리스트 - (val id : Int, val num : Int, val latitude : Double, val longitude : Double, val name : String, val loc : String, val bookMark : Boolean)
    val markerListLiveData: MutableLiveData<ArrayList<mapUtil.Companion.MarkerItem>> = MutableLiveData()
    // 그룹 내에 식당 리스트
    val storeListLiveData: MutableLiveData<ArrayList<RestItem>> = MutableLiveData()

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
    fun getMyRoute(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.getMyRoute(xAuthToken).enqueue(object : Callback<List<Routes>> {
            override fun onResponse(call: Call<List<Routes>>, response: Response<List<Routes>>) {
                if (response.isSuccessful) {
                    // 성공적이라면 일단 서버와의 연결에 성공 했다는 것!
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.e("viewmodel", "Successful response: ${response}")
                        for(data in responseBody) {
                            myRouteList.add(GroupItem(data.routeId, data.routeName, 0, data.numStore, 0))
                        }
                    }
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<List<Routes>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3)
            }
        })
    }
    // 루트 생성 or 그룹 내 루트 추가
    fun createRoute(createRoute : RequestCreateRoute,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.createRoute(xAuthToken, createRoute).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3)
            }
        })
    }
    // 내 or 그룹 루트 거리 조회(공통)
    fun getRouteMap(routeId : Long,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.getRouteMap(xAuthToken, routeId).enqueue(object : Callback<List<RouteList>> {
            override fun onResponse(call: Call<List<RouteList>>, response: Response<List<RouteList>>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    val responseBody = response.body()
                    if(responseBody !=null) {
                        markerListLiveData.value?.clear()
                        for(data in responseBody) {
                            val tmp = markerListLiveData.value?.get(data.ordinal)
                            if (tmp != null) {
                                tmp.longitude = data.longtitude
                                tmp.latitude = data.latitude
                            }
                        }
                    }

                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<List<RouteList>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }
    // 내 루트 상세 조회
    fun getRouteDetail(routeId : Long,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.getRouteDetail(xAuthToken, routeId).enqueue(object : Callback<ResponseRouteDetail> {
            override fun onResponse(call: Call<ResponseRouteDetail>, response: Response<ResponseRouteDetail>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    val responseBody = response.body()
                    if(responseBody !=null) {
                        for(data in responseBody.routes) {
                            markerListLiveData.value?.add(
                                mapUtil.Companion.MarkerItem(
                                    data.storeId,
                                    data.ordinal,
                                    data.routeListId,
                                    0.0,
                                    0.0,
                                    data.storeName,
                                    data.address,
                                    false
                                )
                            )
                        }
                        markerListLiveData.value?.let { list ->
                            // ordinal 속성을 기준으로 리스트를 정렬
                            list.sortBy { it.ordinal }
                        }
                    }
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<ResponseRouteDetail>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }
    // 내 루트 삭제
    fun deleteRoute(routeId : Long,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.deleteRoute(xAuthToken, routeId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }
    // 루트 내 식당 삭제
    fun deleteRouteStore(routeListId : Int,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.deleteRouteStore(xAuthToken, routeListId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }
    // 그룹 리스트 조회
    fun getGroups(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.getGroups(xAuthToken).enqueue(object : Callback<List<ResponseGetGroups>> {
            override fun onResponse(call: Call<List<ResponseGetGroups>>, response: Response<List<ResponseGetGroups>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.e("viewmodel", "Successful response: ${response}")
                        for(data in responseBody) {
                            myRouteList.add(GroupItem(data.groupId,data.groupName,data.numMembers,data.numRestaurants,data.numRoutes))
                        }
                    }
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<List<ResponseGetGroups>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3)
            }
        })
    }
    // 그룹 내 식당 목록 조회
    fun getGroupStores(groupId : Long, callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.getGroupStores(xAuthToken,groupId).enqueue(object : Callback<List<ResponseStore>> {
            override fun onResponse(call: Call<List<ResponseStore>>, response: Response<List<ResponseStore>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        storeListLiveData.value?.clear()
                        Log.e("viewmodel", "Successful response: ${response}")
                        for(data in responseBody) {
                            storeListLiveData.value?.add(RestItem(data.storeName,data.address,data.storeProfileImg,data.userProfileImg,data.storeId,data.groupListId))
                        }
                    }
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<List<ResponseStore>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3)
            }
        })
    }
    // 그룹 내 루트 목록 조회
    fun getGroupRoutes(groupId : Long, callback: (Int,ArrayList<GroupItem>?) -> Unit){
        Log.e("token",xAuthToken)
        service.getGroupRoutes(xAuthToken,groupId).enqueue(object : Callback<List<Routes>> {
            override fun onResponse(call: Call<List<Routes>>, response: Response<List<Routes>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.e("viewmodel", "Successful response: ${response}")
                        val tmpRouteList = ArrayList<GroupItem>()
                        for(data in responseBody) {
                            tmpRouteList.add(GroupItem(data.routeId, data.routeName, 0, data.numStore, 0))
                        }
                        callback(1,tmpRouteList)
                    } else callback(2,null)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3,null)
                }
            }
            override fun onFailure(call: Call<List<Routes>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3,null)
            }
        })
    }
    // 그룹 내 식당 추가
    fun addGroupStore(groupId : Long,storeId : Long, callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.addGroupStore(xAuthToken,groupId,StoredId(storeId)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }
    // 그룹 1건 조회
    fun getGroup(groupId : Long, callback: (Int, ResponseGroup?) -> Unit){
        Log.e("token",xAuthToken)
        service.getGroup(xAuthToken,groupId).enqueue(object : Callback<ResponseGroup> {
            override fun onResponse(call: Call<ResponseGroup>, response: Response<ResponseGroup>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1, response.body()!!)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2,null)
                }
            }
            override fun onFailure(call: Call<ResponseGroup>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2,null)
            }
        })
    }

    // 그룹 세부정보 수정
    fun editGroupOption(groupId : Long,groupName: String,notice: String, callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.editGroupOption(xAuthToken,groupId, RequestGroupOption(groupName,notice)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code()==402) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }
    // 그룹 및 초대코드 생성
    fun createGroup(groupName: String,groupScript: String, callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.createGroup(xAuthToken, RequestCreateGroup(groupName,groupScript)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }
    // 그룹 삭제
    fun deleteGroup(groupId: Long, callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.deleteGroup(xAuthToken,groupId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }
    // 그룹 소유권 이전
    fun transferOwnership(groupId: Long, newOwner: Int, callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.transferOwnership(xAuthToken,groupId, NewOwner(newOwner)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }
    // 그룹 탈퇴
    fun leaveGroup(groupId: Long,  callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.leaveGroup(xAuthToken,groupId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }
    // 그룹 참여
    fun joinGroup(groupId: Long, invitationCode : String ,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.joinGroup(xAuthToken,groupId, RequestJoinGroup(groupId,invitationCode)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }
    // 그룹 구성원 조회
    fun getGroupMembers(groupId: Long, callback: (Int,List<Member>?) -> Unit){
        Log.e("token",xAuthToken)
        service.getGroupMembers(xAuthToken,groupId).enqueue(object : Callback<List<Member>> {
            override fun onResponse(call: Call<List<Member>>, response: Response<List<Member>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        callback(1,responseBody)
                    } else {
                        callback(2,null)
                        Log.e("viewmodel", "Successful response: ${response}")
                    }
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2,null)
                }
            }
            override fun onFailure(call: Call<List<Member>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2,null)
            }
        })
    }
    // 그룹 초대코드 조회
    fun getGroupInvitationCode(groupId: Long,callback: (Int,String?) -> Unit){
        Log.e("token",xAuthToken)
        service.getGroupInvitationCode(xAuthToken,groupId).enqueue(object : Callback<ResoponseInvititionCode> {
            override fun onResponse(call: Call<ResoponseInvititionCode>, response: Response<ResoponseInvititionCode>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.e("viewmodel", "Successful response: ${response}")
                        callback(1,responseBody.code)
                    } else {
                        Log.e("viewmodel", "Unsuccessful response: ${response}")
                        callback(2,null)
                    }
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3,null)
                }
            }
            override fun onFailure(call: Call<ResoponseInvititionCode>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3,null)
            }
        })
    }

    // 리뷰 좋아요 취소
    fun unlickReview(reviewId: Long,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.unlickReview(xAuthToken,reviewId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }

    // 리뷰 좋아요
    fun lickReview(reviewId: Long,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.lickReview(xAuthToken,reviewId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else if(response.code()==400) {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    // 자신 리뷰에는 좋아요 불가능
                    callback(2)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3)
            }
        })
    }

    // 먹스또 - 프로필
    fun getUserProfile(nickname: String,callback: (Int,ResponseProfile?) -> Unit){
        Log.e("token",xAuthToken)
        service.getUserProfile(xAuthToken,nickname).enqueue(object : Callback<ResponseProfile> {
            override fun onResponse(call: Call<ResponseProfile>, response: Response<ResponseProfile>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.e("viewmodel", "Successful response: ${response}")
                        callback(1,responseBody)
                    } else {
                        Log.e("viewmodel", "Successful response: ${response}")
                        callback(2,null)
                    }
                }else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3,null)
                }
            }
            override fun onFailure(call: Call<ResponseProfile>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3,null)
            }
        })
    }
    // 팔로우하기
    fun follow(nickname: String,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.follow(xAuthToken,nickname).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3)
            }
        })
    }
    // 언팔로우하기
    fun unFollow(nickname: String,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.unFollow(xAuthToken,nickname).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3)
            }
        })
    }


}