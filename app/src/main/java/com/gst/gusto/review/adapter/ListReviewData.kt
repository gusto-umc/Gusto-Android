package com.gst.gusto.review.adapter

data class ListReviewData(
    var date: String = "",
    var name: String = "",
    var visit: String = "",
    var imageview1: Int = 0,
    var imageview2: Int = 0,
    var imageview3: Int = 0,
    var viewType: Int = ListReviewType.LISTREVIEW
)
