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
    data class ProductDetails(
        val productId : String,
        val category: String
    ) : Screen
}