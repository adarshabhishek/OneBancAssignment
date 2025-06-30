package com.example.onebancrestaurantapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onebancrestaurantapp.data.model.Cuisine
import com.example.onebancrestaurantapp.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val apiService=ApiService()

    private val _cuisines = MutableStateFlow<List<Cuisine>>(emptyList())
    val cuisines:StateFlow<List<Cuisine>> =_cuisines

    private val _isLoading = MutableStateFlow(false)
    val isLoading:StateFlow<Boolean> = _isLoading

    init{
        fetchCuisines()
    }

    private fun fetchCuisines() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value=true
            try {
                _cuisines.value=apiService.getItemList(page = 1, count = 10)
            } catch (e:Exception) {
                e.printStackTrace()
            } finally{
                _isLoading.value = false
            }
        }
    }
}
