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
) {
    val id: String
        get() = if (brand.isNotBlank() && name.isNotBlank()) {
            "${brand}_${name}".replace(" ", "_").lowercase()
        } else {
            "product_${hashCode()}"
        }

}
data class CartItem(
    val product : Product,
    val quantity : Int = 1
)

data class JsonBinResponse(
    @SerializedName("record")
    val record : List<Product>
)

