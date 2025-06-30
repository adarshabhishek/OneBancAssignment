package com.example.onebancrestaurantapp.data.model

data class CartItem(
    val cuisineId: String,
    val itemId: String,
    val name: String,
    val price: Int,
    var quantity: Int,
    val imageUrl: String
)
