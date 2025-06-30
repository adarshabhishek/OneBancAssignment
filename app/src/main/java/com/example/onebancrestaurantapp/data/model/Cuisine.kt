package com.example.onebancrestaurantapp.data.model

data class Cuisine(
    val cuisineId:String,
    val cuisineName:String,
    val cuisineImageUrl: String,
    val items: List<Dish>
)