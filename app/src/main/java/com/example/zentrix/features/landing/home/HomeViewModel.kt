package com.example.zentrix.features.landing.home

import android.os.Message
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zentrix.data.repository.NetworkResult
import com.example.zentrix.data.repository.ProductRepositoryImpl
import com.example.zentrix.domain.model.FilterState
import com.example.zentrix.domain.model.Product
import com.example.zentrix.domain.model.SortOption
import com.example.zentrix.domain.repository.AuthRepository
import com.example.zentrix.domain.repository.ProductRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


data class HomeUiState(
    val products : List<Product> = emptyList(),
    val isLoading : Boolean = false,
    val errorMessage: String? = null
)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _filterState = MutableStateFlow(FilterState())
    val filterState : StateFlow<FilterState> = _filterState.asStateFlow()

    private var allProducts : List<Product> = emptyList()

    init {
        fetchUserName()
        loadProducts()
    }

    private fun fetchUserName(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Firebase.firestore.collection("users").document(uid).get()
            .addOnSuccessListener {doc ->
                _userName.value = doc.getString("name")?.split(" ")?.firstOrNull() ?: ""
            }

    }
    fun loadProducts(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when(val result = repository.fetchProducts()) {
                is NetworkResult.Success -> {
                    allProducts = result.data
                    applyFilters()
                    _uiState.value = _uiState.value.copy( isLoading = false, errorMessage = null)

                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                is NetworkResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }

            }
        }
    }
    fun updateFilter(filterState: FilterState){
        _filterState.value = filterState
        applyFilters()
    }
    fun clearFilters(){
        _filterState.value = FilterState()
        applyFilters()
    }
    private fun applyFilters() {
        var filteredProducts = allProducts
        val filter = _filterState.value

        //Filter by price range
        if (filter.minPrice != null) {
            filteredProducts = filteredProducts.filter { product ->
                val price = product.price.replace("£", "").replace(",", "").toFloatOrNull() ?: 0f
                price >= filter.minPrice
            }
        }

        if (filter.maxPrice != null) {
            filteredProducts = filteredProducts.filter { product ->
                val price = product.price.replace("£", "").replace(",", "").toFloatOrNull() ?: 0f
                price <= filter.maxPrice
            }
        }

        //Filter by rating
        if (filter.minRating != null) {
            filteredProducts = filteredProducts.filter { it.rating >= filter.minRating }
        }
        if (filter.categories.isNotEmpty()) {
            filteredProducts = filteredProducts.filter { product ->
                filter.categories.any { category ->

                    when(category.lowercase()) {
                        "tops" -> product.name.contains("shirt", ignoreCase = true) ||
                                product.name.contains("hoodie", ignoreCase = true) ||
                                product.name.contains("rollneck", ignoreCase = true)

                        "shoes" -> product.name.contains("shoe", ignoreCase = true) ||
                                product.name.contains("runner", ignoreCase = true) ||
                                product.name.contains("loafer", ignoreCase = true) ||
                                product.name.contains("boot", ignoreCase = true)

                        "bags" -> product.name.contains("bag", ignoreCase = true) ||
                                product.name.contains("tote", ignoreCase = true) ||
                                product.name.contains("backpack", ignoreCase = true) ||
                                product.name.contains("crossbody", ignoreCase = true)

                        "watches" -> product.name.contains("watch", ignoreCase = true)

                        "jewellery" -> product.name.contains("jewel", ignoreCase = true) ||
                                product.name.contains("scarf", ignoreCase = true)

                        "denim" -> product.name.contains("denim", ignoreCase = true) ||
                                product.name.contains("chino", ignoreCase = true)

                        "headphone" -> product.name.contains("headphone", ignoreCase = true) ||
                                product.name.contains("earbud", ignoreCase = true) ||
                                product.name.contains("speaker", ignoreCase = true)

                        "mobile" -> product.name.contains("phone", ignoreCase = true) ||
                                product.name.contains("mobile", ignoreCase = true)

                        "laptop" -> product.name.contains("laptop", ignoreCase = true) ||
                                product.name.contains("tablet", ignoreCase = true) ||
                                product.name.contains("keyboard", ignoreCase = true) ||
                                product.name.contains("mouse", ignoreCase = true) ||
                                product.name.contains("webcam", ignoreCase = true) ||
                                product.name.contains("stand", ignoreCase = true) ||
                                product.name.contains("sleeve", ignoreCase = true) ||
                                product.name.contains("hub", ignoreCase = true)

                        "controller" -> product.name.contains("controller", ignoreCase = true) ||
                                product.name.contains("gaming", ignoreCase = true) ||
                                product.name.contains("mouse", ignoreCase = true)


                        else -> product.name.contains(
                            category,
                            ignoreCase = true
                        ) || product.brand.contains(
                            category,
                            ignoreCase = true
                        )
                    }
                }
            }
        }
        //filter new only
        if(filter.showNewOnly){
            filteredProducts = filteredProducts.filter { it.isNew }
            }

        //filter discount only
        if(filter.showDiscountedOnly){
            filteredProducts = filteredProducts.filter { it.discount != null }
        }
        //Sorting
        filteredProducts = when(filter.sortBy){
            SortOption.NONE -> filteredProducts
            SortOption.PRICE_LOW_TO_HIGH -> filteredProducts.sortedBy { it.price.replace("£", "").replace(",", "").toFloatOrNull() ?: 0f }
            SortOption.PRICE_HIGH_TO_LOW -> filteredProducts.sortedByDescending { it.price.replace("£", "").replace(",", "").toFloatOrNull() ?: 0f }
            SortOption.RATING_HIGH_TO_LOW -> filteredProducts.sortedByDescending { it.rating }
            SortOption.NAME_A_TO_Z -> filteredProducts.sortedBy { it.name }
            SortOption.NAME_Z_TO_A -> filteredProducts.sortedByDescending { it.name }
            }
        _uiState.value = _uiState.value.copy(products = filteredProducts)


        }
    fun retry(){
        loadProducts()
    }
}