package com.example.onebancrestaurantapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.onebancrestaurantapp.presentation.cart.CartScreen
import com.example.onebancrestaurantapp.presentation.cuisine.CuisineScreen
import com.example.onebancrestaurantapp.presentation.home.HomeScreen
import com.example.onebancrestaurantapp.viewmodel.HomeViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Cuisine : Screen("cuisine/{cuisineId}") {
        fun createRoute(cuisineId: String) = "cuisine/$cuisineId"
    }
    object Cart : Screen("cart")
}

@Composable
fun AppNavigation(navController: NavHostController, homeViewModel: HomeViewModel) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, viewModel = homeViewModel)
        }

        composable(
            route = Screen.Cuisine.route,
            arguments = listOf(navArgument("cuisineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val cuisineId = backStackEntry.arguments?.getString("cuisineId") ?: ""
            CuisineScreen(cuisineId = cuisineId, navController = navController)

        }
        composable(Screen.Cart.route) {
            CartScreen()
        }

    }
}
