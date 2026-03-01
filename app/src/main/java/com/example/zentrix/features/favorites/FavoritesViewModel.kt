package com.example.zentrix.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zentrix.data.repository.FirestoreRepository
import com.example.zentrix.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository : FirestoreRepository
): ViewModel() {
    private val _favorites = MutableStateFlow<List<Product>>(emptyList())
    val favorites : StateFlow<List<Product>> = _favorites.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasNewFavorite = MutableStateFlow(false)
    val hasNewFavorite : StateFlow<Boolean> = _favorites.map{ it.isNotEmpty()}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )


    init{
        loadFavoritesFromFirestore()
    }

    private fun loadFavoritesFromFirestore() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.loadFavorites()
                .onSuccess{items ->
                    _favorites.value = items
                }
                .onFailure {e->
                    // Handle error
                    println("Error loading favorites: ${e.message}")

                }
            _isLoading.value = false

        }
    }

    fun toggleFavorite(product : Product){
        val currentFavorites = _favorites.value.toMutableList()
        val existingProduct = currentFavorites.find { it.id == product.id }



            if(existingProduct != null){
                currentFavorites.remove(existingProduct)
                _favorites.value = currentFavorites
                viewModelScope.launch {
                    repository.removeFavorite(product.id)
                }
            } else {
                currentFavorites.add(product)
                _favorites.value = currentFavorites
                _hasNewFavorite.value = true // Set the flag to true
                viewModelScope.launch {
                    repository.addFavorite(product)
                }

            }


    }


    fun removeFavorite(productId: String){
        _favorites.value = _favorites.value.filter { it.id != productId }
        viewModelScope.launch {
            repository.removeFavorite(productId)
        }
    }


}