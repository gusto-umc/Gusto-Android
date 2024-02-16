package com.gst.gusto.api

import com.google.gson.annotations.SerializedName
import java.io.Serial


// 내 루트 조회
data class Routes(
    @SerializedName("routeId") val routeId : Long,
    @SerializedName("routeName") val routeName : String,
    @SerializedName("numStore") val numStore : Int
)

// 루트 생성
data class RequestCreateRoute(
    @SerializedName("routeName") val routeName : String,
    @SerializedName("groupId") val groupId : Long?,
    @SerializedName("routeList") val routeList : List<RouteList>
)
data class RouteList(
    @SerializedName("storeId") val storeId : Long,
    @SerializedName("ordinal") val ordinal : Int,
    // 루트 상세 조회
    @SerializedName("routeListId") val routeListId : Int,
    @SerializedName("storeName") val storeName : String,
    @SerializedName("address") val address : String,
    // 루트 지도 조회
    @SerializedName("longtitude") val longtitude : Double,
    @SerializedName("latitude") val latitude : Double
)
// 루트 상세 조회
data class ResponseRouteDetail(
    @SerializedName("routeName") val routeName : Int,
    @SerializedName("routes") val routes : List<RouteList>
)
data class StoredId(
    @SerializedName("storedId") val storedId : Long
)

// 그룹 조회
data class ResponseGetGroups(
    @SerializedName("groupId") val groupId : Long,
    @SerializedName("groupName") val groupName : String,
    @SerializedName("numMembers") val numMembers : Int,
    @SerializedName("numRestaurants") val numRestaurants : Int,
    @SerializedName("numRoutes") val numRoutes : Int
)
// 그룹 가게 정보
data class ResponseStore(
    @SerializedName("storeName") val storeName : String,
    @SerializedName("storeId") val storeId : Long,
    @SerializedName("storeProfileImg") val storeProfileImg : String,
    @SerializedName("userProfileImg") val userProfileImg : String,
    @SerializedName("address") val address : String,
    @SerializedName("groupListId") val groupListId : Int
)

// 그룹 조회
data class ResponseGroup(
    @SerializedName("groupId") val groupId : Long,
    @SerializedName("groupName") val groupName : String,
    @SerializedName("groupScript") val groupScript : String,
    @SerializedName("owner") val owner : Int,
    @SerializedName("notice") val notice : String,
    @SerializedName("owner") val groupMembers : List<Member>
)
data class Member(
    @SerializedName("groupMemberId") val groupMemberId : Int,
    @SerializedName("nickname") val nickname : String,
    @SerializedName("profileImg") val profileImg : String
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
    @SerializedName("groupId") val groupId : Long,
    @SerializedName("invitationCode") val invitationCode : String
)
// 초대 코드 조회
data class ResoponseInvititionCode(
    @SerializedName("invitationCodeId") val invitationCodeId : Int,
    @SerializedName("groupId") val groupId : Long,
    @SerializedName("code") val code : String
)
// 프로필 조회
data class ResponseProfile(
    @SerializedName("nickname") val nickname : String,
    @SerializedName("review") val review : Int,
    @SerializedName("pin") val pin : Int,
    @SerializedName("follower") val follower : Int,
    @SerializedName("followed") val followed : Boolean
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
    @SerializedName("publishCategory") var publishCategory : String,
    @SerializedName("myCategoryScript") var myCategoryScript : String
)



//카테고리 조회(내 위치 장소 보기)
data class ResponseMapCategory(
    @SerializedName("myCategoryId") val myCategoryId : Int,
    @SerializedName("myCategoryName") var categoryName : String,
    @SerializedName("myCategoryIcon") var categoryIcon : Int,
    @SerializedName("publishCategory") var publishCategory : String,
    @SerializedName("pinCnt") var pinCnt : Int
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
    @SerializedName("categoryName") val categoryName : String,
    @SerializedName("address") val address : String,
    @SerializedName("opening") val opening : Int,
    @SerializedName("pin") var pin : Boolean,
    @SerializedName("reviewImg4") val reviewImg4: List<String>,
    @SerializedName("reviews") val reviews : List<ResponseReviews>
)

//가게 상세 리뷰 -> 수정 필요
data class ResponseReviews(
    @SerializedName("date") val date : String
)

//가게 조회
data class ResponseStoreListItem(
    @SerializedName("storeId") val storeId : Int,
    @SerializedName("storeName") val storeName  :String,
    @SerializedName("address") val address : String,
    @SerializedName("reviewCnt") var reviewCnt : Int,
    @SerializedName("reviewImg") val reviewImg : String
)

// 저장된 가게 response
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
    @SerializedName("categoryName") val categoryName : String,
    @SerializedName("storeName") val storeName : String,
    @SerializedName("address") val address : String,
    @SerializedName("reviewImg") val reviewImg : String
)
//리뷰 상세
data class ResponseMyReview(
    @SerializedName("storeId") val storeId : Long,
    @SerializedName("storeName") val storeName : String,
    @SerializedName("visitedAt") var visitedAt : String?,
    @SerializedName("images") val img : List<String>?,
    @SerializedName("menuName") val menuName : String?,
    @SerializedName("hashTags") val hashTags : String?,
    @SerializedName("taste") val taste : Int,
    @SerializedName("spiciness") val spiciness : Int?,
    @SerializedName("mood") val mood : Int?,
    @SerializedName("toilet") val toilet : Int?,
    @SerializedName("parking") val parking : Int?,
    @SerializedName("comment") val comment : String?,
    @SerializedName("likeCnt") val likeCnt : Int
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
    @SerializedName("comment") val comment : String?
)



