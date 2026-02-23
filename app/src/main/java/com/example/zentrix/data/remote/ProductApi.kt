package com.example.zentrix.data.remote

import com.example.zentrix.domain.model.JsonBinResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProductApi {
    @GET("699bf15bae596e708f4165fa")
    suspend fun getProducts() : Response<JsonBinResponse>
}