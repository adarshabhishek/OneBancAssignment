package com.example.onebancrestaurantapp.presentation.cuisine

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.onebancrestaurantapp.data.model.CartItem
import com.example.onebancrestaurantapp.data.model.Cuisine
import com.example.onebancrestaurantapp.data.model.Dish
import com.example.onebancrestaurantapp.data.remote.ApiService
import com.example.onebancrestaurantapp.presentation.navigation.Screen
import com.example.onebancrestaurantapp.utils.CartManager
import com.example.onebancrestaurantapp.utils.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CuisineScreen(cuisineId: String, navController: NavController) {
    var cuisine by remember { mutableStateOf<Cuisine?>(null) }
    val quantities = remember { mutableStateMapOf<String, Int>() }
    LaunchedEffect(cuisineId) {
        cuisine=withContext(Dispatchers.IO) {
            val api=ApiService()
            val cuisines =api.getItemList(1, 10)
            cuisines.find{ it.cuisineId == cuisineId }
        }
    }

    cuisine?.let { selectedCuisine ->
        val totalQuantity = quantities.values.sum()

        Box(modifier=Modifier.fillMaxSize()) {
            Column(
                modifier=Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(bottom =if (totalQuantity > 0) 64.dp else 0.dp)
            ) {
                Text(
                    text=selectedCuisine.cuisineName,
                    style=MaterialTheme.typography.headlineSmall,
                    fontWeight=FontWeight.Bold
                )

                Spacer(modifier=Modifier.height(12.dp))

                LazyColumn{
                    items(selectedCuisine.items) { dish ->
                        DishQuantityCard(dish,quantities[dish.id] ?: 0) { change ->
                            val current=quantities[dish.id] ?: 0
                            val updated=(current + change).coerceAtLeast(0)
                            quantities[dish.id]=updated

                            if (updated>0) {
                                CartManager.addItem(
                                    CartItem(
                                        cuisineId = selectedCuisine.cuisineId,
                                        itemId = dish.id,
                                        name = dish.name,
                                        price = dish.price,
                                        quantity = 1,
                                        imageUrl = dish.imageUrl
                                    )
                                )
                            }else{
                                CartManager.removeItem(dish.id)
                            }
                        }
                    }
                }
            }
            if (totalQuantity>0) {
                Button(
                    onClick={ navController.navigate(Screen.Cart.route) },
                    modifier=Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688))
                ) {
                    Text("Go to Cart",color=Color.White)
                }
            }
        }
    }?:Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
@Composable
fun DishQuantityCard(dish: Dish,quantity:Int,onQuantityChange:(Int) -> Unit) {
    Card(
        modifier=Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape=RoundedCornerShape(12.dp),
        elevation=CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier=Modifier.padding(12.dp)) {
            val painter=rememberImagePainter(dish.imageUrl)

            if (painter!=null) {
                Image(
                    painter=painter,
                    contentDescription=dish.name,
                    modifier=Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale=ContentScale.Crop
                )
            }
            Spacer(modifier=Modifier.width(12.dp))

            Column(modifier=Modifier.weight(1f)) {
                Text(text=dish.name,fontWeight=FontWeight.SemiBold)
                Text(text="₹${dish.price}",color=Color.Gray)
                Text(text="⭐ ${dish.rating}")
            }
            Row(
                verticalAlignment=Alignment.CenterVertically,
                horizontalArrangement=Arrangement.End
            ) {
                Button(onClick={onQuantityChange(-1)},enabled=quantity>0) {
                    Text("-")
                }
                Text(
                    text=quantity.toString(),
                    modifier=Modifier.padding(horizontal=8.dp)
                )
                Button(onClick={onQuantityChange(1) }) {
                    Text("+")
                }
            }
        }
    }
}
