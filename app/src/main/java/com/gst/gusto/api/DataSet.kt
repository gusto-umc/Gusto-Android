package com.gst.gusto.api

import com.google.gson.annotations.SerializedName
import java.util.Date

// 내 루트 조회
data class Routes(
    @SerializedName("routeId") val routeId : Long,
    @SerializedName("routeName") val routeName : String,
    @SerializedName("numStore") val numStore : Int,
    @SerializedName("publishRoute") val publishRoute : Boolean
)
data class ResponseRoutes(
    @SerializedName("result") val result : List<Routes>,
    @SerializedName("hasNext") val hasNext : Boolean
)

// 루트 생성
data class RequestCreateRoute(
    @SerializedName("routeName") val routeName : String,
    @SerializedName("publishRoute") val publishRoute : Boolean,
    @SerializedName("routeList") val routeList : List<RouteList>
)
data class RequestEditRoute(
    @SerializedName("routeName") val routeName : String,
    @SerializedName("routeList") val routeList : List<RouteList>?
)
data class RouteList(
    @SerializedName("storeId") val storeId : Long,
    @SerializedName("ordinal") val ordinal : Int,
    // 루트 상세 조회
    @SerializedName("routeListId") val routeListId : Long?,
    @SerializedName("storeName") val storeName : String?,
    @SerializedName("address") val address : String?,
    // 루트 지도 조회
    @SerializedName("longitude") val longitude : Double?,
    @SerializedName("latitude") val latitude : Double?
)

// 루트 상세 조회
data class ResponseRouteDetail(
    @SerializedName("routeId") val routeId : Long,
    @SerializedName("routeName") val routeName : String,
    @SerializedName("publishRoute") val publishRoute : Boolean,
    @SerializedName("routes") val routes : List<RouteList>
)
data class StoredId(
    @SerializedName("storeId") val storeId : Long
)

// 그룹 조회
data class ResponseGetGroups(
    @SerializedName("groups") val groups : List<ResponseGetGroup>,
    @SerializedName("hasNext") val hasNext : Boolean
)
data class ResponseGetGroup(
    @SerializedName("groupId") val groupId : Long,
    @SerializedName("groupName") val groupName : String,
    @SerializedName("isOwner") val isOwner : Boolean,
    @SerializedName("numMembers") val numMembers : Int,
    @SerializedName("numRestaurants") val numRestaurants : Int,
    @SerializedName("numRoutes") val numRoutes : Int
)
// 그룹 가게 정보
data class Store(
    @SerializedName("storeName") val storeName : String,
    @SerializedName("storeId") val storeId : Long,
    @SerializedName("storeProfileImg") val storeProfileImg : String,
    @SerializedName("userProfileImg") val userProfileImg : String,
    @SerializedName("address") val address : String,
    @SerializedName("groupListId") val groupListId : Long
)

data class ResponseStores(
    @SerializedName("result") val stores :List<Store>,
    @SerializedName("hasNext") val hasNext: Boolean
)

