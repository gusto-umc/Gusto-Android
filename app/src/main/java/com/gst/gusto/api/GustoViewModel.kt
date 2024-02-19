package com.gst.gusto.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gst.gusto.BuildConfig
import com.gst.gusto.MainActivity
import com.gst.gusto.Util.mapUtil
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.RestItem
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.time.LocalDate

class GustoViewModel: ViewModel() {
    private val retrofit = Retrofit.Builder().baseUrl(BuildConfig.API_BASE)
        .addConverterFactory(GsonConverterFactory.create()).build()
    private val service = retrofit.create(GustoApi::class.java)
    private var xAuthToken = ""
    private var refreshToken = ""

    lateinit var mainActivity : MainActivity
    // 해쉬 태그
    val hashTag = listOf<String>("#따뜻함","#여기서는 화장실 금지","#쾌적","#귀여워","#깨끗함","#인스타","#힙함","#나름 괜찮아","#넓음","#분위기","#가성비")

    // 자신의 루트 리스트 - (val title : String, val people : Int, val food : Int, val route : Int)
    val myRouteList = ArrayList<GroupItem>()
    val otherRouteList = ArrayList<GroupItem>()
    // 루트 가게 정보 임시 데이터
    var routeStorTmpData : ResponseStoreListItem? = null

    // 그룹 루트 생성 임시 데이터들
    var tmpName = ""
    val itemList = ArrayList<mapUtil.Companion.MarkerItem>()

    // 루트 이름
    var routeName = ""
    // 루트 편집 정보
    var removeRoute = ArrayList<Long>()
    var addRoute = ArrayList<Long>()

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
    var skipCheck = true
    var visitedAt: String? = null
    var imageFiles = ArrayList<File>()
    var menuName: String? = null
    var hashTagId: ArrayList<Long>? = null
    var taste: Int? = null
    var spiciness: Int? = null
    var mood: Int? = null
    var toilet: Int? = null
    var parking: Int? = null
    var comment: String? = null

    // 팔로우 리스트
    var followList: List<Member> = listOf()
    var followListTitleName = "팔로잉 중"

    // 가게 정보 보기 아이디 리스트
    var storeIdList = ArrayList<Long>().apply {
        add(1)
        add(2)
        add(3)
        add(4)
    }

    // 선택한 음식점
    var selectStoreId =  2L

    // 현재 동
    private var _dong = MutableLiveData<String>("")
    val dong : LiveData<String>
        get() = _dong

    fun changeDong(new : String){
        _dong.value = new
    }

    //var dong = ""

    // 현재 프로필 닉네임
    var profileNickname = ""
    // 현재 피드 리뷰 아이디
    var currentFeedReviewId = -1L
    // 현재 피드 리뷰 데이터
    lateinit var currentFeedData :ResponseFeedDetail
    // 현재 피드 리뷰 작성자 닉네임
    var currentFeedNickname = ""

    // 먹스또 피드 검색 데이터
    val searchFeedData:MutableLiveData<ResponseFeedSearchReviews?> = MutableLiveData<ResponseFeedSearchReviews?>().apply{
        value = null
    }

    //방문 여부
    var whetherVisit : Int?= null
    //카테고리 별(null가능)
    var category : Int?= null


    // 리뷰 모아보기 현재 페이지
    var currentReviewPage = 0


    // 토큰 얻는 함수
    fun getTokens(activity: MainActivity) {
        xAuthToken = activity.getSharedPref().first
        refreshToken = activity.getSharedPref().second
    }

