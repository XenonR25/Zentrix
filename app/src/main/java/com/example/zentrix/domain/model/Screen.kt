package com.example.zentrix.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Login : Screen
    @Serializable
    data object Signup : Screen
    @Serializable
    data object Home : Screen

    @Serializable
    data object Profile : Screen

    @Serializable
    data object Cart : Screen
    @Serializable
    data object Favorites : Screen

    @Serializable
    data class ProductDetails(
        val productId : String,
        val name: String,
        val brand: String,
        val price: String,
        val originalPrice: String?,
        val rating: Float,
        val imageUrl: String,
        val isNew: Boolean,
        val discount: String?,
    ) : Screen
}