package com.example.zentrix.features.landing.home

import android.os.Message
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zentrix.data.repository.NetworkResult
import com.example.zentrix.data.repository.ProductRepositoryImpl
import com.example.zentrix.domain.model.Product
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
                    _uiState.value = _uiState.value.copy(products = result.data, isLoading = false, errorMessage = null)

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


    fun retry(){
        loadProducts()
    }
}