package com.gst.gusto.api

import okhttp3.MultipartBody
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
import retrofit2.http.Query
import java.time.LocalDate


interface GustoApi {
    // TOKEN
    @POST("auth/reissue-token") // 현재 지역의 카테고리 별 찜한 가게 목록(필터링)
    fun refreshToken(
        @Header("X-AUTH-TOKEN") access: String,
        @Header("refresh-Token") refresh: String
    ): Call<ResponseBody>

    //MAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAPMAP
    @GET("stores/map") // 현재 지역의 카테고리 별 찜한 가게 목록(필터링)
    fun getCurrentMapStores(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("townName") townName : String,
        @Query("myCategoryId") myCategoryId : MutableList<Int>?,
        @Query("visited") visited : Boolean?
    ):Call<List<RouteList>>

    //ROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTEROUTE
    @GET("routes") // 내 루트 조회
    fun getMyRoute(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("routeId") routeId : Long?
    ):Call<ResponseRoutes>

    @GET("routes/{nickname}") // 타인의 루트 조회
    fun getOtherRoute(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("nickname") nickname : String,
        @Query("routeId") routeId : Long?
    ):Call<ResponseRoutes>

    @POST("routes") // 루트 생성 or 그룹 내 루트 추가
    fun createRoute(
        @Header("X-AUTH-TOKEN") token : String,
        @Body requestBody : RequestCreateRoute
    ):Call<ResponseBody>

    @POST("routes/{groupId}") // 루트 생성 or 그룹 내 루트 추가
    fun createGroupRoute(
        @Header("X-AUTH-TOKEN") token : String,
        @Body requestBody : RequestCreateRoute,
        @Path("groupId") groupId : Long
    ):Call<ResponseBody>
    @GET("routeLists/{routeId}/order") // 내/그룹 루트 거리 조회(공통)
    fun getRouteMap(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long
    ):Call<List<RouteList>>
    @DELETE("routes/{routeId}") // 내 루트 삭제
    fun deleteRoute(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long
    ):Call<ResponseBody>

    @PATCH("routes/{routeId}") // 루트 수정
    fun editRoute(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long,
        @Body requestEditRoute : RequestEditRoute
    ):Call<ResponseBody>
    @PATCH("routes/{routeId}/publishing/{publishStatus}") // (생성 이후)루트별 공개/비공개 수정
    fun patchPublish(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long,
        @Path("publishStatus") publishStatus : Boolean
    ):Call<ResponseBody>
    @DELETE("routeLists/{routeListId}") // 루트 내 식당(경로) 삭제
    fun deleteRouteStore(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeListId") routeListId : Long
    ):Call<ResponseBody>

    @POST("routeLists/{routeId}") // 루트 내 식당 추가 (공통)
    fun addRouteStore(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long,
        @Body body : List<RouteList>
    ):Call<ResponseBody>

    //GROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUPGROUP

    @GET("groups") // 그룹 리스트 조회
    fun getGroups(
        @Header("X-AUTH-TOKEN") token: String,
        @Query("lastGroupId") lastGroupId: Long?
    ):Call<ResponseGetGroups>

    @GET("groups/{groupId}/groupLists") // 그룹 내 찜 식당 목록 조회
    fun getGroupStores(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long,
        @Query("groupListId") groupListId : Long?
    ):Call<ResponseStores>
    @GET("routes/groups/{groupId}") // 그룹 내 루트 목록
    fun getGroupRoutes(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long,
        @Query("routeId") routeId : Long?
    ):Call<ResponseRoutes>

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

    @POST("groups/check-invitation") // 초대 코드로 그룹 정보 조회
    fun checkGroup(
        @Header("X-AUTH-TOKEN") token : String,
        @Body body : RequestCheckGroup
    ):Call<ResponseCheckGroup>

    @DELETE("groups/{groupId}") // 그룹 삭제
    fun deleteGroup(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long
    ):Call<ResponseBody>

