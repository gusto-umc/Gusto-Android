package com.gst.gusto.api

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gst.gusto.MainActivity
import com.gst.gusto.R
import com.gst.gusto.api.retrofit.RetrofitInstance
import com.gst.gusto.dto.ResponseInstaReviews
import com.gst.gusto.list.adapter.GroupAdapter
import com.gst.gusto.util.mapUtil
import com.gst.gusto.list.adapter.GroupItem
import com.gst.gusto.list.adapter.MapRoutesAdapter
import com.gst.gusto.list.adapter.RestItem
import com.gst.gusto.list.fragment.GroupRouteCreateFragment
import com.gst.gusto.list.fragment.GroupRoutesFragment
import com.gst.gusto.list.fragment.GroupStoresFragment
import com.gst.gusto.list.fragment.RouteCreateFragment
import com.gst.gusto.util.GustoApplication
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

    private val service = RetrofitInstance.createService(GustoApi::class.java)
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


    // 그룹 store 프래그먼트
    lateinit var groupStoresFragment: GroupStoresFragment
    // 그룹 route 프래그먼트
    lateinit var groupRouteFragment: GroupRoutesFragment
    // 그룹 route create 프래그먼트
    lateinit var groupRouteCreateFragment : GroupRouteCreateFragment

    // 루트 이름
    var routeName = ""
    // 루트 공개 여부
    var publishRoute = false
    // 루트 편집 정보
    var removeRoute = ArrayList<Long>()
    var addRoute = ArrayList<Long>()
    var editMode = false

    // 루트 생성 데이터
    var requestRoutesData : RequestCreateRoute? = null
    // 자신의 그룹 리스트
    val myGroupList = ArrayList<GroupItem>()
    // 현재 그룹 아이디
    var currentGroupId = 0L
    var currentGroupName =""
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
    var publishCheck = false

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
    // lateinit var activity : MainActivity

    private val _tokenToastData: MutableLiveData<Unit> = MutableLiveData()
    val tokenToastData: LiveData<Unit>
        get() = _tokenToastData

    fun getTokens() {
        xAuthToken = GustoApplication.prefs.getSharedPrefs().first
        refreshToken = GustoApplication.prefs.getSharedPrefs().second
    }

    fun refreshToken(){
        service.refreshToken(xAuthToken, refreshToken)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    //Log.d("thisistoken",xAuthToken+", "+refreshToken)
                    if (response.isSuccessful) {
                        xAuthToken = response.headers().get("X-Auth-Token")?:""
                        refreshToken = response.headers().get("refresh-Token")?:""
                        GustoApplication.prefs.setSharedPrefs(xAuthToken, refreshToken)
                        Log.d("thisistoken2",xAuthToken)
                    } else if(response.code()==403) {
                        _tokenToastData.value = Unit
                        refreshToken()
                    }  else {
                        Log.e("LoginViewModel", "Unsuccessful response: ${response}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("LoginViewModel", "Failed to make the request", t)
                }
            })
    }

    // 현재 지역의 카테고리 별 찜한 가게 목록(필터링)
    fun getCurrentMapStores(cateId : Int?, isVisited : Boolean,callback: (Int,List<RouteList>?) -> Unit){
        Log.e("token",xAuthToken)
        Log.d("viewmodel","view : ${_dong.value}")
        service.getCurrentMapStores(xAuthToken,_dong.value!!,cateId, isVisited).enqueue(object : Callback<List<RouteList>> {
            override fun onResponse(call: Call<List<RouteList>>, response: Response<List<RouteList>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    myRouteList.clear()
                    if(responseBody!=null) {
                        Log.d("viewmodel helo", "Successful response: ${response.body()}")
                        callback(1,responseBody)
                    } else {
                        Log.e("viewmodel", "Unsuccessful response: ${response}")
                        callback(3,null)
                    }
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else {
                    Log.e("viewmodel helo3", "Unsuccessful response: ${response}")
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
    fun getMyRoute(lastRouteId: Long?, callback: (Int, Boolean) -> Unit){
        Log.e("token",xAuthToken)
        service.getMyRoute(xAuthToken,lastRouteId).enqueue(object : Callback<ResponseRoutes> {
            override fun onResponse(call: Call<ResponseRoutes>, response: Response<ResponseRoutes>) {
                if (response.isSuccessful) {
                    // 성공적이라면 일단 서버와의 연결에 성공 했다는 것!
                    val responseBody = response.body()
                    myRouteList.clear()
                    if(responseBody!=null) {
                        Log.d("viewmodel2", "Successful response: ${responseBody}")
                        for(data in responseBody.result) {
                            myRouteList.add(GroupItem(data.routeId, data.routeName, 0, true,data.numStore, 0))
                        }
                        callback(1,responseBody.hasNext)
                    } else callback(2,false)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3,false)
                }
            }
            override fun onFailure(call: Call<ResponseRoutes>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3,false)
            }
        })
    }
    // 타인의 루트 조회
    fun getOtherRoute(lastRouteId: Long?,nickname: String,callback: (Int, Boolean) -> Unit){
        Log.e("token",xAuthToken)
        service.getOtherRoute(xAuthToken,nickname,lastRouteId).enqueue(object : Callback<ResponseRoutes> {
            override fun onResponse(call: Call<ResponseRoutes>, response: Response<ResponseRoutes>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    otherRouteList.clear()
                    if(responseBody!=null) {
                        Log.d("viewmodel2", "Successful response: ${responseBody}")
                        for(data in responseBody.result) {
                            otherRouteList.add(GroupItem(data.routeId, data.routeName, 0, true,data.numStore, 0))
                        }
                        callback(1,responseBody.hasNext)
                    } else callback(2,false)

                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3,false)
                }
            }
            override fun onFailure(call: Call<ResponseRoutes>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3,false)
            }
        })
    }
    // 루트 생성
    fun createRoute(callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.createRoute(xAuthToken, requestRoutesData!!).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
    // 그룹 내 루트 추가
    fun createGroupRoute(callback: (Int) -> Unit){
        service.createGroupRoute(xAuthToken, requestRoutesData!!, currentGroupId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodelGR", "Successful response: ${response}")
                    callback(1)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else {
                    Log.e("viewmodelGR", "Unsuccessful response: ${response}, ${requestRoutesData}")
                    callback(3)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodelGR", "Failed to make the request", t)
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                        Log.d("viewmodel","route data : ${responseBody}")
                        routeName = responseBody.routeName
                        publishRoute = responseBody.publishRoute
                        callback(1)
                    } else callback(2)

                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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

                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
    // 루트 수정
    fun editRoute(routeListId : Long,routeName : String, routeList : List<RouteList>?, callback: (Int) -> Unit){
        service.editRoute(xAuthToken, routeListId ,RequestEditRoute(routeName, routeList)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
    fun patchPublish(routeListId : Long,publish : Boolean, callback: (Int) -> Unit){
        service.patchPublish(xAuthToken, routeListId ,publish).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
    // 루트 내 식당 추가 (공통)
    fun addRouteStore(addList: ArrayList<RouteList>,  callback: (Int) -> Unit){
        Log.e("token",xAuthToken)
        service.addRouteStore(xAuthToken,currentRouteId,addList).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response(Add): ${response}")
                    callback(1)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
    fun getGroups(lastGroupId : Long?,callback: (Int, Boolean) -> Unit){
        service.getGroups(xAuthToken,lastGroupId).enqueue(object : Callback<ResponseGetGroups> {
            override fun onResponse(call: Call<ResponseGetGroups>, response: Response<ResponseGetGroups>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    myGroupList.clear()
                    Log.d("viewmodel", "Successful response: ${responseBody}")
                    if(responseBody!=null&&responseBody.groups!=null) {
                        for(data in responseBody.groups) {
                            myGroupList.add(GroupItem(data.groupId,data.groupName,data.numMembers,data.isOwner,data.numRestaurants,data.numRoutes))
                        }
                        callback(1,responseBody.hasNext)
                    }else {
                        callback(2,false)
                    }
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3,false)
                }
            }
            override fun onFailure(call: Call<ResponseGetGroups>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3,false)
            }
        })
    }
    // 그룹 내 찜 식당 목록 조회
    fun getGroupStores(lastStoreId : Long?,callback: (Int, Boolean) -> Unit){
        Log.e("token",xAuthToken)
        service.getGroupStores(xAuthToken,currentGroupId,lastStoreId).enqueue(object : Callback<ResponseStores> {
            override fun onResponse(call: Call<ResponseStores>, response: Response<ResponseStores>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        storeListLiveData.clear()
                        Log.d("viewmodel_getGroupStores", "Successful response: ${response}")
                        for(data in responseBody.stores) {
                            storeListLiveData.add(RestItem(data.storeName,data.address,data.storeProfileImg,data.userProfileImg,data.storeId,data.groupListId))
                        }
                        callback(1,responseBody.hasNext)
                    } else callback(3,false)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else {
                    Log.e("viewmodel_getGroupStores", "Unsuccessful response: ${response}")
                    callback(3,false)
                }
            }
            override fun onFailure(call: Call<ResponseStores>, t: Throwable) {
                Log.e("viewmodel_getGroupStores", "Failed to make the request", t)
                callback(3,false)
            }
        })
    }
    // 그룹 내 루트 목록
    fun getGroupRoutes(lastRouteId : Long?, callback: (Int,ArrayList<GroupItem>?, Boolean) -> Unit){
        service.getGroupRoutes(xAuthToken,currentGroupId,lastRouteId).enqueue(object : Callback<ResponseRoutes> {
            override fun onResponse(call: Call<ResponseRoutes>, response: Response<ResponseRoutes>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.d("viewmodel2", "Successful response: ${responseBody}")
                        val tmpRouteList = ArrayList<GroupItem>()
                        for(data in responseBody.result) {
                            tmpRouteList.add(GroupItem(data.routeId, data.routeName, 0, true,data.numStore, 0))
                        }
                        callback(1,tmpRouteList,responseBody.hasNext)
                    } else callback(2,null,false)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3,null,false)
                }
            }
            override fun onFailure(call: Call<ResponseRoutes>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3,null,false)
            }
        })
    }
    // 그룹 내 식당 추가
    fun addGroupStore(storeId : Long, callback: (Int) -> Unit){
        Log.e("viewmodel add group",storeId.toString())
        service.addGroupStore(xAuthToken,currentGroupId,StoredId(storeId)).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "addGroupStore response: ${response}")
                    callback(1)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
    // 초대 코드로 그룹 정보 조회
    fun checkGroup(code: String, callback: (Int, ResponseCheckGroup?) -> Unit){
        service.checkGroup(xAuthToken, RequestCheckGroup(code)).enqueue(object : Callback<ResponseCheckGroup> {
            override fun onResponse(call: Call<ResponseCheckGroup>, response: Response<ResponseCheckGroup>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        callback(1, responseBody)
                    } else callback(3,null)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else if(response.code()==404) {
                    callback(2,null)
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3,null)
                }
            }
            override fun onFailure(call: Call<ResponseCheckGroup>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3,null)
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
    fun getGroupMembers(lastMemberId : Int?, callback: (Int, Boolean) -> Unit){
        Log.e("token",xAuthToken)
        service.getGroupMembers(xAuthToken,currentGroupId,lastMemberId).enqueue(object : Callback<ResponseGroupMembers> {
            override fun onResponse(call: Call<ResponseGroupMembers>, response: Response<ResponseGroupMembers>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        followList = responseBody.groupMembers
                        Log.e("viewmodel", "Successful response: ${response}")
                        Log.d("viewmodel", "Successful response: ${responseBody}")
                        callback(1,responseBody.hasNext)
                    } else {
                        callback(2,false)
                        Log.e("viewmodel", "Unsuccessful response: ${response}")
                    }
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(2,false)
                }
            }
            override fun onFailure(call: Call<ResponseGroupMembers>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(2,false)
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
        val data = RequestCreateReview(skipCheck,myStoreDetail?.storeId!!.toLong(),visitedAt,menuName,hashTagId,taste,comment,publishCheck)
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                    if (responseBody != null) {
                        Log.d("getFeedReview", "Successful response: ${response}")
                        currentFeedData = responseBody
                        Log.d("currentFeedData", currentFeedData.toString())
                        callback(1)
                    } else {
                        Log.e("getFeedReview success", "Unsuccessful response: ${response}")
                        callback(3)
                    }
                } else if(response.code() == 403204){
                    Log.d("feed private", "해당 리뷰는 private입니다")
                }
                else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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

    // 팔로잉,팔로우 조회
    fun getFollow(followId : Int?, option : Int, callback: (Int, Boolean) -> Unit){
        if(option == 0) {
            service.getFollowing(xAuthToken,followId).enqueue(object : Callback<ResponseFollowMembers> {
                override fun onResponse(call: Call<ResponseFollowMembers>, response: Response<ResponseFollowMembers>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody!=null) {
                            Log.e("viewmodel", "Unsuccessful response: ${responseBody}")
                            followList = responseBody.result
                            callback(1,responseBody.hasNext)
                        }else {
                            callback(2,false)
                        }
                    } else if(response.code()==403) {
                        _tokenToastData.value = Unit
                        refreshToken()
                    } else if(response.code()==404){
                        Log.e("viewmodel", "Unsuccessful response: ${response}")
                        callback(2,false)
                    } else {
                        Log.e("viewmodel", "Unsuccessful response: ${response}")
                        callback(3,false)
                    }
                }
                override fun onFailure(call: Call<ResponseFollowMembers>, t: Throwable) {
                    Log.e("viewmodel", "Failed to make the request", t)
                    callback(3,false)
                }
            })
        } else if(option == 1) {
            service.getFollower(xAuthToken,followId).enqueue(object : Callback<ResponseFollowMembers> {
                override fun onResponse(call: Call<ResponseFollowMembers>, response: Response<ResponseFollowMembers>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody!=null) {
                            followList = responseBody.result
                            callback(1,responseBody.hasNext)
                        }else {
                            callback(2,false)
                        }
                    } else if(response.code()==403) {
                        _tokenToastData.value = Unit
                        refreshToken()
                    } else if(response.code()==404){
                        Log.e("viewmodel", "Unsuccessful response: ${response}")
                        callback(2,false)
                    } else {
                        Log.e("viewmodel", "Unsuccessful response: ${response}")
                        callback(3,false)
                    }
                }
                override fun onFailure(call: Call<ResponseFollowMembers>, t: Throwable) {
                    Log.e("viewmodel", "Failed to make the request", t)
                    callback(3,false)
                }
            })
        }

    }
    // 가게 정보 조회(짧은 화면)
    fun getStoreDetailQuick(storeIds: List<Long>, callback: (Int,List<ResponseStoreDetailQuick>?) -> Unit){
        Log.e("token",xAuthToken)
        service.getStoreDetailQuick(xAuthToken,storeIds.toMutableList()).enqueue(object : Callback<List<ResponseStoreDetailQuick>> {
            override fun onResponse(call: Call<List<ResponseStoreDetailQuick>>, response: Response<List<ResponseStoreDetailQuick>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if(responseBody!=null) {
                        Log.d("viewmodel", "Successful response: ${response}")
                        callback(1,responseBody)
                    } else {
                        Log.e("viewmodel", "Unsuccessful response: ${response}")
                        callback(3,null)
                    }

                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(3,null)
                }
            }
            override fun onFailure(call: Call<List<ResponseStoreDetailQuick>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(3,null)
            }
        })
    }



    //----------------------------------------------------------------------------------------------------------------------------------------------------------//

    //내 위치 장소보기 카테고리 array
    var myMapCategoryList : ArrayList<ResponseMapCategory>? = null
    var myAllCategoryList : ArrayList<ResponseMapCategory> = arrayListOf()


    private val _cateEditFlag = MutableLiveData<Boolean?>(false)
    val cateEditFlag: LiveData<Boolean?>
        get() = _cateEditFlag
    fun changeEditFlag(flag : Boolean?){
        _cateEditFlag.value = flag
    }

    private val _selectedCategory = MutableLiveData<MutableList<Int>>()

    val categorySlist = mutableListOf<Int>(0)
    val selectedCategoryList : LiveData<MutableList<Int>> = _selectedCategory

    init {
        _selectedCategory.value = categorySlist
    }

    fun addItem(item: Int) {
        categorySlist.add(item)
        _selectedCategory.value = categorySlist
    }

    fun removeItem(item: Int) {
        categorySlist.remove(item)
        _selectedCategory.value = categorySlist
    }

    fun clearItem(){
        categorySlist.clear()
        _selectedCategory.value = categorySlist
    }



    fun changeCategoryList(flag : Boolean, data : Int?){
        if(flag){
            _selectedCategory.value!!.add(data!!)
        }
        else{
            if(data == null){
                _selectedCategory.value!!.clear()
            }
            else{
                _selectedCategory.value!!.remove(data)
            }

        }
    }

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

    fun findIconResource(iconId : Int) : Int{
        var iconResource : Int = when(iconId){
            1 -> R.drawable.ic_chat
            2 ->R.drawable.ic_wine
            3 ->R.drawable.ic_taco
            4 ->R.drawable.ic_shrimp
            5 ->R.drawable.ic_rice_cate
            6 ->R.drawable.ic_reserv
            7 ->R.drawable.ic_noodle
            8 ->R.drawable.ic_music
            9 ->R.drawable.ic_moods
            10 ->R.drawable.ic_money
            11 ->R.drawable.ic_likes
            12 ->R.drawable.ic_friends
            13 ->R.drawable.ic_fresh
            14 ->R.drawable.ic_dish
            15 ->R.drawable.ic_cake
            else -> R.drawable.ic_bread
        }
        return iconResource
    }

    // storeFragment에서 사용하는 선택된 카테고리 정보
    var selectedCategoryInfo : ResponseMapCategory? = null

    // 카테고리 추가 -> 확인 완료
    fun addCategory(categoryName : String,categoryIcon : Int, public : Boolean?, desc : String,  callback: (Int) -> Unit){
        var publicString = if(public!!){"PUBLIC"}else{"PRIVATE"}
        var categoryRequestData = RequestAddCategory(myCategoryName = categoryName, myCategoryIcon = categoryIcon, publishCategory = publicString, myCategoryScript = desc )
        Log.d("checking", categoryRequestData.toString())
        service.addCategory(xAuthToken, data = categoryRequestData).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    Log.d("viewmodel", "Successful response: ${response}")
                    callback(0)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
        var categoryRequestData = RequestEditCategory(myCategoryName = categoryName, myCategoryIcon = categoryIcon, publishCategory = public, myCategoryScript = desc)
        Log.d("checking edit", categoryRequestData.toString())
        Log.d("checking edit", categoryId.toString())
        service.editCategory(token = xAuthToken, myCategoryId = categoryId, data = categoryRequestData).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.isSuccessful){
                    Log.d("viewmodel edit", "Successful response: ${response}")
                    callback(0)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
        service.getMapCategory(xAuthToken, townName = townName).enqueue(object :Callback<ArrayList<ResponseMapCategory>>{
            override fun onResponse(
                call: Call<ArrayList<ResponseMapCategory>>,
                response: Response<ArrayList<ResponseMapCategory>>
            ) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", response.body()!!.toString())
                    Log.e("viewmodel", "Successful response: ${response}")
                    myMapCategoryList = response.body()!!
                    callback(0)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else {
                    Log.e("viewmodel", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<ArrayList<ResponseMapCategory>>, t: Throwable) {
                Log.e("viewmodel", "Failed to make the request", t)
                callback(1)
            }

        })
    }


    fun deleteCateogories(data : MutableList<Int>, callback: (Int) -> Unit){
        val requestData = data
        service.deleteCategory2(xAuthToken, requestData).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.e("deleteCateogories", "Successful response: ${response}")
                    callback(0)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else {
                    Log.e("deleteCateogories", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("deleteCateogories", "Failed to make the request", t)
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
                    //myAllCategoryList = response.body()!!
                    callback(0)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
        service.getAllUserCategory(xAuthToken).enqueue(object : Callback<ArrayList<ResponseMapCategory>>{
            override fun onResponse(
                call: Call<ArrayList<ResponseMapCategory>>,
                response: Response<ArrayList<ResponseMapCategory>>
            ) {
                if (response.isSuccessful) {
                    Log.e("getAllUserCategory", "Successful response: ${response}")
                    Log.d("getAllUserCategory", response.body()!!.toString())
                    myAllCategoryList = response.body()!!
                    callback(0)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else {
                    Log.e("getAllUserCategory", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<ArrayList<ResponseMapCategory>>, t: Throwable) {
                Log.e("getAllUserCategory", "Failed to make the request", t)
                callback(1)
            }

        })
    }


    fun getPPMyCategory(categoryId : Int?, callback: (Int, Boolean) -> Unit){
            service.pGetMyCategory(xAuthToken, categoryId).enqueue(object : Callback<ResponsePMyCategory>{
                override fun onResponse(
                    call: Call<ResponsePMyCategory>,
                    response: Response<ResponsePMyCategory>
                ) {
                    if(response.isSuccessful){
                        val body = response.body()
                        if(body!=null){
                            Log.e("getPPMyCategory", "Successful response: ${response}")
                            myAllCategoryList.addAll(body.result)
                            Log.e("getPPMyCategory", myAllCategoryList.toString())
                            callback(1, body.hasNext)
                        }
                        else{
                            callback(2, false)
                        }
                    }
                    else if(response.code() == 403){
                        //추가 예정
                        callback(2, false)
                    }
                    else if(response.code() == 404){
                        Log.e("getPPMyCategory", "Unsuccessful response: ${response}")
                        callback(2, false)
                    }
                }

                override fun onFailure(call: Call<ResponsePMyCategory>, t: Throwable) {
                    Log.e("getPPMyCategory", "Failed to make the request", t)
                    callback(3, false)
                }

            })

    }

    fun getPPOtherCategory(categoryId : Int?, nickname: String, callback: (Int, Boolean) -> Unit){
        service.pGetOtherCategory(xAuthToken, nickname, categoryId).enqueue(object : Callback<ResponsePMyCategory>{
            override fun onResponse(
                call: Call<ResponsePMyCategory>,
                response: Response<ResponsePMyCategory>
            ) {
                if(response.isSuccessful){
                    val body = response.body()
                    if(body!=null){
                        Log.e("getPPOtherCategory", "Successful response: ${response}")
                        myAllCategoryList.addAll(body.result)
                        Log.e("getPPOtherCategory", myAllCategoryList.toString())
                        callback(1, body.hasNext)
                    }
                    else{
                        callback(2, false)
                    }
                }
                else if(response.code() == 403){
                    //추가 예정
                    callback(2, false)
                }
                else if(response.code() == 404){
                    Log.e("getPPOtherCategory", "Unsuccessful response: ${response}")
                    callback(2, false)
                }
            }

            override fun onFailure(call: Call<ResponsePMyCategory>, t: Throwable) {
                Log.e("getPPOtherCategory", "Failed to make the request", t)
                callback(3, false)
            }

        })

    }




    /**
     * 가게 api 함수 - mindy
     */
    var selectedDetailStoreId = 1
    var myMapStoreList : List<ResponseStoreListItem>? = null
    var myAllStoreList : ArrayList<PResponseStoreListItem> = arrayListOf()

    var myStoreDetail : ResponseStoreDetail? = null
    var storeDetailReviews = ArrayList<ResponseReviews>()
    var detailReviewLastId : Long? = null
    var detailReviewLastVisitedAt : String? = null

    var userNickname : String = "Gusto"

    //저장된 맛집
    var mapVisitedList : List<ResponseSavedStoreData>? = null
    var mapUnvisitedList : List<ResponseSavedStoreData>? = null
    var mapVisitedCnt = 0
    var mapUnvisitedCnt = 0


    //가게 삭제
    var selectedStoreIdList : ArrayList<Int> = arrayListOf()



    //가게 카테고리 추가(찜) -> 확인 완, 수정 필요
    fun addPin(categoryId: Long, storeLong: Long, callback: (Int, ResponseAddPin?) -> Unit){
        var pinData = RequestPin(storeId = storeLong)
        service.addPin(xAuthToken, categoryId, pinData).enqueue(object : Callback<ResponseAddPin>{
            override fun onResponse(call: Call<ResponseAddPin>, response: Response<ResponseAddPin>) {
                if (response.isSuccessful) {
                    Log.e("viewmodel", "Successful response: ${response}")
                    callback(0, response.body()!!)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                        for (i in response.body()!!.reviews.result){
                            detailReviewLastId = i.reviewId
                            detailReviewLastVisitedAt = i.visitedAt
                            storeDetailReviews.add(i)
                            Log.d("reviewId check first", i.toString())
                            Log.d("reviewId check first", detailReviewLastId.toString())
                        }
                    }
                    else{
                        Log.d("reviewId check", "more")
                        for (i in response.body()!!.reviews.result){
                            detailReviewLastId = i.reviewId
                            detailReviewLastVisitedAt = i.visitedAt
                            storeDetailReviews.add(i)
                            Log.d("reviewId check more", i.toString())
                            Log.d("reviewId check more", detailReviewLastId.toString())
                        }
                    }
                    callback(0)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                    //myAllStoreList = response.body()!!
                    callback(0)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                    //myAllStoreList = response.body()!!
                    callback(0)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
    private var _allFlag = MutableLiveData<String>("false")
    val allFlag : LiveData<String>
        get() = _allFlag

    fun updateSelectFlag(change : String){
        _allFlag.value = change
    }
    fun deleteStores(data : MutableList<Int>, callback: (Int) -> Unit){
        service.deleteStores(xAuthToken, data).enqueue(object : Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.e("deleteStores", "Successful response: ${response}")
                    callback(0)
                } else {
                    Log.e("deleteStores", "Unsuccessful response: ${response}")
                    callback(1)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("deleteStores", "Failed to make the request", t)
                callback(1)
            }

        })
    }

    // paging api

    fun getPPMyStore(categoryId: Int, pinId : Int?, storeName : String? = null, sort : String? = null,  callback: (Int, Boolean) -> Unit){
        service.ppGetAllMyStores(xAuthToken, categoryId, pinId, sort, storeName).enqueue(object : Callback<PResponseStoreData>{
            override fun onResponse(
                call: Call<PResponseStoreData>,
                response: Response<PResponseStoreData>
            ) {
                if(response.isSuccessful){
                    val body = response.body()
                    if(body!=null){
                        Log.e("getPPMyStore", "Successful response: ${response}")
                        myAllStoreList.addAll(body.result)
                        Log.e("getPPMyStore", myAllStoreList.toString())
                        callback(1, body.hasNext)
                    }
                    else{
                        callback(2, false)
                    }
                }
                else if(response.code() == 403){
                    //추가 예정
                    callback(2, false)
                }
                else if(response.code() == 404){
                    Log.e("getPPMyStore", "Unsuccessful response: ${response}")
                    callback(2, false)
                }
            }

            override fun onFailure(call: Call<PResponseStoreData>, t: Throwable) {
                Log.e("getPPMyCategory", "Failed to make the request", t)
                callback(3, false)
            }

        })

    }

    //paging -> other user store
    fun getPPOtherStore(categoryId: Int, nickname: String, pinId : Int?, callback: (Int, Boolean) -> Unit){
        service.ppGetAllOtherStores(xAuthToken, nickname, categoryId, pinId).enqueue(object : Callback<PResponseStoreData>{
            override fun onResponse(
                call: Call<PResponseStoreData>,
                response: Response<PResponseStoreData>
            ) {
                if(response.isSuccessful){
                    val body = response.body()
                    if(body!=null){
                        Log.e("getPPOtherStore", "Successful response: ${response}")
                        myAllStoreList.addAll(body.result)
                        Log.e("getPPOtherStore", myAllStoreList.toString())
                        callback(1, body.hasNext)
                    }
                    else{
                        callback(2, false)
                    }
                }
                else if(response.code() == 403){
                    //추가 예정
                    callback(2, false)
                }
                else if(response.code() == 404){
                    Log.e("getPPOtherStore", "Unsuccessful response: ${response}")
                    callback(2, false)
                }
            }

            override fun onFailure(call: Call<PResponseStoreData>, t: Throwable) {
                Log.e("getPPOtherStore", "Failed to make the request", t)
                callback(3, false)
            }

        })

    }

    //---------------
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
    private var _successFlag = MutableLiveData<Boolean>(false)
    val successFlg : LiveData<Boolean>
        get() = _successFlag

    fun changeReviewFlag(sign : Boolean){
        _successFlag.value = sign
    }
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
    fun editReview(reviewId : Long, img : String?, menuName : String?, taste : Int, spiceness : Int, mood : Int, toilet : Int, parking : Int, comment : String?,publish : Boolean, callback: (Int) -> Unit){
        var requestBody = RequestMyReview(menuName = menuName, taste = taste, spiciness = spiceness, mood = mood, toilet = toilet, parking = parking, comment = comment,publicCheck=publish)
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                    }
                    mapKeepStoreIdArray = mapSearchStoreIdArray
                    callback(0)

                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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

                    } else if(response.code()==403) {
                        _tokenToastData.value = Unit
                        refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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

    //내 카테고리 전체 조회 + 카테고리 담기
//    fun getMyMapCategory(townName: String, callback: (Int) -> Unit) {
//        service.getMapCategory(xAuthToken, townName = townName).enqueue(object : Callback<ArrayList<ResponseMapCategory>> {
//            override fun onResponse(
//                call: Call<ArrayList<ResponseMapCategory>>,
//                response: Response<ArrayList<ResponseMapCategory>>
//            ) {
//                if (response.isSuccessful) {
//                    Log.e("viewmodel", response.body()!!.toString())
//                    Log.e("viewmodel", "Successful response: ${response}")
//                    myMapCategoryList = response.body()!! // 서버에서 받아온 카테고리 목록을 저장
//                    callback(0)
//                } else {
//                    Log.e("viewmodel", "Unsuccessful response: ${response}")
//                    callback(1)
//                }
//            }
//
//            override fun onFailure(call: Call<ArrayList<ResponseMapCategory>>, t: Throwable) {
//                Log.e("viewmodel", "Failed to make the request", t)
//                callback(1)
//            }
//
//        })
//    }

    fun logout(callback: (Int) -> Unit) {
        service.logout(xAuthToken,refreshToken).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel", "Successful response: ${response}")
                    callback(1)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
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
    fun unregister(callback: (Int) -> Unit) {
        service.unregister(xAuthToken).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("viewmodel UNREGISTER", "Successful response: ${response}")
                    callback(1)
                } else if(response.code()==403) {
                    _tokenToastData.value = Unit
                    refreshToken()
                } else {
                    Log.e("viewmodel UNREGISTER", "Unsuccessful response: ${response}")
                    callback(2)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("viewmodel UNREGISTER", "Failed to make the request", t)
                callback(2)
            }
        })
    }
}