package com.example.onebancrestaurantapp.presentation.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.onebancrestaurantapp.R
import com.example.onebancrestaurantapp.data.model.Cuisine
import com.example.onebancrestaurantapp.data.model.Dish
import com.example.onebancrestaurantapp.presentation.navigation.Screen
import com.example.onebancrestaurantapp.utils.rememberImagePainter
import com.example.onebancrestaurantapp.viewmodel.HomeViewModel
import androidx.navigation.NavController
import java.util.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    var isEnglish by remember { mutableStateOf(true) }
    val cuisines by viewModel.cuisines.collectAsState()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Button(
                onClick={
                    val locale =if(isEnglish) Locale("hi") else Locale("en")
                    Locale.setDefault(locale)
                    configuration.setLocale(locale)
                    context.createConfigurationContext(configuration)
                    isEnglish=!isEnglish
                },
                colors=ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7)),
                modifier=Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if(isEnglish) "Switch to Hindi" else "अंग्रेज़ी में बदलें",
                    color=Color.White
                )
            }

            Spacer(modifier= Modifier.height(12.dp))

            Text(
                text=stringResource(R.string.cuisine_categories),
                style=MaterialTheme.typography.titleLarge,
                fontWeight=FontWeight.Bold
            )

            Spacer(modifier =Modifier.height(8.dp))
            LazyRow {
                items(cuisines) { cuisine ->
                    CuisineCard(cuisine=cuisine) {
                        navController.navigate(Screen.Cuisine.createRoute(cuisine.cuisineId))
                    }
                }
            }
            Spacer(modifier=Modifier.height(16.dp))
            Text(
                text=stringResource(R.string.top_dishes),
                style=MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier=Modifier.height(8.dp))
            LazyColumn {
                items(cuisines.take(1).flatMap {it.items.take(3) }) { dish ->
                    DishCard(dish)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick= { navController.navigate(Screen.Cart.route) },
                modifier= Modifier.fillMaxWidth(),
                colors=ButtonDefaults.buttonColors(containerColor = Color(0xFF009688))
            ) {
                Text("Go to Cart",color =Color.White)
            }

        }
    }
}
@Composable
fun CuisineCard(cuisine: Cuisine, onClick: () -> Unit) {
    Card(
        modifier =Modifier
            .padding(end = 12.dp)
            .width(180.dp)
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        val painter = rememberImagePainter(cuisine.cuisineImageUrl)
        Box {
            if (painter!=null) {
                Image(
                    painter=painter,
                    contentDescription=cuisine.cuisineName,
                    modifier=Modifier.fillMaxSize(),
                    contentScale =ContentScale.Crop
                )
            }
            Box(
                modifier=Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Text(
                text=cuisine.cuisineName,
                color=Color.White,
                modifier=Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                fontWeight=FontWeight.Bold
            )
        }
    }
}

@Composable
fun DishCard(dish:Dish) {
    Card(
        modifier=Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape=RoundedCornerShape(12.dp),
        elevation=CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier=Modifier.padding(8.dp)) {
            val painter=rememberImagePainter(dish.imageUrl)

            if (painter!=null) {
                Image(
                    painter=painter,
                    contentDescription = dish.name,
                    modifier=Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale=ContentScale.Crop
                )
            }

            Spacer(modifier=Modifier.width(12.dp))
            Column {
                Text(text=dish.name, fontWeight = FontWeight.Bold)
                Text(text="₹${dish.price}", color = Color.Gray)
                Text(text= "⭐ ${dish.rating}")
            }
        }
    }
}
