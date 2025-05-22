package com.example.iot

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface GApiService {
    @Headers(
        "Authorization: Bearer API_KEY", // Replace with actual API key
        "Content-Type: application/json",
    )
    @POST("chat/completions") // OpenRouter API endpoint
    suspend fun sendImageToGemini(@Body request: GeminiRequest): Response<GeminiResponse>
}
