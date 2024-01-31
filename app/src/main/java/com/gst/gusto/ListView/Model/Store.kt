package com.gst.gusto.ListView.Model

import android.app.appsearch.StorageInfo
import android.health.connect.datatypes.StepsCadenceRecord

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
    var hashTageName : ArrayList<String>
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