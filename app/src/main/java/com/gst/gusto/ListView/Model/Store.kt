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