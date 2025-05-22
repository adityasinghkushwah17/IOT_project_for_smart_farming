package com.example.iot

import android.graphics.Bitmap
import android.net.http.HttpException
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.random.Random

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
class iotViewModel : ViewModel() {

    private val _sensorData = MutableStateFlow(Data(45f, 28f, 47, 36))
    val sensorData: StateFlow<Data> = _sensorData

    private val _temperatureData = MutableStateFlow(
        listOf(65f, 68f, 74f, 71f, 69f, 71f)
    )
    val temperatureData: StateFlow<List<Float>> = _temperatureData

    init {
        // fetchSensorData()
        startUpdatingData()
    }


    private val _responseText = MutableStateFlow("Waiting for response...")
    val responseText: StateFlow<String> = _responseText


    fun sendToGemini(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Convert Bitmap to Base64
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val imageBytes = byteArrayOutputStream.toByteArray()
                val encodedImage =
                    Base64.encodeToString(imageBytes, Base64.NO_WRAP) // Avoid line breaks

                val promptText = "What is in this image?"

                // Construct the request
                val request = GeminiRequest(
                    messages = listOf(
                        Message(
                            content = listOf(
                                ContentItem(type = "text", text = promptText),
                                ContentItem(
                                    type = "image_url",
                                    image_url = ImageUrl("data:image/jpeg;base64,$encodedImage")
                                )
                            )
                        )
                    )
                )

                // Call API
                val response = RetrofitClient.apiService.sendImageToGemini(request)

                // Process Response
                if (response.isSuccessful) {
                    val responseText = response.body()?.choices?.firstOrNull()?.message?.content
                        ?: "No response from API"
                    val cleanedResponse = responseText.substringBefore("```json")
                        .trim() // Remove everything after "```json"

                    _responseText.value = cleanedResponse
                    Log.d("API Response", "Full Response: $responseText")

                    Log.d("API Response", _responseText.value)
                } else {
                    _responseText.value = "API Error: ${response.errorBody()?.string()}"
                    Log.e("API Error", _responseText.value)
                }
            } catch (e: Exception) {
                _responseText.value = "Exception: ${e.message}"
                Log.e("Exception", _responseText.value)
            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private fun fetchSensorData() {
        viewModelScope.launch {
            while (isActive) {
                fetchStrings()
                delay(1000)
            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private suspend fun fetchStrings() {
        try {
            val data = RetrofitInstance.api.fetchdata() // Directly calling the API
            _sensorData.value = data // Updating LiveData/StateFlow
            Log.d("Retrofit", "Fetched data: $data")
        } catch (e: IOException) {
            Log.e("Retrofit", "Network error: ${e.message}") // Handle no internet
        } catch (e: HttpException) {
            Log.e("Retrofit", "Server error: ${e.message}") // Handle HTTP errors (404, 500)
        } catch (e: Exception) {
            Log.e("Retrofit", "Unexpected error: ${e.message}")
        }
    }

    fun startUpdatingData() {
        viewModelScope.launch {
            while (isActive) {
                val newData = generateRandomData()
                _sensorData.value = newData // Updating StateFlow with new values
                delay(2000) // Updates every 2 seconds
            }
        }
    }

    private fun generateRandomData(): Data {
        return Data(
            soil_moisture = 0.0f, // 10% to 60% moisture
            smoke_level = String.format("%.2f", Random.nextFloat() * (50f - 40f) + 40f).toFloat()
            , // Generates values between 40 and 50 µg/m³

            humidity = Random.nextInt(36, 38), // 30% to 90% humidity
            temperature = 36 // 15°C to 40°C
        )
    }
}