// 그룹 조회
data class ResponseGroup(
    @SerializedName("groupId") val groupId : Long,
    @SerializedName("groupName") val groupName : String,
    @SerializedName("groupScript") val groupScript : String,
    @SerializedName("owner") val owner : Int,
    @SerializedName("notice") val notice : String,
    @SerializedName("groupMembers") val groupMembers : List<Member>,

    // 초대 코드로 그룹 정보 조회
    @SerializedName("numMembers") val numMembers : Int
)
// 초대 코드로 그룹 정보 조회
data class ResponseCheckGroup(
    @SerializedName("groupName") val groupName : String,
    @SerializedName("groupMembers") val groupMembers : List<String>,
    @SerializedName("numMembers") val numMembers : Int
)
data class Member(
    @SerializedName("groupMemberId") val groupMemberId : Int,
    @SerializedName("nickname") val nickname : String,
    @SerializedName("profileImg") val profileImg : String,

    // 팔로워 조회
    @SerializedName("followId") val followId : Int
)
data class ResponseGroupMembers(
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("groupMembers") val groupMembers : List<Member>
)
data class ResponseFollowMembers(
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("result") val result : List<Member>
)
data class NewOwner(
    @SerializedName("newOwner") val newOwner : Int
)
data class RequestGroupOption(
    @SerializedName("groupName") val groupName : String?,
    @SerializedName("notice") val notice : String?
)
// 그룹 생성
data class RequestCreateGroup(
    @SerializedName("groupName") val groupName : String,
    @SerializedName("groupScript") val groupScript : String
)
// 그룹 참여
data class RequestJoinGroup(
    @SerializedName("code") val code : String
)
// 초대 코드 조회
data class ResoponseInvititionCode(
    @SerializedName("invitationCodeId") val invitationCodeId : Int,
    @SerializedName("groupId") val groupId : Long,
    @SerializedName("code") val code : String
)
//
data class RequestCheckGroup(
    @SerializedName("code") val code : String
)
// 프로필 조회
data class ResponseProfile(
    @SerializedName("nickname") val nickname : String,
    @SerializedName("profileImg") val profileImg : String,
    @SerializedName("review") val review : Int,
    @SerializedName("following") val following : Int,
    @SerializedName("follower") val follower : Int,
    @SerializedName("followed") val followed : Boolean
)

// list (timeline) review Request-Body
data class RequestBodyListReview(
    @SerializedName("reviewId") val reviewId: Long,
    @SerializedName("size") val size: Int
)


// list (timeline) review 조회- reviews
data class ResponseListReviews(
    @SerializedName("reviewId") val reviewId: Long,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("visitedAt") val visitedAt: Date,
    @SerializedName("visitedCount") val visitedCount: Int,
    @SerializedName("images") val images: List<String>,
    )

// list (timeline) review 조회
data class ResponseListReview(
    @SerializedName("reviews") val reviews: List<ResponseListReviews>,
    @SerializedName("hasNext") val hasNext: Boolean
)

//카테고리 추가, 수정 request body
data class RequestAddCategory(
    @SerializedName("myCategoryName") var myCategoryName : String,
    @SerializedName("myCategoryIcon") var myCategoryIcon : Int,
    @SerializedName("publishCategory") var publishCategory : String,
    @SerializedName("myCategoryScript") var myCategoryScript : String = ""
)

data class RequestEditCategory(
    @SerializedName("myCategoryName") var myCategoryName : String,
    @SerializedName("myCategoryIcon") var myCategoryIcon : Int,
    @SerializedName("publishCategory") var publishCategory : String?,
    @SerializedName("myCategoryScript") var myCategoryScript : String
)



//카테고리 조회(내 위치 장소 보기)
data class ResponseMapCategory(
    @SerializedName("myCategoryId") val myCategoryId : Int,
    @SerializedName("myCategoryName") var categoryName : String,
    @SerializedName("myCategoryIcon") var categoryIcon : Int,
    @SerializedName("publishCategory") var publishCategory : Boolean,
    @SerializedName("userPublishCategory") val userPublishCategory : Boolean,
    @SerializedName("myCategoryScript") var myCategoryScript : String?,
    @SerializedName("pinCnt") var pinCnt : Int
)

// paging 내 카테고리 전체 조회
data class ResponsePMyCategory(
    @SerializedName("hasNext") val hasNext : Boolean,
    @SerializedName("result") val result : ArrayList<ResponseMapCategory>
)

//카테고리 조회- 마이, 피드
data class ResponseAllCategory(
    @SerializedName("myCategoryId") val myCategoryId : Int,
    @SerializedName("myCategoryName") var categoryName : String,
    @SerializedName("myCategoryIcon") var categoryIcon : Int,
    @SerializedName("publishCategory") var publishCategory : String,
    @SerializedName("pinCnt") var pinCnt : Int
)

//가게 찜 추가
data class RequestPin(
    @SerializedName("storeId") val storeId : Long
)

