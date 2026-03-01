package com.example.zentrix.domain.model

data class FilterState(
    val minPrice: Float? = null,
    val maxPrice: Float? = null,
    val minRating: Float? = null,
    val categories: Set<String> = emptySet(),
    val showNewOnly : Boolean = false,
    val showDiscountedOnly : Boolean = false,
    val sortBy : SortOption = SortOption.NONE
)
enum class SortOption{
    NONE,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    RATING_HIGH_TO_LOW,
    NAME_A_TO_Z,
    NAME_Z_TO_A
}
