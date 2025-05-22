package com.example.iot

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.iot.Screens.HomeScreen
import com.example.iot.Screens.OtherScreen
import com.example.iot.ui.theme.IOTTheme

sealed class DesitnationScreen(val route: String){
    object Home : DesitnationScreen("Home")
    object Other : DesitnationScreen("Other")

}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IOTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   chatapppnavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}



@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun chatapppnavigation(modifier: Modifier) {
    val iotviewmodel: iotViewModel=viewModel()
    val navcontroller = rememberNavController()

    NavHost(navController = navcontroller, startDestination = DesitnationScreen.Other.route) {
        composable(DesitnationScreen.Home.route) {
            HomeScreen(iotviewmodel,navController = navcontroller)
        }
        composable(DesitnationScreen.Other.route) {
            OtherScreen(iotviewmodel,navController = navcontroller)

        }

    }
}

