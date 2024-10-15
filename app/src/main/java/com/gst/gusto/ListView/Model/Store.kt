package com.gst.gusto.ListView.Model

data class Store(
    var id : Int,
    var storeName : String,
    var location : String,
    var visitCount : Int? = null,
    var serverCategory : String? = null,
    var storePhoto : Int?,
    var isSaved : Boolean? = null
)

data class StoreDetailReview(
    var reviewId : Int,
    var visitedAt : String,
    var nickname : String,
    var liked : Int,
    var comment : String,
    var hashTageName : ArrayList<String>,
    var date : String,
    var photoArray : ArrayList<Int>? = null,
    var profileImg : Int? = null
)

data class StoreDetail(
    var storeId : Int,
    var storeName : String,
    var categoryName : String,
    var address : String,
    var opening : Int,
    var pin : Int,
    var reviewImg : ArrayList<Int>,
    var reviews : ArrayList<StoreDetailReview>,
    var listSize : Int? = 0,
    var totalPage : Int? = 0,
    var totalElements : Int? = 0
)

data class StoreSearch(
    val storeId : Int,
    val storeName : String,
    val categoryName : String,
    val reviewImg : Int
)

