package com.example.zentrix.domain.repository

import com.example.zentrix.data.repository.NetworkResult
import com.example.zentrix.domain.model.Product

interface ProductRepository {
    suspend fun fetchProducts(): NetworkResult<List<Product>>
}