package com.gst.gusto.api

import com.google.gson.annotations.SerializedName


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
    @SerializedName("routeListId") val routeListId : Int?,
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
    @SerializedName("routes") val routes : List<RouteList>
)
data class StoredId(
    @SerializedName("storeId") val storeId : Long
)

// 그룹 조회
data class ResponseGetGroups(
    @SerializedName("groupId") val groupId : Long,
    @SerializedName("groupName") val groupName : String,
    @SerializedName("numMembers") val numMembers : Int,
    @SerializedName("isOwner") val isOwner : Boolean,
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
    @SerializedName("groupListId") val groupListId : Long
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
    @SerializedName("code") val code : String
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
    @SerializedName("profileImg") val profileImg : String,
    @SerializedName("review") val review : Int,
    @SerializedName("following") val following : Int,
    @SerializedName("follower") val follower : Int,
    @SerializedName("followed") val followed : Boolean
)

// 리뷰 작성
data class RequestCreateReview(
    @SerializedName("storeId") val storeId : Long,
    @SerializedName("visitedAt") val visitedAt : String?,
    @SerializedName("menuName") val menuName : String?,
    @SerializedName("hashTagId") val hashTagId : String?,
    @SerializedName("taste") val taste : Int?,
    @SerializedName("spiciness") val spiciness : Int?,
    @SerializedName("mood") val mood : Int?,
    @SerializedName("toilet") val toilet : Int?,
    @SerializedName("parking") val parking : Int?,
    @SerializedName("comment") val comment : String?
)
// 가게 정보 조회(짧은 화면)
data class ResponseStoreDetailQuick(
    @SerializedName("pinId") val pinId : Long,
    @SerializedName("storeId") val storeId : Long,
    @SerializedName("storeName") val storeName : String,
    @SerializedName("address") val address : String,
    @SerializedName("longitude") val longitude : Double,
    @SerializedName("latitude") val latitude : Double,
    //@SerializedName("businessDay") val businessDay : Double,
    @SerializedName("contact") val contact : String,
    @SerializedName("reviewImg3") val reviewImg3 : List<String>,
    @SerializedName("pin") val pin : Boolean
)

// 카카오 행정구역
data class RegionInfoResponse(
    @SerializedName("meta") val meta: Meta,
    @SerializedName("documents") val documents: List<RegionDocument>
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