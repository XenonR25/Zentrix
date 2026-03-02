package com.example.zentrix.data.remote

import com.example.zentrix.domain.model.JsonBinResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProductApi {
    @GET("69a55bdbae596e708f57652a")
    suspend fun getProducts() : Response<JsonBinResponse>
}