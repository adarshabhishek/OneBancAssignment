package com.example.onebancrestaurantapp.domain.repository

import com.example.onebancrestaurantapp.data.model.Cuisine

interface FoodRepository{
    fun getAllCuisines(): List<Cuisine>
}
