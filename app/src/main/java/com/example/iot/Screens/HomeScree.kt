package com.example.iot.Screens


import android.os.Build
import androidx.annotation.RequiresExtension
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.iot.BottomNavigationMenu
import com.example.iot.Data
import com.example.iot.iotViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: iotViewModel, navController: NavController) {
    val sensorData by viewModel.sensorData.collectAsState()
    val temperatureData by viewModel.temperatureData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Dashboard", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF2E7D32))
            )
        },
        bottomBar = {
            BottomNavigationMenu(selectedItem = "Home", navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEFEFEF))
                .padding(innerPadding)
        ) {
            TemperatureChart(temperatureData)
            Spacer(modifier = Modifier.height(16.dp))
            SensorGrid(sensorData)
        }
    }
}



@Composable
fun SensorGrid(sensorData: Data) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            SensorCard("Soil Moisture", "${((sensorData.soil_moisture))}%", Color.Black)
            SensorCard("Temperature", "${sensorData.temperature}°C", Color.Black)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            SensorCard("Humidity", "${sensorData.humidity}%", Color.Black) // Highlighted Red
            SensorCard("Smoke level", "${sensorData.smoke_level} ug/m3", Color.Black)
        }
    }
}
@Composable
fun TemperatureChart(temperatureData: List<Float>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(12.dp)),
        factory = { context ->
            LineChart(context).apply {
                description.text = "Temperature Trend"
                description.textSize = 12f
                description.isEnabled = true
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)

                val entries = temperatureData.mapIndexed { index, value ->
                    Entry(index.toFloat(), value)
                }

                val dataSet = LineDataSet(entries, "Temperature (°C)").apply {
                    color = Color(0xFF2E7D32).toArgb() // Green color
                    valueTextColor = Color.Black.toArgb()
                    lineWidth = 3f
                    circleRadius = 5f
                    setCircleColor(Color(0xFF2E7D32).toArgb()) // Green circles
                    valueTextSize = 10f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                }

                data = LineData(dataSet)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    valueFormatter = IndexAxisValueFormatter(
                        listOf("6AM", "9AM", "12PM", "3PM", "6PM", "9PM")
                    )
                    textColor = Color.Black.toArgb()
                }

                axisLeft.apply {
                    textColor = Color.Black.toArgb()
                    setDrawGridLines(false)
                }

                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val entries = temperatureData.mapIndexed { index, value ->
                Entry(index.toFloat(), value)
            }
            val dataSet = LineDataSet(entries, "Temperature (°C)").apply {
                color = Color(0xFF2E7D32).toArgb()
                valueTextColor = Color.Black.toArgb()
                lineWidth = 3f
                circleRadius = 5f
                setCircleColor(Color(0xFF2E7D32).toArgb())
                valueTextSize = 10f
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }
            chart.data = LineData(dataSet)
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    )
}
@Composable
fun SensorCard(label: String, value: String, textColor: Color) {
    Box(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 14.sp, color = Color.Gray)
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val iotviewmodel: iotViewModel = viewModel()
    val fakeNavController = rememberNavController() // Mocked NavController

    HomeScreen(iotviewmodel, navController = fakeNavController)
}

