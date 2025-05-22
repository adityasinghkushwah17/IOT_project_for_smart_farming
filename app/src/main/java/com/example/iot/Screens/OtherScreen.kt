package com.example.iot.Screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.iot.BottomNavigationMenu
import com.example.iot.iotViewModel
import kotlinx.coroutines.launch
import org.json.JSONArray

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherScreen(viewModel: iotViewModel, navController: NavController) {
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val responseText = viewModel.responseText.collectAsState().value
    var detections by remember { mutableStateOf<List<Detection>>(emptyList()) }
    val cameraPermission = remember { mutableStateOf(false) }

// Check if the permission is granted
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        cameraPermission.value = isGranted
    }

    LaunchedEffect(Unit) {
        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }
    // Handle image selection
    val captureImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                selectedImage = bitmap
            }
        }
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImage = BitmapFactory.decodeStream(context.contentResolver.openInputStream(it))
            }
        }

    // Parse API response when it updates
    LaunchedEffect(responseText) {
        detections = parseBoundingBoxResponse(responseText)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Object Detection", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        },
        bottomBar = {
            BottomNavigationMenu(selectedItem = "Other", navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF))
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display selected image
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                selectedImage?.let {
                    Image(bitmap = it.asImageBitmap(), contentDescription = "Selected Image")
                } ?: Text("No image selected", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons for capturing/uploading images
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { if (cameraPermission.value) {
                        captureImageLauncher.launch(null)
                    } else {
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    } },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Take Photo", color = Color.White)
                }

                Button(
                    onClick = { pickImageLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Upload Photo", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Analyze Image Button
            OutlinedButton(
                onClick = {
                    selectedImage?.let { image ->
                        viewModel.viewModelScope.launch {
                            viewModel.sendToGemini(image)
                        }
                    }
                },
                border = BorderStroke(1.dp, Color(0xFF2E7D32)),
            ) {
                Text("Analyze Image", color = Color(0xFF2E7D32))
            }

            Spacer(modifier = Modifier.height(20.dp))


            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                item {
                    Text(
                        text = "Analysis Result:",
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Text(
                        text = responseText,
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


// **Data Class for Detection**
data class Detection(val box2d: List<Int>, val label: String)

// **Function to Parse JSON Response**
fun parseBoundingBoxResponse(response: String): List<Detection> {
    val detections = mutableListOf<Detection>()

    try {
        // Extract JSON array from response
        val jsonArrayStart = response.indexOf("[")
        val jsonArrayEnd = response.lastIndexOf("]") + 1
        if (jsonArrayStart != -1 && jsonArrayEnd > jsonArrayStart) {
            val jsonArray = JSONArray(response.substring(jsonArrayStart, jsonArrayEnd))

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val box2dArray = obj.getJSONArray("box_2d")
                val label = obj.getString("label")

                val box2d = List(box2dArray.length()) { index -> box2dArray.getInt(index) }
                detections.add(Detection(box2d, label))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return detections
}

@Preview(showBackground = true)
@Composable
fun otherScreenPreview() {
    val iotviewmodel: iotViewModel = viewModel()
    val fakeNavController = rememberNavController()

    OtherScreen(iotviewmodel, navController = fakeNavController)
}
