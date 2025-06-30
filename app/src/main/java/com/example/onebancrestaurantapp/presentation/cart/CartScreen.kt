package com.example.onebancrestaurantapp.presentation.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.onebancrestaurantapp.data.model.CartItem
import com.example.onebancrestaurantapp.data.remote.ApiService
import com.example.onebancrestaurantapp.utils.CartManager
import com.example.onebancrestaurantapp.utils.rememberImagePainter

@Composable
fun CartScreen() {
    val items = remember { mutableStateListOf<CartItem>() }

    LaunchedEffect(Unit) {
        items.clear()
        items.addAll(CartManager.getItems())
    }

    val netTotal = items.sumOf { it.price * it.quantity }
    val cgst = netTotal * 0.025
    val sgst = netTotal * 0.025
    val grandTotal = netTotal + cgst + sgst

    var showSuccess by remember { mutableStateOf(false) }
    var txnRef by remember { mutableStateOf("") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Your Cart", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items.size) { index ->
                CartItemRow(item = items[index])
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
                val api = ApiService()
                val ref = api.makePayment(
                    totalAmount = grandTotal.toInt(),
                    totalItems = items.sumOf { it.quantity },
                    cartItems = items
                )
                if (ref != null) {
                    txnRef = ref
                    showSuccess = true
                    CartManager.clear()
                    items.clear()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Place Order", color = Color.White)
        }

        if (showSuccess) {
            AlertDialog(
                onDismissRequest = { showSuccess = false },
                title = { Text("Order Placed!") },
                text = { Text("Transaction Reference: $txnRef") },
                confirmButton = {
                    TextButton(onClick = { showSuccess = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Composable
fun CartItemRow(item: CartItem) {
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
        }
    }
}
