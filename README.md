# 🌱 Smart Farming Android App

This is an Android-based smart farming application that integrates **real-time sensor data** from an ESP32-based IoT system and an **AI-powered plant image classification** system using the Gemini model via OpenRouter API.

---

<p float="left">
  <img src="https://github.com/adityasinghkushwah17/IOT_project_for_smart_farming/blob/b4075fa3536fe3b9b26b6009d4b28476cf68cfd0/photo_2025-05-22_14-44-45.jpg?raw=true" width="24%" />
  <img src="https://github.com/adityasinghkushwah17/IOT_project_for_smart_farming/blob/b4075fa3536fe3b9b26b6009d4b28476cf68cfd0/photo_2025-05-22_14-44-38.jpg?raw=true" width="24%" />
</p>


## 📱 Features

### 1. 🔧 Sensor Data Monitoring
- Connects to ESP32 over WiFi using **Retrofit**.
- Displays real-time data from sensors like:
  - 🌡️ **Temperature** and **Humidity** (DHT11/DHT22)
  - 🌱 **Soil Moisture**
  - ☀️ Additional environmental parameters (optional)
- Smooth data refresh with responsive UI using **Jetpack Compose**.

### 2. 🌿 Plant Image Classification
- Allows user to either:
  - 📸 **Capture an image** using the camera
  - 🖼️ **Select an image** from the device gallery
- Sends the image to the **Gemini model** using **OpenRouter API**.
- Receives classification result describing the **type of plant** or relevant characteristics.

---

## 🔌 Technologies Used

| Area                  | Tech Stack                               |
|-----------------------|-------------------------------------------|
| Android Framework     | Kotlin, Jetpack Compose, ViewModel        |
| Networking            | Retrofit, REST API                        |
| Hardware Integration  | ESP32, Soil Moisture Sensor, DHT11/22     |
| AI Model              | Gemini via OpenRouter API                 |
| Image Input           | CameraX / Intent Picker                   |
| State Management      | LiveData / State in Compose               |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- ESP32 board with:
  - DHT11/DHT22 sensor
  - Soil moisture sensor
- A hosted REST API on ESP32 returning sensor values in JSON format
- OpenRouter API key for Gemini model access
