package com.example.onebancrestaurantapp.presentation.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.onebancrestaurantapp.data.model.CartItem
import com.example.onebancrestaurantapp.utils.CartManager
import com.example.onebancrestaurantapp.data.remote.ApiService


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

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Your Cart", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(items) { item ->
                CartItemRow(item)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Subtotal: ₹$netTotal")
        Text("CGST (2.5%): ₹${cgst.toInt()}")
        Text("SGST (2.5%): ₹${sgst.toInt()}")
        Text("Grand Total: ₹${grandTotal.toInt()}", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        var showSuccess by remember { mutableStateOf(false) }
        var txnRef by remember { mutableStateOf("") }

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
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text(text = "${item.name} x${item.quantity}")
        Text(text = "₹${item.price * item.quantity}", color = Color.Gray)
    }
}
