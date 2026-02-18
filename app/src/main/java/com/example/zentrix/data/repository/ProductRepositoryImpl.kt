package com.example.zentrix.data.repository

import com.example.zentrix.data.remote.RetrofitInstance
import com.example.zentrix.domain.model.Product
import com.example.zentrix.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


sealed class NetworkResult<out T>{
    data class Success<T>(val data: T): NetworkResult<T>()
    data class Error(val message: String): NetworkResult<Nothing>()
    object Loading: NetworkResult<Nothing>()
}

class ProductRepositoryImpl @Inject constructor() : ProductRepository {
    private val api = RetrofitInstance.productApi

    override suspend fun fetchProducts(): NetworkResult<List<Product>> {
        return withContext(Dispatchers.IO) {
        try {
            val response = api.getProducts()

            if (response.isSuccessful) {
                val products = response.body()?.record?.products
                if (products != null && products.isNotEmpty()) {
                    NetworkResult.Success(products)
                }
                else {
                    NetworkResult.Error("No products found")
                }
            }
            else {
                NetworkResult.Error("Error: ${response.code()} - ${response.message()}")
            }
        }
            catch (e: Exception){
                NetworkResult.Error("Network Error: ${e.localizedMessage ?: "Unknown Error"}  ")
                }
            }
        }
    }