//가게 상세 조회
data class ResponseStoreDetail(
    @SerializedName("storeId") val storeId : Int,
    @SerializedName("storeName") val storeName : String,
    @SerializedName("categoryString") val categoryString : String,
    @SerializedName("address") val address : String,
    @SerializedName("opening") val opening : Int,
    @SerializedName("pin") var pin : Boolean,
    @SerializedName("pinId") var pinId : Int,
    @SerializedName("reviewImg4") val reviewImg4: ArrayList<String>,
    @SerializedName("reviews") val reviews : ResultReviews
)

//가게 상세 리뷰 -> 수정 필요
data class ResponseReviews(
    @SerializedName("reviewId") val reviewId : Long,
    @SerializedName("visitedAt") val visitedAt : String,
    @SerializedName("profileImage") val profileImage : String,
    @SerializedName("nickname") val nickname : String,
    @SerializedName("liked") val liked : Int,
    @SerializedName("comment") val comment : String?,
    @SerializedName("img1") val img1 : String?,
    @SerializedName("img2") val img2 : String?,
    @SerializedName("img3") val img3 : String?,
    @SerializedName("img4") val img4 : String?
)

data class ResultReviews(
    @SerializedName("hasNext") val hasNext : Boolean,
    @SerializedName("result") val result : MutableList<ResponseReviews>
)

//가게 조회
data class ResponseStoreListItem(
    @SerializedName("storeId") val storeId : Int,
    @SerializedName("storeName") val storeName  :String,
    @SerializedName("address") val address : String,
    @SerializedName("reviewCnt") var reviewCnt : Int,
    @SerializedName("reviewImg") val reviewImg : String?
)

data class PResponseStoreListItem(
    @SerializedName("pinId") val pinId : Int,
    @SerializedName("storeId") val storeId : Int,
    @SerializedName("storeName") val storeName  :String,
    @SerializedName("address") val address : String,
    @SerializedName("reviewCnt") var reviewCnt : Int,
    @SerializedName("img1") val img1 : String?,
    @SerializedName("img2") val img2 : String?,
    @SerializedName("img3") val img3 : String?
)

// 저장된 가게 response

data class PResponseStoreData(
    @SerializedName("hasNext") val hasNext: Boolean,
    @SerializedName("result") val result : ArrayList<PResponseStoreListItem>
)
data class ResponseSavedStore(
    @SerializedName("nickname") val nickname : String,
    @SerializedName("numPinStores") val numPinStores : Int,
    @SerializedName("visitedStores") val visitedStores : List<ResponseVisitedStoreData>,
    @SerializedName("unvisitedStores") val unvisitedStores : List<ResponseUnvisitedStoreData>
)

data class ResponseUnvisitedStoreData(
    @SerializedName("numPinStores") val numPinStores : Int,
    @SerializedName("unvisitedStores") val unvisitedStores : List<ResponseSavedStoreData>
)

data class ResponseVisitedStoreData(
    @SerializedName("numPinStores") val numPinStores : Int,
    @SerializedName("visitedStores") val visitedStores : List<ResponseSavedStoreData>
)

data class ResponseSavedStoreData(
    @SerializedName("storeId") val storeId : Int,
    @SerializedName("categoryName") val categoryName : String?,
    @SerializedName("storeName") val storeName : String,
    @SerializedName("address") val address : String,
    @SerializedName("reviewImg") val reviewImg : String?
)
//리뷰 상세
data class ResponseMyReview(
    @SerializedName("storeId") val storeId : Long,
    @SerializedName("storeName") val storeName : String,
    @SerializedName("visitedAt") var visitedAt : String?,
    @SerializedName("images") val img : List<String>?,
    @SerializedName("menuName") val menuName : String?,
    @SerializedName("hashTags") val hashTags : List<Int>?,
    @SerializedName("taste") val taste : Int,
    @SerializedName("comment") val comment : String?,
    @SerializedName("likeCnt") val likeCnt : Int,
    @SerializedName("publicCheck") val publicCheck : Boolean
)

data class ResponseMyReview2(
    @SerializedName("storeId") val storeId : Long
)