    @DELETE("groups/groupLists") // 그룹 내 식당 삭제(중복처리가능)
    fun deleteGroupStore(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("groupListId") groupListId: Long
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

    @POST("groups/join") // 그룹 참여
    fun joinGroup(
        @Header("X-AUTH-TOKEN") token : String,
        @Body body : RequestJoinGroup
    ):Call<ResponseBody>

    @GET("groups/{groupId}/members") // 그룹 구성원 조회
    fun getGroupMembers(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long,
        @Query("lastMemberId") lastMemberId : Int?
    ):Call<ResponseGroupMembers>

    @GET("groups/{groupId}/invitationCode") // 그룹 초대코드 조회
    fun getGroupInvitationCode(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("groupId") groupId : Long
    ):Call<ResoponseInvititionCode>

    @GET("routeLists/{routeId}") // 내 루트/그룹 루트 상세 조회 (공통)
    fun getGroupRouteDetail(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long?,
        @Query("groupId") groupId : Long?
    ):Call<ResponseRouteDetail>

    @GET("routeLists/{routeId}") // 타인의 루트 상세 조회
    fun getOtherRouteDetail(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long,
        @Query("nickname") nickname : String
    ):Call<ResponseRouteDetail>

    @DELETE("groups/routes/{routeId}") // 그룹 루트 삭제
    fun removeGroupRoute(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("routeId") routeId : Long,
        @Query("groupId") groupId : Long
    ):Call<ResponseBody>

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

    @Multipart
    @POST("reviews") // 리뷰 작성
    fun createReview(
        @Header("X-AUTH-TOKEN") token : String,
        @Part image: List<MultipartBody.Part>?,
        @Part("info") info: RequestCreateReview
    ):Call<ResponseBody>

    @GET("feeds/{reviewId}") // 먹스또 피드 상세 보기
    fun getFeedReview(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("reviewId") reviewId : Long
    ):Call<ResponseFeedDetail>

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

    @GET("reviews/timelineView") // 리뷰 모아보기-3 (timeline view)
    fun timelineView(
        @Header("X-AUTH-TOKEN") token: String,
        @Query("reviewId") reviewId: Long?,
        @Query("size") size: Int
    ):Call<ResponseListReview>

    /**
     * 리스트 - 카테고리
     */

    //1. 카테고리 생성 -> 확인 완
    @POST("myCategories")
    fun addCategory(
        @Header("X-AUTH-TOKEN") token : String,
        @Body data : RequestAddCategory
    ) : Call<Void>

    //2. 카테고리 수정 -> 확인 완
    @PATCH("myCategories/{myCategoryId}")
    fun editCategory(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("myCategoryId") myCategoryId : Long,
        @Body data : RequestEditCategory
    ) : Call<Void>

    //3. 카테고리 조회(위치 기반, 내 위치 장소보기) ->  확인 완
    @GET("myCategories")
    fun getMapCategory(
        @Header("X-AUTH-TOKEN") token: String,
        @Query("townName") townName: String
    ): Call<ResponsePMyCategory>
            //Call<ArrayList<ResponseMapCategory>>

    //4. 카테고리 삭제하기 -> 단 건 삭제 확인 완

    @DELETE("myCategories")
    fun deleteCategory2(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("myCategoryId") myCategoryId : MutableList<Int>
    ) : Call<Void>

    //5.카테고리 전체 조회 - 피드-> 서버 배포 후 다시 확인하기
    @GET("myCategories")
    fun getAllCategory(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("nickname") nickname : String?
    ) : Call<List<ResponseMapCategory>>

    //6.카테고리 전체 조회 - 마이->
    @GET("myCategories")
    fun getAllUserCategory(
        @Header("X-AUTH-TOKEN") token : String
    ) : Call<ArrayList<ResponseMapCategory>>

    //8. 카테고리 전체 조회(paging)
    @GET("myCategories")
    fun pGetMyCategory(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("myCategoryId") myCategoryId : Int?
    ) : Call<ResponsePMyCategory>

    //9. 카테고리 전체조회(타유저)
    @GET("myCategories")
    fun pGetOtherCategory(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("nickname") nickname : String?,
        @Query("myCategoryId") myCategoryId : Int?
    ) : Call<ResponsePMyCategory>

    /**
     * 가게
     */

    //1. 가게 카테고리 추가 -> 확인 완, 보완 필(pinInd)
    @POST("myCategories/{myCategoryId}/pin")
    fun addPin(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("myCategoryId") myCategoryId : Long,
        @Body body: RequestPin
    ) : Call<ResponseAddPin>

    //2. 가게 카테고리 삭제(찜 취소) -> 확인 완
    @DELETE("myCategories/pins")
    fun deletePin(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("pinId") pinId : Int
    ): Call<Void>

    //가게 다중 삭제
    @DELETE("myCategories/pins")
    fun deleteStores(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("pinId") pinId : MutableList<Int>
    ) : Call<Void>

    //3. 가게 상세 조회
    @GET("stores/{storeId}/detail")
    fun getStoreDetail(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("storeId") storeId : Long,
        @Query("visitedAt") visitedAt : String?,
        @Query("reviewId") reviewId : Long?
    ) : Call<ResponseStoreDetail>

    //4. 카테고리 별 가게 조회 - 위치기반 -> 확인 완, 보완 필(pinInd)
    @GET("myCategories/pins")
    fun getMapStores(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("myCategoryId") categoryId : Int,
        @Query("townName") townName : String
    ) : Call<List<ResponseStoreListItem>>

    //5. 카테고리 별 가게 조회 - 전체 -> 확인 완
    @GET("myCategories/pins")
    fun getAllStores(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("nickname") nickname : String?,
        @Query("myCategoryId") categoryId : Int
    ): Call<List<ResponseStoreListItem>>

    //5. 카테고리 별 가게 조회 - 전체 -> 확인 완
    @GET("myCategories/pins")
    fun getAllUserStores(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("myCategoryId") categoryId : Int
    ): Call<List<ResponseStoreListItem>>

    //7. 저장된 맛집 리스트 -> cateogry 적용 X
    @GET("stores/pins")
    fun getSavedStores(
        @Header("X-AUTH-TOKEN") token: String,
        @Query("myCategoryId") categoryId: Int?,
        @Query("townName") townName: String
    ) : Call<List<ResponseSavedStore>>


    // 8. (paging) 카테고리별 내 가게 조회
    @GET("myCategories/pins")
    fun ppGetAllMyStores(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("myCategoryId") categoryId : Int,
        @Query("pinId") pinId : Int?,
        @Query("sort") sort : String?,
        @Query("storeName") storeName: String?
    ): Call<PResponseStoreData>

    // 9. (paging) 카테고리별 타인 가게 조회
    @GET("myCategories/pins")
    fun ppGetAllOtherStores(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("nickname") nickname : String?,
        @Query("myCategoryId") categoryId : Int,
        @Query("pinId") pinId : Int?
    ): Call<PResponseStoreData>



    /**
     * 리뷰 - 연결 완
     */

    //1. 리뷰 1건 조회 -> 확인 완료
    @GET("reviews/{reviewId}")
    fun getReview(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("reviewId") reviewId : Int
    ) : Call<ResponseMyReview>

    //2. 리뷰 수정 -> 확인 완료, 보완필(image 첨부해서 보내기)
    @Multipart
    @PATCH("reviews/{reviewId}")
    fun editReview(
        @Header("X-AUTH-TOKEN") token : String,
        @Path("reviewId") reviewId : Long,
        @Part image: List<MultipartBody.Part>?,
        @Part("info") info: RequestMyReview
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
    @GET("stores/search")
    fun getSearch(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("keyword") keyword : String
    ) : Call<ArrayList<ResponseSearch>>

    @GET("users/follower") // 팔로워 조회
    fun getFollower(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("followId") followerId : Int?
    ):Call<ResponseFollowMembers>
    @GET("users/following") // 팔로잉 조회
    fun getFollowing(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("followId") followerId : Int?
    ):Call<ResponseFollowMembers>

    @GET("reviews/calView") // 리뷰 모아보기 - 2 (cal view)
    fun calView(
        @Header("X-AUTH-TOKEN") token: String,
        @Query("reviewId") reviewId: Long?,
        @Query("size") size: Int,
        @Query("date") date: LocalDate
    ):Call<ResponseCalReview>

    //STORESTORESTORESTORESTORESTORESTORESTORESTORESTORESTORESTORESTORESTORESTORESTORESTORESTORE

    @GET("stores") // 가게 정보 조회(잛은 화면)
    fun getStoreDetailQuick(
        @Header("X-AUTH-TOKEN") token : String,
        @Query("storeId") storeId : MutableList<Long>
    ):Call<List<ResponseStoreDetailQuick>>

    // 행정구역 가져오기
    @GET("v2/local/geo/coord2regioncode.json")
    fun getRegionInfo(
        @Header("Authorization") authorization: String,
        @Query("x") longitude: String,
        @Query("y") latitude: String
    ): Call<RegionInfoResponse>

    @GET("OpenAPI3/auth/authentication.json")
    fun authenticate(
        @Query("consumer_key") consumerKey: String,
        @Query("consumer_secret") consumerSecret: String
    ): Call<AuthResponse>

    @GET("OpenAPI3/addr/rgeocodewgs84.json")
    fun getNewRegionInfo(
        @Query("accessToken") accessToken: String,
        @Query("x_coor") longitude: Double,
        @Query("y_coor") latitude: Double,
        @Query("addr_type") addrType: Int = 21
    ): Call<NewRegionInfoResponse>

    @GET("feeds/search") // 맛집 & 해시태그 검색 엔진
    fun feedSearch(
        @Header("X-AUTH-TOKEN") token: String,
        @Query("keyword") keyword: String,
        @Query("hashTags") hashTags: List<Long>?
    ):Call<ResponseFeedSearchReviews>


    // 현재 지역의 카테고리 별 찜한 가게 목록(필터링)
    @GET("stores/map?townName={townName}&myCategoryId={myCategoryId}&visit={visitedStatus}")
    fun LocalCategory(
        @Query("storeId") storeId: Int,
        @Query("storeName") storeName: String,
        @Query("longtitude") longitude: Double,
        @Query("latitude") latitude: Double
    ): Call<LocalCategoryResponse>

    //나의 콘텐츠 공개 여부 조회
    @GET("users/my-info/publishing")
    fun myPublishGet(
        @Header("X-AUTH-TOKEN") token: String
    ): Call<ResponseMyPublishGet>

    //나의 콘텐츠 공개 여부 변경
    @PATCH("users/my-info/publishing")
    fun myPublishSet(
        @Header("X-AUTH-TOKEN") token: String,
        @Body data: RequestMyPublish
    ): Call<ResponseBody>

    // 로그아웃
    @POST("users/sign-out")
    fun logout(
        @Header("X-AUTH-TOKEN") xtoken: String,
        @Header("refresh-token") rtoken: String
    ): Call<ResponseBody>

    // 회원탈퇴
    @DELETE("users/my")
    fun unregister(
        @Header("X-AUTH-TOKEN") xtoken: String
    ): Call<ResponseBody>

    // 연결된 소셜 서버 목록
    @GET("users/social-accounts")
    fun getConnectedSocialList(
        @Header("X-AUTH-TOKEN") token: String
    ): Call<ConnectecSocialListResponse>

    //탭바 저장된 음식점 수정
    //방문식당 조회
    @GET("stores/pins/visited")
    fun getVisitedStores(
        @Header("X-AUTH-TOKEN") xtoken: String,
        @Query("myCategoryId") categoryId: Int?,
        @Query("townName") townName: String,
        @Query("lastStoreId") lastStoreId: Long? = null
    ): Call<VisitedStoresResponse>

    //미방문 식당 조회
    @GET("stores/pins/unvisited")
    fun getUnvisitedStores(
        @Header("X-AUTH-TOKEN") xtoken: String,
        @Query("myCategoryId") categoryId: Int?,
        @Query("townName") townName: String,
        @Query("lastStoreId") lastStoreId: Long? = null
    ): Call<UnVisitedStoresResponse>

}
