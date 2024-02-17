package com.gst.gusto.review.adapter

data class ListReviewData(
    var date: String = "",
    var name: String = "",
    var visit: String = "",
    var imageview1: String = "",
    var imageview2: String = "",
    var imageview3: String = "",
    var reviewId: Long,
    var viewType: Int = ListReviewType.LISTREVIEW,

)
