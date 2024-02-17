package com.gst.gusto.api

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
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
import java.time.LocalDate

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

    //----------------------------------------------------------------------------------------------------------------------------------------------------------//

    //내 우치 장소보기 카테고리 array
    var myMapCategoryList : List<ResponseMapCategory>? = null
    var myAllCategoryList : List<ResponseAllCategory>? = null

    private val _cateEditFlag = MutableLiveData<Boolean?>(false)
    val cateEditFlag: LiveData<Boolean?>
        get() = _cateEditFlag
    fun changeEditFlag(flag : Boolean){
        _cateEditFlag.value = !flag
    }
    var selectedCategory = mutableListOf<Int>()

    private val _categoryAllFlag = MutableLiveData<Boolean>(false)
    val categoryAllFlag : LiveData<Boolean>
        get() = _categoryAllFlag
    fun changeCategoryFlag(flag : Boolean){
        _categoryAllFlag.value = flag
    }
    var cateRemoveFlag = false


    /**
     * 카테고리 api 함수 - mindy
     */

    // 카테고리 추가 -> 확인 완료
    fun addCategory(categoryName : String,categoryIcon : Int, public : String, desc : String,  callback: (Int) -> Unit){
        var categoryRequestData = RequestAddCategory(myCategoryName = categoryName, myCategoryIcon = categoryIcon, publishCategory = public, myCategoryScript = desc )
        Log.d("checking", categoryRequestData.toString())
        service.addCategory(xAuthToken, data = categoryRequestData).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    Log.d("viewmodel", "Successful response: ${response}")
                    callback(0)
                }else{
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }
    // 카테고리 수정 -> 확인 완료, comment 일단 제외
    fun editCategory(categoryId: Long, categoryName: String, categoryIcon: Int, public: String, desc: String, callback: (Int) -> Unit){
        var categoryRequestData = RequestEditCategory(myCategoryName = categoryName, myCategoryIcon = categoryIcon, publishCategory = "PUBLIC", desc)
        Log.d("checking edit", categoryRequestData.toString())
        Log.d("checking edit", categoryId.toString())
        service.editCategory(token = xAuthToken, myCategoryId = categoryId, data = categoryRequestData).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    Log.d("viewmodel edit", "Successful response: ${response}")
                    callback(0)
                }else{
                    Log.e("viewmodel edit", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("viewmodel edit", "Failed to make the request", t)
                callback(1)
            }

        })
    }
    // 카테고리 조회(위치 기반, 내 위치 장소보기) -> 확인 완료
    fun getMapCategory(townName : String, callback: (Int) -> Unit){
        service.getMapCategory(xAuthToken, townName = townName).enqueue(object :Callback<List<ResponseMapCategory>>{
            override fun onResponse(
                call: Call<List<ResponseMapCategory>>,
                response: Response<List<ResponseMapCategory>>
            ) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", response.body()!!.toString())
                    Log.e("viewmodel", "Successful response: ${response}")
                    myMapCategoryList = response.body()!!
                    callback(0)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<List<ResponseMapCategory>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }
    // 카테고리 삭제하기 -> 단 건 삭제 확인
    fun deleteCategory(categoryId : Int, callback: (Int) -> Unit){
        service.deleteCategory(xAuthToken, myCategoryId = categoryId).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.e("deleteCategory", "Successful response: ${response}")
                    callback(0)
                } else {
                    Log.e("deleteCategory", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }
    //카테고리 전체 조회 - 피드, 마이 -> 확인 완, nickname 전달 필요
    fun getAllCategory(nickname: String, callback: (Int) -> Unit){
        service.getAllCategory(xAuthToken, nickname = nickname).enqueue(object : Callback<List<ResponseAllCategory>>{
            override fun onResponse(
                call: Call<List<ResponseAllCategory>>,
                response: Response<List<ResponseAllCategory>>
            ) {
                if (response.isSuccessful) {
                    Log.e("getAllMap", "Successful response: ${response}")
                    Log.d("getAllMap", response.body()!!.toString())
                    myAllCategoryList = response.body()!!
                    callback(0)
                } else {
                    Log.e("getAllMap", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<List<ResponseAllCategory>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }

    /**
     * 가게 api 함수 - mindy
     */
    var myMapStoreList : List<ResponseStoreListItem>? = null
    var myAllStoreList : List<ResponseStoreListItem>? = null

    var myStoreDetail : ResponseStoreDetail? = null

    //가게 카테고리 추가(찜) -> 확인 완, 수정 필요
    fun addPin(categoryId: Long, storeLong: Long, callback: (Int) -> Unit){
        var pinData = RequestPin(storeId = storeLong)
        service.addPin(xAuthToken, categoryId, pinData).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(0)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }
    //가게 카테고리 삭제(찜 취소) -> 수정 필요
    fun deletePin(storeId: Int, callback: (Int) -> Unit){
        service.deletePin(xAuthToken, storeId).enqueue(object :Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(0)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }
    //가게 상세 조회 -> 수정 필요
    fun getStoreDetail(storeId: Long, reviewId : Int?, callback: (Int) -> Unit){
        service.getStoreDetail(xAuthToken, storeId, reviewId).enqueue(object : Callback<ResponseStoreDetail>{
            override fun onResponse(
                call: Call<ResponseStoreDetail>,
                response: Response<ResponseStoreDetail>
            ) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    Log.d("getStoreDetail", response.body()!!.reviews.toString())
                    myStoreDetail = response.body()
                    callback(0)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<ResponseStoreDetail>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }
    //카테고리 별 가게 조회 - 위치기반
    fun getMapStores(categoryId: Int, townName: String, callback: (Int) -> Unit){
        service.getMapStores(xAuthToken, categoryId = categoryId, townName = townName).enqueue(object :Callback<List<ResponseStoreListItem>>{
            override fun onResponse(
                call: Call<List<ResponseStoreListItem>>,
                response: Response<List<ResponseStoreListItem>>
            ) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    Log.d("viewmodel", response.body()!!.toString())
                    myMapStoreList = response.body()!!
                    callback(0)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<List<ResponseStoreListItem>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }
    //카테고리 별 가게 조회 - 전체
    fun getAllStores(categoryId: Int, nickname: String, callback: (Int) -> Unit){
        service.getAllStores(xAuthToken, nickname = nickname, categoryId = categoryId).enqueue(object : Callback<List<ResponseStoreListItem>>{
            override fun onResponse(
                call: Call<List<ResponseStoreListItem>>,
                response: Response<List<ResponseStoreListItem>>
            ) {
                if (response.isSuccessful) {
                    Log.e("get all stores", "Successful response: ${response}")
                    myAllStoreList = response.body()!!
                    callback(0)
                } else {
                    Log.e("get all stores", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<List<ResponseStoreListItem>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }
    //저장된 맛집 리스트 -> 수정 예정
    fun getSavedStores(townName: String, categoryId : Int?, callback: (Int) -> Unit){
        service.getSavedStores(xAuthToken, townName = "성수1가1동", categoryId = 3).enqueue(object : Callback<List<ResponseSavedStore>>{
            override fun onResponse(
                call: Call<List<ResponseSavedStore>>,
                response: Response<List<ResponseSavedStore>>
            ) {
                if (response.isSuccessful) {
                    Log.e("getSavedStores", "Successful response: ${response}")
                    Log.d("getSavedStores", response.body()!![0].toString())
                    callback(0)
                } else {
                    Log.e("getSavedStores", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<List<ResponseSavedStore>>, t: Throwable) {
                Log.e("getSavedStores", "Failed to make the request", t)
                callback(1)
            }

        })
    }

    /**
     * 리뷰 api 함수 - mindy
     */

    var myReview : ResponseMyReview? = null
    var myReviewId : Long? = null
    //리뷰 1건 조회 -> 확인 완
    fun getReview(reviewId : Int, callback: (Int) -> Unit){
        service.getReview(xAuthToken, reviewId).enqueue(object : Callback<ResponseMyReview>{
            override fun onResponse(
                call: Call<ResponseMyReview>,
                response: Response<ResponseMyReview>
            ) {
                if (response.isSuccessful) {
                    Log.e("getReview", "Successful response: ${response}")
                    Log.e("getReview", response.body()!!.toString())
                    myReview = response.body()!!
                    callback(0)
                } else {
                    Log.e("getReview", "Unsuccessful response: ${response}")
                    callback(1)
                    myReview = null
                }
            }

            override fun onFailure(call: Call<ResponseMyReview>, t: Throwable) {
                Log.e("getReview", "Failed to make the request", t)
                callback(1)
            }

        })
    }
    //리뷰 수정 -> 확인 완
    fun editReview(reviewId : Long, img : String?, menuName : String?, taste : Int, spiceness : Int, mood : Int, toilet : Int, parking : Int, comment : String?, callback: (Int) -> Unit){
        var requestBody = RequestMyReview(menuName = menuName, taste = taste, spiciness = spiceness, mood = mood, toilet = toilet, parking = parking, comment = comment)
        Log.d("edit checking", requestBody.toString())
        service.editReview(xAuthToken, reviewId, null, requestBody).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(0)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }
    //리뷰 삭제 -> 확인 완
    fun deleteReview(reviewId: Long, callback: (Int) -> Unit){
        service.deleteReview(xAuthToken, reviewId).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
                    callback(0)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }

    /**
     * 검색 api 함수 - mindy
     */
    //검색 결과 -> 작성 예정

}