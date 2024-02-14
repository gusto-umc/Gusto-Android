package com.gst.gusto.api

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface GustoApi {
    //ROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTE
    @GET("routes/") // 내 루트 조회
    fun getMyRoute(
        @Header("X-AUTH-TOKEN") token : String
    ):Call<List<Routes>>

    @POST("routes/") // 루트 생성 or 그룹 내 루트 추가
    fun createRoute(
        @Header("X-AUTH-TOKEN") token : String,
        @Body requestBody : RequestCreateRoute
    ):Call<ResponseBody>
    @GET("routeLists/{routeId}/order") // 내 or 그룹 루트 거리 조회(공통)
    fun getRouteMap(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long
    ):Call<List<RouteList>>

    @GET("routeLists/{routeId}") // 내 루트 상세 조회
    fun getRouteDetail(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long
    ):Call<ResponseRouteDetail>
    @DELETE("routes/{routeId}") // 내 루트 삭제
    fun deleteRoute(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long
    ):Call<ResponseBody>
    @DELETE("routeLists/{routeListId}") // 루트 내 식당 삭제
    fun deleteRouteStore(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeListId") routeListId : Int
    ):Call<ResponseBody>

    //GROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUP

    @GET("groups") // 그룹 리스트 조회
    fun getGroups(
        @Header("X-AUTH-TOKEN") token : String
    ):Call<List<ResponseGetGroups>>

    @GET("groups/{groupId}/groupLists") // 그룹 내 식당 목록 조회
    fun getGroupStores(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long
    ):Call<List<ResponseStore>>
    @GET("groups/{groupId}/routes/") // 그룹 내 루트 목록 조회
    fun getGroupRoutes(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long
    ):Call<List<Routes>>

    @POST("groups/{groupId}/groupList") // 그룹 내 식당 추가
    fun addGroupStore(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long,
        @Body storedId : StoredId
    ):Call<ResponseBody>

    @GET("groups/{groupId}") // 그룹 1건 조회
    fun getGroup(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long
    ):Call<ResponseGroup>

    @PATCH("groups/{groupId}") // 그룹 세부정보 수정
    fun editGroupOption(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long,
        @Body body : RequestGroupOption
    ):Call<ResponseBody>

    @POST("groups") // 그룹 및 초대코드 생성
    fun createGroup(
        @Header("X-AUTH-TOKEN") token : String,
        @Body body : RequestCreateGroup
    ):Call<ResponseBody>

    @DELETE("groups/{groupId}") // 그룹 삭제
    fun deleteGroup(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long
    ):Call<ResponseBody>

    @DELETE("groups/groupLists?groupListId={groupListId}&groupListId={groupListId}") // 그룹 내 식당 삭제(중복처리가능)
    fun deleteGroupStore(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long,
        @Path("groupListId") groupListId : Long
    ):Call<ResponseBody>

    @PATCH("groups/{groupId}/transfer-ownership") // 그룹 소유권 이전
    fun transferOwnership(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long,
        @Body body : NewOwner
    ):Call<ResponseBody>

    @DELETE("groups/{groupId}/leave") // 그룹 탈퇴
    fun leaveGroup(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long
    ):Call<ResponseBody>

    @POST("groups/{groupId}/join") // 그룹 참여
    fun joinGroup(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long,
        @Body body : RequestJoinGroup
    ):Call<ResponseBody>

    @GET("groups/{groupId}/members") // 그룹 구성원 조회
    fun getGroupMembers(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long
    ):Call<List<Member>>

    @GET("groups/{groupId}/invitationCode") // 그룹 초대코드 조회
    fun getGroupInvitationCode(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long
    ):Call<ResoponseInvititionCode>

    @GET("group/routeLists/{routeId}") // 그룹 루트 상세 조회
    fun getGroupRouteDetail(

    )

    //REVIEWREVIEWREVIEWREVIEWREVIEWREVIEWREVIEWREVIEWREVIEWREVIEWREVIEWREVIEWREVIEW

    @DELETE("reviews/{reviewId}/unlike") // 리뷰 좋아요 취소
    fun unlickReview(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("reviewId") reviewId : Long
    ):Call<ResponseBody>

    @POST("reviews/{reviewId}/like") // 리뷰 좋아요
    fun lickReview(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("reviewId") reviewId : Long
    ):Call<ResponseBody>

    @GET("users/{nickname}/profile") // 먹스또 - 프로필
    fun getUserProfile(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("nickname") nickname : String
    ):Call<ResponseProfile>

    //USERUSERUSERUSERUSERUSERUSERUSERUSERUSERUSERUSERUSERUSERUSERUSERUSERUSERUSER

    @POST("users/follow/{nickname}") // 팔로우하기
    fun follow(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("nickname") nickname : String
    ):Call<ResponseBody>

    @DELETE("users/unfollow/{nickname}") // 언팔로우하기
    fun unFollow(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("nickname") nickname : String
    ):Call<ResponseBody>

    /**
     * 리스트 - 카테고리
     */

    //1. 카테고리 생성
    @POST("myCategories")
    fun addCategory(
        @Header("X-AUTH-TOKEN") token : String,
        @Body body : RequestAddCategory
    ) : Call<Void>

    //2. 카테고리 수정
    @PATCH("myCategories/{myCategoryId}")
    fun editCategory(
        @Header("X-AUTH-TOKEN") token : String,
        @Body body : RequestAddCategory
    ) : Call<Void>

    //3. 카테고리 조회(위치 기반, 내 위치 장소보기)
    @GET("myCategories?townName={townName}")
    fun getMapCategory(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("townName") townName : String
    ) : Call<List<ResponseMapCategory>>

    //4. 카테고리 삭제하기
    @DELETE("myCategories?myCategoryId={myCategoryId}")
    fun deleteCategory(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("myCategoryId") myCategoryId : Int
    ) : Call<Void>

    //5.카테고리 전체 조회 - 피드, 마이
    @GET("myCategories/{nickname}")
    fun getAllCategory(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("nickname") nickname : String
    ) : Call<List<ResponseAllCategory>>

    /**
     * 가게
     */

    //1. 가게 카테고리 추가
    @POST("myCategories/{myCategoryId}/pin")
    fun addPin(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("myCategoryId") myCategoryId : Long,
        @Body body: RequestPin
    ) : Call<Void>

    //2. 가게 카테고리 삭제(찜 취소)
    @DELETE("myCategories/pins?pinId={pinId}")
    fun deletePin(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("pinId") pinId : Long
    ): Call<Void>

    //3. 가게 상세 조회
    @GET("stores/{storeId}/detail?reviewId={reviewId}&visitedAt={visitedAt}")
    fun getStoreDetail(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("storeId") storeId : Long,
        @Path("reviewId") reviewId : Int,
        @Path("visitedAt") visitedAt : String,
    ) : Call<ResponseStoreDetail>

    //4. 카테고리 별 가게 조회 - 위치기반
    @GET("myCategories/pins?myCategoryId={categoryId}&townName={townName}")
    fun getMapStores(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("categoryId") categoryId : Long,
        @Path("townName") townName : String
    ) : Call<List<ResponseStoreListItem>>

    //5. 카테고리 별 가게 조회 - 전체
    @GET("myCategories/pins/{nickname}?myCategoryId={categoryId}")
    fun getAllStores(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("nickname") nickname : String,
        @Path("categoryId") categoryId : Long
    ): Call<List<ResponseStoreListItem>>

    //6. 저장된 맛집 리스트
    @GET("stores/pins?myCategoryId={categoryId}&townName={townName}")
    fun getSavedStores(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("categoryId") categoryId : Int,
        @Path("townName") townName : String
    ) : Call<ResponseSavedStore>


    /**
     * 리뷰
     */

    //1. 리뷰 1건 조회
    @GET("reviews/{reviewId}")
    fun getReview(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("reviewId") reviewId : Long
    ) : Call<ResponseMyReview>

    //2. 리뷰 수정
    @PATCH("reviews/{reviewId}")
    fun editReview(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("reviewId") reviewId : Long,
        @Body body : RequestMyReview
    ) : Call<Void>

    //3. 리뷰 삭제
    @DELETE("reviews/{reviewId}")
    fun deleteReview(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("reviewId") reviewId : Long
    ) : Call<Void>

    /**
     * 검색
     */

    //1. 검색 결과

}
