package com.example.zentrix.domain.model

import androidx.compose.ui.graphics.Color

data class Product(
    val name: String, val brand: String, val price: String,
    val originalPrice: String?, val rating: Float,
    val cardGradient: List<Color>, val accentColor: Color,
    val isFavorite: Boolean = false, val isNew: Boolean = false,
    val discount: String? = null
)
