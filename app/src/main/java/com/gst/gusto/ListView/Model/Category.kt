package com.gst.gusto.ListView.Model

data class CategorySimple(
    var id : Int,
    var categoryName : String,
    var categoryIcon : Int,
    var storeCount : Int
)

data class CategoryDetail(
    var id : Int,
    var categoryName : String,
    var categoryDesc : String?,
    var categoryIcon : Int,
    var isPublic : Boolean
)
