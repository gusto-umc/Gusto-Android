package com.gst.gusto.api

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.util.Date


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
    @SerializedName("groupMembers") val groupMembers : List<Member>
)
data class Member(
    @SerializedName("groupMemberId") val groupMemberId : Int,
    @SerializedName("nickname") val nickname : String,
    @SerializedName("profileImg") val profileImg : String,

    // 팔로워 조회
    @SerializedName("followId") val followId : Int
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

// 리뷰 작성
data class RequestCreateReview(
    @SerializedName("storeId") val storeId : Int,
    @SerializedName("visitedAt") val visitedAt : String?,
    @SerializedName("menuName") val menuName : String?,
    @SerializedName("hashTagId") val hashTagId : String?,
    @SerializedName("taste") val taste : Int?,
    @SerializedName("spiciness") val spiciness : Int?,
    @SerializedName("mood") val mood : Int?,
    @SerializedName("toilet") val toilet : Int?,
    @SerializedName("parking") val parking : Int?,
    @SerializedName("comment") val comment : String?,
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