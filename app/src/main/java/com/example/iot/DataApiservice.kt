package com.example.iot

import okhttp3.Response
import retrofit2.http.GET

interface DataApiservice {
    @GET("data")
    suspend fun fetchdata(): Data
}