package com.example.zentrix.domain.model

import androidx.compose.ui.graphics.Color

data class PromoItem(
    val title: String, val subtitle: String, val badge: String,
    val gradientColors: List<Color>, val accentColor: Color
)