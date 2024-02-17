package com.gst.gusto.api

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gst.gusto.BuildConfig
import com.gst.gusto.MainActivity
import com.gst.gusto.Util.mapUtil
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.RestItem
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class GustoViewModel: ViewModel() {
    private val retrofit = Retrofit.Builder().baseUrl(BuildConfig.API_BASE)
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val service = retrofit.create(GustoApi::class.java)
    private var xAuthToken = ""
    private var refreshToken = ""

    // 자신의 루트 리스트 - (val title : String, val people : Int, val food : Int, val route : Int)
    val myRouteList = ArrayList<GroupItem>()
    // 루트 생성 데이터
    var requestRoutesData : RequestCreateRoute? = null
    // 자신의 그룹 리스트
    val myGroupList = ArrayList<GroupItem>()
    // 현재 그룹 아이디
    var currentGroupId = 0L
    // 현재 루트 아이디
    var currentRouteId = 0L
    // 현재 그룹장 유뮤
    var groupOner = false
    // 현재 지도에 보이는 마커 리스트 - (val id : Int, val num : Int, val latitude : Double, val longitude : Double, val name : String, val loc : String, val bookMark : Boolean)
    val markerListLiveData:MutableLiveData<ArrayList<mapUtil.Companion.MarkerItem>> = MutableLiveData<ArrayList<mapUtil.Companion.MarkerItem>>().apply {
        value = ArrayList()
    }
    // 그룹 내에 식당 리스트
    val storeListLiveData = ArrayList<RestItem>()

    // 리뷰 작성하기에서 위의 진행도 바
    var progress = 0

    // 리스트 화면에서 돌아온 화면 종류
    var listFragment = "group" // group or route
    var groupFragment = 0 // stores = 0 or routes = 1

    // 리뷰 작성 필요 변수
    var visitedAt: String? = null
    var img: File? = null
    var menuName: String? = null
    var hashTagId: String? = null
    var taste: Int? = null
    var spiciness: Int? = null
    var mood: Int? = null
    var toilet: Int? = null
    var parking: Int? = null
    var comment: String? = null

    // 팔로우 리스트
    var followList: List<Member> = listOf()

    // 가게 정보 보기 아이디 리스트
    val storeIdList = ArrayList<Long>().apply {
        add(1)
        add(2)
        add(3)
        add(4)
    }

    // 선택한 음식점
    var selectStoreId =  2L

    // 현재 동
    var dong = ""




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
                    myRouteList.clear()
                    if(responseBody!=null) {
                        Log.d("viewmodel", "Successful response: ${response}")
                        for(data in responseBody) {
                            myRouteList.add(GroupItem(data.routeId, data.routeName, 0, true,data.numStore, 0))
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
    // 루트 생성/그룹 내 루트 추가
    fun createRoute(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.createRoute(xAuthToken, requestRoutesData!!).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}, ${requestRoutesData}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3)
            }
        })
    }
    // 내/그룹 루트 거리 조회(공통)
    fun getRouteMap(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.getRouteMap(xAuthToken, currentRouteId).enqueue(object : Callback<List<RouteList>> {
            override fun onResponse(call: Call<List<RouteList>>, response: Response<List<RouteList>>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
                    val responseBody = response.body()
                    if(responseBody !=null) {
                        Log.d("viewmodel",responseBody.toString())
                        for(data in responseBody) {
                            val tmp = markerListLiveData.value?.get(data.ordinal-1)
                            if (tmp != null) {
                                tmp.longitude = data.longitude!!
                                tmp.latitude = data.latitude!!
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
    // 내 루트/그룹 루트 상세 조회 (공통)
    fun getGroupRouteDetail(routeId : Long?,callback: (Int) -> Unit){
        val groupId = if (routeId == null) currentGroupId else null
        service.getGroupRouteDetail(xAuthToken,routeId,groupId).enqueue(object : Callback<ResponseRouteDetail> {
            override fun onResponse(call: Call<ResponseRouteDetail>, response: Response<ResponseRouteDetail>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
                    val responseBody = response.body()
                    if(responseBody !=null) {
                        markerListLiveData.value?.clear()
                        for(data in responseBody.routes) {
                            markerListLiveData.value?.add(
                                mapUtil.Companion.MarkerItem(
                                    data.storeId,
                                    data.ordinal,
                                    data.routeListId!!,
                                    0.0,
                                    0.0,
                                    data.storeName!!,
                                    data.address!!,
                                    false
                                )
                            )
                        }
                        markerListLiveData.value?.let { list ->
                            // ordinal 속성을 기준으로 리스트를 정렬
                            list.sortBy { it.ordinal }
                        }
                        callback(1)
                    } else callback(2)

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
    fun deleteRoute(routeId: Long,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.deleteRoute(xAuthToken, routeId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
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
                    Log.d("viewmodel", "Successful response: ${response}")
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
                    myGroupList.clear()
                    if(responseBody!=null) {
                        Log.d("viewmodel", "Successful response: ${response}")
                        for(data in responseBody) {
                            myGroupList.add(GroupItem(data.groupId,data.groupName,data.numMembers,data.isOwner,data.numRestaurants,data.numRoutes))
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
    fun getGroupStores(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.getGroupStores(xAuthToken,currentGroupId).enqueue(object : Callback<List<ResponseStore>> {
            override fun onResponse(call: Call<List<ResponseStore>>, response: Response<List<ResponseStore>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        storeListLiveData.clear()
                        Log.d("viewmodel", "Successful response: ${response}")
                        for(data in responseBody) {
                            storeListLiveData.add(RestItem(data.storeName,data.address,data.storeProfileImg,data.userProfileImg,data.storeId,data.groupListId))
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
    fun getGroupRoutes(callback: (Int,ArrayList<GroupItem>?) -> Unit){
        service.getGroupRoutes(xAuthToken,currentGroupId).enqueue(object : Callback<List<Routes>> {
            override fun onResponse(call: Call<List<Routes>>, response: Response<List<Routes>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.d("viewmodel", "Successful response: ${response}")
                        val tmpRouteList = ArrayList<GroupItem>()
                        for(data in responseBody) {
                            tmpRouteList.add(GroupItem(data.routeId, data.routeName, 0, true,data.numStore, 0))
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
    fun addGroupStore(storeId : Long, callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.addGroupStore(xAuthToken,currentGroupId,StoredId(storeId)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
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
    fun getGroup( callback: (Int, ResponseGroup?) -> Unit){
        Log.e("token",xAuthToken)
        service.getGroup(xAuthToken,currentGroupId).enqueue(object : Callback<ResponseGroup> {
            override fun onResponse(call: Call<ResponseGroup>, response: Response<ResponseGroup>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
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
    fun editGroupOption(groupName: String?,notice: String, callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.editGroupOption(xAuthToken,currentGroupId, RequestGroupOption(groupName,notice)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
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
        val data = RequestCreateGroup(groupName,groupScript)
        service.createGroup(xAuthToken, data).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
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
    fun deleteGroup( callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.deleteGroup(xAuthToken,currentGroupId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
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
    fun deleteGroupStore(groupListId: Long,callback: (Int) -> Unit){
        service.deleteGroupStore(xAuthToken,groupListId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
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
    fun transferOwnership(newOwner: Int, callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.transferOwnership(xAuthToken,currentGroupId, NewOwner(newOwner)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
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
    fun leaveGroup(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.leaveGroup(xAuthToken,currentGroupId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
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
    fun joinGroup(invitationCode : String ,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.joinGroup(xAuthToken, RequestJoinGroup(invitationCode)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
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
    fun getGroupMembers(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.getGroupMembers(xAuthToken,currentGroupId).enqueue(object : Callback<List<Member>> {
            override fun onResponse(call: Call<List<Member>>, response: Response<List<Member>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        followList = responseBody
                        Log.d("viewmodel", "Successful response: ${response}")
                        callback(1)
                    } else {
                        callback(2)
                        Log.e("viewmodel", "Unsuccessful response: ${response}")
                    }
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<List<Member>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }
    // 그룹 초대코드 조회
    fun getGroupInvitationCode(callback: (Int,String?) -> Unit){
        Log.e("token",xAuthToken)
        service.getGroupInvitationCode(xAuthToken,currentGroupId).enqueue(object : Callback<ResoponseInvititionCode> {
            override fun onResponse(call: Call<ResoponseInvititionCode>, response: Response<ResoponseInvititionCode>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.d("viewmodel", "Successful response: ${response}")
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
    // 그룹 루트 삭제
    fun removeGroupRoute(routeId : Long,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.removeGroupRoute(xAuthToken,routeId,currentGroupId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
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

    // 리뷰 좋아요 취소
    fun unlickReview(reviewId: Long,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.unlickReview(xAuthToken,reviewId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
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
                    Log.d("viewmodel", "Successful response: ${response}")
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
                        Log.d("viewmodel", "Successful response: ${response}")
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
    // 리뷰 작성
    fun createReview(callback: (Int) -> Unit){
        val data = RequestCreateReview(1,visitedAt,menuName,hashTagId,taste,spiciness,mood,toilet,parking,comment)
        val fileToUpload: MultipartBody.Part? = if (img != null) {
            val file = img!!
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("uploaded_file", file.name, requestFile)
        } else {
            null
        }
        Log.e("token",xAuthToken)
        Log.d("viewmodel",data.toString())
        Log.d("viewmodel",img.toString())
        service.createReview(xAuthToken,fileToUpload,data).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
                    callback(1)
                }else {
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

    // 팔로우하기
    fun follow(nickname: String,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.follow(xAuthToken,nickname).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
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
                    Log.d("viewmodel", "Successful response: ${response}")
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
    // 팔로워 조회
    fun getFollower(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.getFollower(xAuthToken).enqueue(object : Callback<List<Member>> {
            override fun onResponse(call: Call<List<Member>>, response: Response<List<Member>>) {
                if (response.isSuccessful) {
                    followList = response.body()!!
                    Log.d("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<List<Member>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3)
            }
        })
    }
    // 팔로잉 조회
    fun getFollowing(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.getFollowing(xAuthToken).enqueue(object : Callback<List<Member>> {
            override fun onResponse(call: Call<List<Member>>, response: Response<List<Member>>) {
                if (response.isSuccessful) {
                    followList = response.body()!!
                    Log.d("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<List<Member>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3)
            }
        })
    }
    // 가게 정보 조회(짧은 화면)
    fun getStoreDetailQuick(storedId: Long, callback: (Int,ResponseStoreDetailQuick?) -> Unit){
        Log.e("token",xAuthToken)
        service.getStoreDetailQuick(xAuthToken,storedId).enqueue(object : Callback<ResponseStoreDetailQuick> {
            override fun onResponse(call: Call<ResponseStoreDetailQuick>, response: Response<ResponseStoreDetailQuick>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.d("viewmodel", "Successful response: ${response}")
                        callback(1,responseBody)
                    } else {
                        Log.e("viewmodel", "Unsuccessful response: ${response}")
                        callback(3,null)
                    }

                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3,null)
                }
            }
            override fun onFailure(call: Call<ResponseStoreDetailQuick>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3,null)
            }
        })
    }

    // 행정 구역
    companion object {
        private const val BASE_URL = "https://dapi.kakao.com/"
        private const val REST_API_KEY = "70da0c4f2b9dfd637641a4dd22039969"
    }
    fun getRegionInfo(x: Double,y : Double, callback: (Int) -> Unit){
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val tmpService = retrofit.create(GustoApi::class.java)
        tmpService.getRegionInfo("KakaoAK ${REST_API_KEY}",x.toString(),y.toString()).enqueue(object : Callback<RegionInfoResponse> {
            override fun onResponse(call: Call<RegionInfoResponse>, response: Response<RegionInfoResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.d("viewmodel", "Successful response: ${response}")
                        dong = responseBody.documents.get(1).region3DepthName
                        callback(1)
                    } else {
                        Log.e("viewmodel", "Unsuccessful response: ${response}")
                        callback(3)
                    }

                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<RegionInfoResponse>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3)
            }
        })
    }

}