//리뷰 수정완 -> 확인 완
data class RequestMyReview(
    @SerializedName("menuName") val menuName : String?,
    @SerializedName("taste") val taste : Int,
    @SerializedName("spiciness") val spiciness : Int?,
    @SerializedName("mood") val mood : Int?,
    @SerializedName("toilet") val toilet : Int?,
    @SerializedName("parking") val parking : Int?,
    @SerializedName("comment") val comment : String?,
    @SerializedName("publicCheck") val publicCheck : Boolean
)

// 리뷰 작성
data class RequestCreateReview(
    @SerializedName("skipCheck") val skipCheck : Boolean,
    @SerializedName("storeId") val storeId : Long,
    @SerializedName("visitedAt") val visitedAt : String?,
    @SerializedName("menuName") val menuName : String?,
    @SerializedName("hashTagId") val hashTagId : List<Long>?,
    @SerializedName("taste") val taste : Int?,
    //@SerializedName("spiciness") val spiciness : Int?,
    //@SerializedName("mood") val mood : Int?,
    //@SerializedName("toilet") val toilet : Int?,
    //@SerializedName("parking") val parking : Int?,
    @SerializedName("comment") val comment : String?,
    @SerializedName("publicCheck") val publicCheck : Boolean
)

//검색결과
data class ResponseSearch(
    @SerializedName("storeId") val storeId : Long,
    @SerializedName("storeName") val storeName : String,
    @SerializedName("categoryName") val categoryName : String?,
    @SerializedName("reviewImg") val reviewImg : String?,
    @SerializedName("address") val address : String
)

// 가게 정보 조회(짧은 화면)
data class ResponseStoreDetailQuick(
    @SerializedName("pinId") var pinId : Long,
    @SerializedName("storeId") val storeId : Long,
    @SerializedName("storeName") val storeName : String,
    @SerializedName("address") val address : String,
    @SerializedName("longitude") val longitude : Double,
    @SerializedName("latitude") val latitude : Double,
    //@SerializedName("businessDay") val businessDay : Double,
    @SerializedName("contact") val contact : String,
    @SerializedName("reviewImg3") val reviewImg3 : List<String>,
    @SerializedName("pin") var pin : Boolean
)

data class ResponseAddPin(
    @SerializedName("pinId") var pinId: Int
)

// 카카오 행정구역
data class RegionInfoResponse(
    @SerializedName("meta") val meta: Meta,
    @SerializedName("documents") val documents: List<RegionDocument>
)

data class AuthResponse(
    @SerializedName("id") val id: String,
    @SerializedName("result") val result: AuthResult,
    @SerializedName("errMsg") val errMsg: String,
    @SerializedName("errCd") val errCd: Int,
    @SerializedName("trId") val trId: String
)
data class NewRegionInfoResponse(
    @SerializedName("id") val id: String,
    @SerializedName("result") val result: List<RegionInfo>,
    @SerializedName("errMsg") val errMsg: String,
    @SerializedName("errCd") val errCd: Int,
    @SerializedName("trId") val trId: String
)

data class RegionInfo(
    @SerializedName("sgg_cd") val sggCode: String,
    @SerializedName("adm_dr_cd") val admDrCode: String,
    @SerializedName("emdong_cd") val emdongCode: String,
    @SerializedName("sub_no") val subNo: String,
    @SerializedName("full_addr") val fullAddress: String,
    @SerializedName("sido_nm") val sidoName: String,
    @SerializedName("main_no") val mainNo: String,
    @SerializedName("sgg_nm") val sggName: String,
    @SerializedName("emdong_nm") val emdongName: String,
    @SerializedName("sido_cd") val sidoCode: String
)


data class AuthResult(
    @SerializedName("accessTimeout") val accessTimeout: String,
    @SerializedName("accessToken") val accessToken: String
)


data class Meta(
    @SerializedName("total_count") val totalCount: Int
)

data class RegionDocument(
    @SerializedName("region_type") val regionType: String,
    @SerializedName("address_name") val addressName: String,
    @SerializedName("region_1depth_name") val region1DepthName: String,
    @SerializedName("region_2depth_name") val region2DepthName: String,
    @SerializedName("region_3depth_name") val region3DepthName: String,
    @SerializedName("region_4depth_name") val region4DepthName: String?,
    @SerializedName("code") val code: String,
    @SerializedName("x") val longitude: Double,
    @SerializedName("y") val latitude: Double
)

