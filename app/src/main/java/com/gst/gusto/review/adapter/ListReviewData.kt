package com.gst.gusto.review.adapter

data class ListReviewData(
    var date: String = "",
    var name: String = "",
    var visit: String = "",
    var images : ArrayList<String> = ArrayList(),
    var reviewId: Long,
    var viewType: Int = ListReviewType.LISTREVIEW,

)
