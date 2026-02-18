package com.example.zentrix.data.remote

import com.example.zentrix.domain.model.JsonBinResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProductApi {
    @GET("69955a31ae596e708f33798f")
    suspend fun getProducts() : Response<JsonBinResponse>
}