package com.example.iot

import kotlinx.serialization.Serializable
@Serializable
data class Data(
    val soil_moisture: Float,
    val smoke_level: Float,
    val humidity: Int,
    val temperature: Int
)
//data class GeminiRequest(val image: String, val prompt: String)
//data class GeminiResponse(val textOutput: String)

data class GeminiRequest(
    val model: String = "google/gemini-2.0-flash-001", // Specify the model
    val messages: List<Message>
)

data class Message(
    val role: String = "user",
    val content: List<ContentItem>
)

data class ContentItem(
    val type: String,
    val text: String? = null,
    val image_url: ImageUrl? = null
)

data class ImageUrl(
    val url: String
)
data class GeminiResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: MessageContent
)

data class MessageContent(
    val content: String
)

