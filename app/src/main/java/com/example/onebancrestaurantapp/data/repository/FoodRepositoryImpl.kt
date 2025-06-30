package com.example.onebancrestaurantapp.data.repository

import com.example.onebancrestaurantapp.data.model.Cuisine
import com.example.onebancrestaurantapp.data.remote.ApiService
import com.example.onebancrestaurantapp.domain.repository.FoodRepository

class FoodRepositoryImpl(private val apiService: ApiService) : FoodRepository {
    override fun getAllCuisines(): List<Cuisine> {
        return apiService.getItemList(page = 1, count = 10)
    }
}