    // 현재 지역의 카테고리 별 찜한 가게 목록(필터링)
    fun getCurrentMapStores(callback: (Int,List<RouteList>?) -> Unit){
        Log.e("token",xAuthToken)
        Log.d("viewmodel","view : ${_dong.value}")
        service.getCurrentMapStores(xAuthToken,_dong.value!!,null).enqueue(object : Callback<List<RouteList>> {
            override fun onResponse(call: Call<List<RouteList>>, response: Response<List<RouteList>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    myRouteList.clear()
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
            override fun onFailure(call: Call<List<RouteList>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3,null)
            }
        })
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
    // 타인의 루트 조회
    fun getOtherRoute(nickname: String,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.getOtherRoute(xAuthToken,nickname).enqueue(object : Callback<List<Routes>> {
            override fun onResponse(call: Call<List<Routes>>, response: Response<List<Routes>>) {
                if (response.isSuccessful) {
                    // 성공적이라면 일단 서버와의 연결에 성공 했다는 것!
                    val responseBody = response.body()
                    otherRouteList.clear()
                    if(responseBody!=null) {
                        Log.d("viewmodel", "Successful response: ${response}")
                        for(data in responseBody) {
                            otherRouteList.add(GroupItem(data.routeId, data.routeName, 0, true,data.numStore, 0))
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
                        Log.d("viewmodel","route data : ${markerListLiveData.value}")
                        routeName = responseBody.routeName
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
    // 내 루트/그룹 루트 상세 조회 (공통)
    fun getOtherRouteDetail(routeId : Long,profileNickname: String,callback: (Int) -> Unit){
        service.getOtherRouteDetail(xAuthToken,routeId,profileNickname).enqueue(object : Callback<ResponseRouteDetail> {
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
                        routeName = responseBody.routeName
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
    fun deleteRouteStore(routeListId : Long,callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.deleteRouteStore(xAuthToken, routeListId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response(Remove): ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response(Remove): ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2)
            }
        })
    }
    // 루트 내 식당 추가 (공통)
    fun addRouteStore(addList: ArrayList<RouteList>,  callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.addRouteStore(xAuthToken,currentRouteId,addList).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response(Add): ${response}")
                    callback(1)
                } else {
                    Log.e("viewmodel", "Unsuccessful response(add): ${response} ${addList}")
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
    // 그룹 내 루트 목록
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
                } else if(response.code()==400) {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
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
    // 그룹 1건 조회
    fun getGroup( callback: (Int, ResponseGroup?) -> Unit){
        Log.e("token",xAuthToken)
        service.getGroup(xAuthToken,currentGroupId).enqueue(object : Callback<ResponseGroup> {
            override fun onResponse(call: Call<ResponseGroup>, response: Response<ResponseGroup>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}, data : ${response.body()!!}")
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
                    Log.d("viewmodel", "Successful response: ${response}, id:${newOwner}")
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
    fun unlickReview(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.unlickReview(xAuthToken,currentFeedReviewId).enqueue(object : Callback<ResponseBody> {
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
    fun lickReview(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.lickReview(xAuthToken,currentFeedReviewId).enqueue(object : Callback<ResponseBody> {
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
        val data = RequestCreateReview(skipCheck,myStoreDetail?.storeId!!.toLong(),visitedAt,menuName,hashTagId,taste,spiciness,mood,toilet,parking,comment)
        val filesToUpload: MutableList<MultipartBody.Part> = mutableListOf()

        // 이미지 파일들을 반복하면서 MultipartBody.Part 리스트에 추가
        imageFiles?.forEach { imgFile ->
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imgFile)
            val filePart = MultipartBody.Part.createFormData("image", imgFile.name, requestFile)
            filesToUpload.add(filePart)
        }

        service.createReview(xAuthToken,filesToUpload,data).enqueue(object : Callback<ResponseBody> {
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
    // 먹스또 피드 상세 보기
    fun getFeedReview(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.getFeedReview(xAuthToken,currentFeedReviewId).enqueue(object : Callback<ResponseFeedDetail> {
            override fun onResponse(call: Call<ResponseFeedDetail>, response: Response<ResponseFeedDetail>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.d("getFeedReview", "Successful response: ${response}")
                        currentFeedData = responseBody
                        callback(1)
                    } else {
                        Log.e("getFeedReview success", "Unsuccessful response: ${response}")
                        callback(3)
                    }
                } else {
                    Log.e("getFeedReview Android", "Unsuccessful response: ${response}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<ResponseFeedDetail>, t: Throwable) {
                Log.e("getFeedReview server", "Failed to make the request", t)
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
    fun unFollow(nickname: String, callback: (Int) -> Unit){
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
                    Log.d("viewmodel", "Successful response: ${response} ${response.body()}")
                    callback(1)
                }else if(response.code()==404){
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
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
                } else if(response.code()==404){
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2)
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



    //----------------------------------------------------------------------------------------------------------------------------------------------------------//

    //내 위치 장소보기 카테고리 array
    var myMapCategoryList : List<ResponseMapCategory>? = null
    var myAllCategoryList : List<ResponseMapCategory>? = null

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
        var categoryRequestData = RequestEditCategory(myCategoryName = categoryName, myCategoryIcon = categoryIcon, publishCategory = "PUBLIC", myCategoryScript = desc)
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
    // 카테고리 삭제하기
    fun deleteCategory(categoryId : Int, callback: (Int) -> Unit){
        service.deleteCategory(xAuthToken, myCategoryId = categoryId).enqueue(object : Callback<Void>{
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
    //타인 카테고리 전체 조회 - 피드, 마이 -> 확인 완, nickname 전달 필요
    fun getAllCategory(nickname: String, callback: (Int) -> Unit){
        service.getAllCategory(xAuthToken, nickname = nickname).enqueue(object : Callback<List<ResponseMapCategory>>{
            override fun onResponse(
                call: Call<List<ResponseMapCategory>>,
                response: Response<List<ResponseMapCategory>>
            ) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    Log.d("getAllMap", response.body()!!.toString())
                    myAllCategoryList = response.body()!!
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
    //카테고리 전체 조회 - 피드, 마이 -> 확인 완, nickname 전달 필요
    fun getAllUserCategory(callback: (Int) -> Unit){
        service.getAllUserCategory(xAuthToken).enqueue(object : Callback<List<ResponseMapCategory>>{
            override fun onResponse(
                call: Call<List<ResponseMapCategory>>,
                response: Response<List<ResponseMapCategory>>
            ) {
                if (response.isSuccessful) {
                    Log.e("getAllUserCategory", "Successful response: ${response}")
                    Log.d("getAllUserCategory", response.body()!!.toString())
                    myAllCategoryList = response.body()!!
                    callback(0)
                } else {
                    Log.e("getAllUserCategory", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<List<ResponseMapCategory>>, t: Throwable) {
                Log.e("getAllUserCategory", "Failed to make the request", t)
                callback(1)
            }

        })
    }

    /**
     * 가게 api 함수 - mindy
     */
    var selectedDetailStoreId = 1
    var myMapStoreList : List<ResponseStoreListItem>? = null
    var myAllStoreList : List<ResponseStoreListItem>? = null

    var myStoreDetail : ResponseStoreDetail? = null
    var storeDetailReviews = ArrayList<ResponseReviews>()
    var detailReviewLastId : Long? = null
    var detailReviewLastVisitedAt : String? = null

    var userNickname : String = "Gusto"

    var mapVisitedList : List<ResponseSavedStoreData>? = null
    var mapUnvisitedList : List<ResponseSavedStoreData>? = null
    var mapVisitedCnt = 0
    var mapUnvisitedCnt = 0

    //가게 카테고리 추가(찜) -> 확인 완, 수정 필요
    fun addPin(categoryId: Long, storeLong: Long, callback: (Int, ResponseAddPin?) -> Unit){
        var pinData = RequestPin(storeId = storeLong)
        service.addPin(xAuthToken, categoryId, pinData).enqueue(object : Callback<ResponseAddPin>{
            override fun onResponse(call: Call<ResponseAddPin>, response: Response<ResponseAddPin>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(0, response.body()!!)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(1, null)
                }
            }

            override fun onFailure(call: Call<ResponseAddPin>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1, null)
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
    //가게 상세 조회 -> 완
    fun getStoreDetail(storeId: Long, callback: (Int) -> Unit){
        service.getStoreDetail(xAuthToken, storeId, detailReviewLastVisitedAt, detailReviewLastId).enqueue(object : Callback<ResponseStoreDetail>{
            override fun onResponse(
                call: Call<ResponseStoreDetail>,
                response: Response<ResponseStoreDetail>
            ) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    Log.d("getStoreDetail", response.body()!!.reviews.toString())
                    myStoreDetail = response.body()
                    if(detailReviewLastId == null){
                        for (i in response.body()!!.reviews){
                            detailReviewLastId = i.reviewId
                            detailReviewLastVisitedAt = i.visitedAt
                            storeDetailReviews.add(i)
                            Log.d("reviewId check first", i.toString())
                            Log.d("reviewId check first", detailReviewLastId.toString())
                        }
                    }
                    else{
                        Log.d("reviewId check", "more")
                        for (i in response.body()!!.reviews){
                            detailReviewLastId = i.reviewId
                            detailReviewLastVisitedAt = i.visitedAt
                            storeDetailReviews.add(i)
                            Log.d("reviewId check more", i.toString())
                            Log.d("reviewId check more", detailReviewLastId.toString())
                        }
                    }
                    callback(0)
                }
                else {
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
    //카테고리 별 가게 조회 - 위치기반 -> 완
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
    //타인 카테고리 별 가게 조회 - 전체 -> 완
    fun getAllStores(categoryId: Int, nickname: String, callback: (Int) -> Unit){
        service.getAllStores(xAuthToken, nickname = nickname, categoryId = categoryId).enqueue(object : Callback<List<ResponseStoreListItem>>{
            override fun onResponse(
                call: Call<List<ResponseStoreListItem>>,
                response: Response<List<ResponseStoreListItem>>
            ) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    myAllStoreList = response.body()!!
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
    // 내 카테고리 별 전체 가게 조회
    fun getAllUserStores(categoryId: Int,  callback: (Int) -> Unit){
        service.getAllUserStores(xAuthToken, categoryId = categoryId).enqueue(object : Callback<List<ResponseStoreListItem>>{
            override fun onResponse(
                call: Call<List<ResponseStoreListItem>>,
                response: Response<List<ResponseStoreListItem>>
            ) {
                if (response.isSuccessful) {
                    Log.e("getAllUserStores", response.body()!!.toString())
                    myAllStoreList = response.body()!!
                    callback(0)
                } else {
                    Log.e("getAllUserStores", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<List<ResponseStoreListItem>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }
    //저장된 맛집 리스트 -> 완
    var savedStoreIdList = ArrayList<Long>()
    var unsavedStoreIdList = ArrayList<Long>()
    fun getSavedStores(townName: String, categoryId : Int?, callback: (Int) -> Unit){
        service.getSavedStores(xAuthToken, townName = _dong.value!!, categoryId = null).enqueue(object : Callback<List<ResponseSavedStore>>{
            override fun onResponse(
                call: Call<List<ResponseSavedStore>>,
                response: Response<List<ResponseSavedStore>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()!![0]
                    userNickname = data.nickname
                    mapVisitedList = data.visitedStores[0].visitedStores
                    mapVisitedCnt = data.visitedStores[0].numPinStores
                    mapUnvisitedList = data.unvisitedStores[0].unvisitedStores
                    mapUnvisitedCnt = data.unvisitedStores[0].numPinStores
                    unsavedStoreIdList.clear()
                    savedStoreIdList.clear()
                    if(!mapUnvisitedList.isNullOrEmpty()){
                        for(i in mapUnvisitedList!!){
                            unsavedStoreIdList.add(i.storeId.toLong())
                        }
                    }

                    if(!mapVisitedList.isNullOrEmpty()){
                        for(i in mapVisitedList!!){
                            savedStoreIdList.add(i.storeId.toLong())
                        }
                    }
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
    var reviewEditImg = ArrayList<File>()
    //리뷰 1건 조회 -> 확인 완
    fun getReview(reviewId : Long, callback: (Int) -> Unit){
        service.getReview(xAuthToken, reviewId.toInt()).enqueue(object : Callback<ResponseMyReview>{
            override fun onResponse(
                call: Call<ResponseMyReview>,
                response: Response<ResponseMyReview>
            ) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    Log.e("viewmodel", response.body()!!.toString())
                    myReview = response.body()!!
                    callback(0)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(1)
                    myReview = null
                }
            }

            override fun onFailure(call: Call<ResponseMyReview>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }
    //리뷰 수정 -> 확인 완
    fun editReview(reviewId : Long, img : String?, menuName : String?, taste : Int, spiceness : Int, mood : Int, toilet : Int, parking : Int, comment : String?, callback: (Int) -> Unit){
        var requestBody = RequestMyReview(menuName = menuName, taste = taste, spiciness = spiceness, mood = mood, toilet = toilet, parking = parking, comment = comment)
        val filesToUpload: MutableList<MultipartBody.Part> = mutableListOf()

        // 이미지 파일들을 반복하면서 MultipartBody.Part 리스트에 추가
        reviewEditImg?.forEach { imgFile ->
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imgFile)
            val filePart = MultipartBody.Part.createFormData("image", imgFile.name, requestFile)
            filesToUpload.add(filePart)
        }
        Log.d("edit checking", requestBody.toString())
        service.editReview(xAuthToken, reviewId, filesToUpload, requestBody).enqueue(object : Callback<Void>{
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
    //리뷰 삭제
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
    var mapSearchArray = ArrayList<ResponseSearch>()
    var mapSearchStoreIdArray = ArrayList<Long>()

    var keepFlag = false
    var mapKeepArray = ArrayList<ResponseSearch>()
    var mapKeepStoreIdArray = ArrayList<Long>()
    var searchKeepKeyword = ""
    //검색 결과 -> 작성 예정
    fun getSearchResult(keyword : String, callback: (Int) -> Unit){
        service.getSearch(xAuthToken, keyword).enqueue(object : Callback<ArrayList<ResponseSearch>>{
            override fun onResponse(
                call: Call<ArrayList<ResponseSearch>>,
                response: Response<ArrayList<ResponseSearch>>
            ) {
                if (response.isSuccessful) {
                    Log.d("getSearchResult", "Successful response: ${response}")
                    mapSearchArray = response.body()!!
                    mapKeepArray = response.body()!!
                    mapSearchStoreIdArray.clear()
                    for(i in response.body()!!){
                        mapSearchStoreIdArray.add(i.storeId)
                        mapKeepStoreIdArray.add(i.storeId)
                    }
                    callback(0)

                } else {
                    Log.e("getSearchResult", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<ArrayList<ResponseSearch>>, t: Throwable) {
                Log.e("getSearchResult", "Failed to make the request", t)
                callback(1)
            }

        })
    }


    // 행정 구역
    companion object {
        private const val BASE_URL = "https://dapi.kakao.com/"
        private const val REST_API_KEY = "70da0c4f2b9dfd637641a4dd22039969"
    }
    fun getRegionInfo(x: Double,y : Double, callback: (Int,String) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val tmpService = retrofit.create(GustoApi::class.java)
        tmpService.getRegionInfo("KakaoAK ${REST_API_KEY}", x.toString(), y.toString())
            .enqueue(object : Callback<RegionInfoResponse> {
                override fun onResponse(
                    call: Call<RegionInfoResponse>,
                    response: Response<RegionInfoResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            Log.d("viewmodel", "Successful response: ${response}")
                            if(responseBody.documents.get(1).region3DepthName == _dong.value) {
                                callback(2,responseBody.documents.get(1).addressName)
                            } else {
                                _dong.value = responseBody.documents.get(1).region3DepthName
                                callback(1,responseBody.documents.get(1).addressName)
                            }

                        } else {
                            Log.e("viewmodel", "Unsuccessful response: ${response}")
                            callback(3,"위치를 알 수 없음")
                        }

                    } else {
                        Log.e("viewmodel", "Unsuccessful response: ${response}")
                        callback(3,"위치를 알 수 없음")
                    }
                }

                override fun onFailure(call: Call<RegionInfoResponse>, t: Throwable) {
                    Log.e("viewmodel", "Failed to make the request", t)
                    callback(3,"알 수 없음")
                }
            })
    }


    // 리뷰 모아보기-1 (Insta view)
    fun instaView(reviewId: Long?, size: Int, callback: (Int, ResponseInstaReview?) -> Unit){
        service.instaView(xAuthToken, reviewId, size).enqueue(object : Callback<ResponseInstaReview> {
            override fun onResponse(call: Call<ResponseInstaReview>, response: Response<ResponseInstaReview>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.e("viewmodel", "1 Successful response: ${response}")
                        callback(1, responseBody)
                    } else {
                        Log.e("viewmodel", "2 Successful response: ${response}")
                        callback(2, null)
                    }
                }else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3, null)
                }
            }
            override fun onFailure(call: Call<ResponseInstaReview>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3, null)
            }
        })
    }
    // 리뷰 모아보기-3 (timeline view)
    fun timeLineView(reviewId: Long?, size: Int, callback: (Int, ResponseListReview?) -> Unit){
        Log.e("token",xAuthToken)
        service.timelineView(xAuthToken, reviewId, size).enqueue(object : Callback<ResponseListReview> {
            override fun onResponse(call: Call<ResponseListReview>, response: Response<ResponseListReview>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.e("viewmodel", "1 Successful response: ${response}")
                        callback(1, responseBody)
                    } else {
                        Log.e("viewmodel", "2 Successful response: ${response}")
                        callback(2, null)
                    }
                }else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3, null)
                }
            }
            override fun onFailure(call: Call<ResponseListReview>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3, null)
            }
        })
    }


    // 리뷰 모아보기 - 2 (cal view)
    fun calView(reviewId: Long?, size: Int, date: LocalDate, callback: (Int, ResponseCalReview?) -> Unit){
        service.calView(xAuthToken, reviewId, size, date).enqueue(object : Callback<ResponseCalReview> {
            override fun onResponse(call: Call<ResponseCalReview>, response: Response<ResponseCalReview>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.e("viewmodel", "1 Successful response: ${response}")
                        callback(1, responseBody)
                    } else {
                        Log.e("viewmodel", "2 Successful response: ${response}")
                        callback(2, null)
                    }
                }else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3, null)
                }
            }
            override fun onFailure(call: Call<ResponseCalReview>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3, null)
            }
        })
    }

    // 먹스또 랜덤 피드
    fun feed(callback: (Int, ArrayList<ResponseFeedReview>?) -> Unit){
        service.feed(xAuthToken).enqueue(object : Callback<ArrayList<ResponseFeedReview>> {
            override fun onResponse(call: Call<ArrayList<ResponseFeedReview>>, response: Response<ArrayList<ResponseFeedReview>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.e("viewmodel", "1 Successful response: ${response}")
                        callback(1, responseBody)
                    } else {
                        Log.e("viewmodel", "2 Successful response: ${response}")
                        callback(2, null)
                    }
                }else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3, null)
                }
            }
            override fun onFailure(call: Call<ArrayList<ResponseFeedReview>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3, null)
            }
        })
    }

    // 맛집 & 해시태그 검색 엔진
    fun feedSearch(keyword: String, hashTags: List<Long>?, callback: (Int, ResponseFeedSearchReviews?) -> Unit){
        service.feedSearch(xAuthToken, keyword, hashTags).enqueue(object : Callback<ResponseFeedSearchReviews> {
            override fun onResponse(call: Call<ResponseFeedSearchReviews>, response: Response<ResponseFeedSearchReviews>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.e("viewmodel", "1 Successful response: ${response}")
                        callback(1, responseBody)
                    } else {
                        Log.e("viewmodel", "2 Successful response: ${response}")
                        callback(2, null)
                    }
                }else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3, null)
                }
            }
            override fun onFailure(call: Call<ResponseFeedSearchReviews>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3, null)
            }
        })
    }
    //현재 지역의 카테고리 별 찜한 가게 목록(필터링)
    fun LocalCategory(callback: (Int) -> Unit){

    }

    //내 카테고리 전체 조회 + 카테고리 담기
    fun getMyMapCategory(townName: String, callback: (Int) -> Unit) {
        service.getMapCategory(xAuthToken, townName = townName).enqueue(object : Callback<List<ResponseMapCategory>> {
            override fun onResponse(
                call: Call<List<ResponseMapCategory>>,
                response: Response<List<ResponseMapCategory>>
            ) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", response.body()!!.toString())
                    Log.e("viewmodel", "Successful response: ${response}")
                    myMapCategoryList = response.body()!! // 서버에서 받아온 카테고리 목록을 저장
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



    // 타인 리뷰 모아보기
    fun otherInstaView(nickName: String, reviewId: Long?, size: Int, callback: (Int, ResponseInstaReview?) -> Unit){
        service.otherInstaView(xAuthToken, nickName, reviewId, size).enqueue(object : Callback<ResponseInstaReview> {
            override fun onResponse(call: Call<ResponseInstaReview>, response: Response<ResponseInstaReview>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.e("viewmodel", "1 Successful response: ${response}")
                        callback(1, responseBody)
                    } else {
                        Log.e("viewmodel", "2 Successful response: ${response}")
                        callback(2, null)
                    }
                }else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3, null)
                }
            }
            override fun onFailure(call: Call<ResponseInstaReview>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3, null)
            }
        })
    }
}