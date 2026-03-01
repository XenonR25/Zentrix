package com.example.zentrix.data.remote

import com.example.zentrix.domain.model.JsonBinResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProductApi {
    @GET("699fce2b43b1c97be99f0e09")
    suspend fun getProducts() : Response<JsonBinResponse>
}