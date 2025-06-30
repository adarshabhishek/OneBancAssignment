package com.example.onebancrestaurantapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.onebancrestaurantapp.presentation.navigation.AppNavigation
import com.example.onebancrestaurantapp.presentation.ui.theme.OneBancRestaurantAppTheme
import com.example.onebancrestaurantapp.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OneBancRestaurantAppTheme {
                val navController=rememberNavController()
                val homeViewModel:HomeViewModel=viewModel()
                AppNavigation(navController = navController,homeViewModel=homeViewModel)
            }
        }
    }
}
