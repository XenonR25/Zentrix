package com.example.zentrix.domain.model

import com.google.gson.annotations.SerializedName


data class Product(
    @SerializedName("name")
    val name : String,
    @SerializedName("brand")
    val brand : String,
    @SerializedName("price")
    val price : String,
    @SerializedName("originalPrice")
    val originalPrice : String? = null,
    @SerializedName("rating")
    val rating : Float,
    @SerializedName("imageUrl")
    val imageUrl : String,
    @SerializedName("isFavorite")
    val isFavorite : Boolean = false,
    @SerializedName("isNew")
    val isNew : Boolean = false,
    @SerializedName("discount")
    val discount : String? = null
)

data class productRecord(
    @SerializedName("products")
    val products : List<Product>
)

data class JsonBinResponse(
    @SerializedName("record")
    val record : productRecord
)

