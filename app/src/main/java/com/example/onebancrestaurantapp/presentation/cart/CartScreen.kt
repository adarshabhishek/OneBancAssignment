package com.example.onebancrestaurantapp.presentation.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.onebancrestaurantapp.data.model.CartItem
import com.example.onebancrestaurantapp.data.remote.ApiService
import com.example.onebancrestaurantapp.utils.CartManager
import com.example.onebancrestaurantapp.utils.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CartScreen() {
    val items = remember { mutableStateListOf<CartItem>() }

    LaunchedEffect(Unit) {
        items.clear()
        items.addAll(CartManager.getItems())
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val netTotal = items.sumOf { it.price * it.quantity }
    val cgst = netTotal * 0.025
    val sgst = netTotal * 0.025
    val grandTotal = netTotal + cgst + sgst

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                "Your Cart",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items.size) { index ->
                    CartItemRow(
                        item = items[index],
                        onQuantityChange = { newQty ->
                            val currentItem = items[index]

                            if (newQty > 0) {
                                val updatedItem = currentItem.copy(quantity = newQty)
                                items[index] = updatedItem
                                CartManager.updateItem(updatedItem)
                            } else {
                                items.removeAt(index)
                                CartManager.removeItem(currentItem.itemId)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text("Subtotal: ₹$netTotal")
            Text("CGST (2.5%): ₹${cgst.toInt()}")
            Text("SGST (2.5%): ₹${sgst.toInt()}")
            Text("Grand Total: ₹${grandTotal.toInt()}", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (items.isEmpty()) {
                            snackbarHostState.showSnackbar(
                                message = "🛒 Your cart is empty",
                                duration = SnackbarDuration.Short
                            )
                            return@launch
                        }

                        val api = ApiService()
                        val ref = withContext(Dispatchers.IO) {
                            try {
                                api.makePayment(
                                    totalAmount = grandTotal.toInt(),
                                    totalItems = items.sumOf { it.quantity },
                                    cartItems = items
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                null
                            }
                        }

                        if (ref != null) {
                            CartManager.clear()
                            items.clear()
                            snackbarHostState.showSnackbar(
                                message = "🎉 Order placed successfully",
                                duration = SnackbarDuration.Short
                            )
                        } else {
                            snackbarHostState.showSnackbar(
                                message = "❌ Failed to place order",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Place Order", color = Color.White)
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, onQuantityChange: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            val painter = rememberImagePainter(item.imageUrl)

            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold)
                Text("₹${item.price} × ${item.quantity}", color = Color.Gray)
                Text("Total: ₹${item.price * item.quantity}", fontWeight = FontWeight.Medium)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onQuantityChange(item.quantity - 1) },
                    enabled = item.quantity > 0,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("-")
                }

                Text(
                    text = item.quantity.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Button(
                    onClick = { onQuantityChange(item.quantity + 1) },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text("+")
                }
            }
        }
    }
}
