package com.example.zentrix.data.remote

import com.example.zentrix.domain.model.JsonBinResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProductApi {
    @GET("699a7e8e43b1c97be9928f92")
    suspend fun getProducts() : Response<JsonBinResponse>
}