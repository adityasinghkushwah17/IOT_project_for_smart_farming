package com.example.iot

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import kotlin.jvm.java

object RetrofitInstance {
    private const val BASE_URL =
        "http://192.168.162.218/"

    private val json = Json { ignoreUnknownKeys = true }
    private val contentType = "application/json".toMediaType()

    @OptIn(ExperimentalSerializationApi::class)
    val api: DataApiservice by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Corrected reference
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DataApiservice::class.java)
    }
}


object RetrofitClient {
    private const val BASE_URL = "https://openrouter.ai/api/v1/" // Replace with actual base URL

    val apiService: GApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GApiService::class.java)
    }
}