data class ResponseFeedDetail(
    @SerializedName("storeId") val storeId: Long,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("address") val address: String,
    @SerializedName("nickName") val nickName: String,
    @SerializedName("profileImage") val profileImage: String,
    @SerializedName("likeCnt") val likeCnt: Int,
    @SerializedName("likeCheck") val likeCheck: Boolean,
    @SerializedName("images") val images: List<String>,
    @SerializedName("menuName") val menuName: String,
    @SerializedName("hashTags") val hashTags: List<Int>?,
    @SerializedName("taste") val taste: Int,
    @SerializedName("spiciness") val spiciness: Int,
    @SerializedName("mood") val mood: Int,
    @SerializedName("toilet") val toilet: Int,
    @SerializedName("parking") val parking: Int,
    @SerializedName("comment") val comment: String
  )
// cal Review (calendar Review) 조회- reviews
data class ResponseCalReviews(
    @SerializedName("reviewId") val reviewId: Long,
    @SerializedName("visitedDate") val visitedDate: String,
    @SerializedName("images") val images: String
)

// cal Review (calendar Review) 조회
data class ResponseCalReview(
     @SerializedName("reviews") val reviews: List<ResponseCalReviews>
)

// 먹스또 랜덤 피드 | 맛집 & 해시태그 검색 엔진
data class ResponseFeedReview(
    @SerializedName("reviewId") val reviewId: Long,
    @SerializedName("images") val images: String
)

// 맛집 & 해시태그 검색 엔진- reviews
data class ResponseFeedSearchReviews(
    @SerializedName("reviews") val reviews: ArrayList<ResponseFeedReview>
)



// 현재 지역의 카테고리 별 찜한 가게 목록(필터링)
data class LocalCategoryResponse (
    @SerializedName("storeId") val storeId: Int,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("longtitude") val longitude: Double,
    @SerializedName("latitude") val latitude: Double
)

// 회원가입
data class Singup(
    @SerializedName("provider") val provider: String,
    @SerializedName("providerId") val providerId: String,
    @SerializedName("accessToken") val accessToken : String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("age") val age: String,
    @SerializedName("gender") val gender: String
)
data class Login(
    @SerializedName("provider") val provider: String,
    @SerializedName("providerId") val providerId: String,
    @SerializedName("accessToken") val accessToken : String
)
data class Nickname(
    @SerializedName("nickname") val nickname: String
)
//구글 토큰
data class AccessTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("refresh_token") val refreshToken: String?
)

// 소셜 리스트
data class ConnectecSocialListResponse(
    @SerializedName("NAVER") val NAVER: Boolean,
    @SerializedName("KAKAO") val KAKAO: Boolean,
    @SerializedName("GOOGLE") val GOOGLE: Boolean,
)

// 나의 콘텐츠 공개 여부 조회
data class ResponseMyPublishGet(
    @SerializedName("publishReview") val publishReview: Boolean,
    @SerializedName("publishPin") val publishPin: Boolean,
    @SerializedName("publishRoute") val publishRoute: Boolean,
)

// 나의 콘텐츠 공개 여부 변경
data class RequestMyPublish(
    @SerializedName("publishReview") val publishReview: Boolean,
    @SerializedName("publishPin") val publishPin: Boolean,
    @SerializedName("publishRoute") val publishRoute: Boolean,
)

//방문 식당
data class VisitedStoresResponse(
    @SerializedName("pinStores") val pinStores: List<StoreData>,
    @SerializedName("hasNext") val hasNext: Boolean
)

//미방문 식당
data class UnVisitedStoresResponse(
    @SerializedName("pinStores") val pinStores: List<StoreData>,
    @SerializedName("hasNext") val hasNext: Boolean
)


data class StoreData(
    @SerializedName("storeId") val storeId: Int,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("address")val address: String,
    @SerializedName("category") val category: String,
    @SerializedName("reviewImg3") val reviewImg3: List<String>
)