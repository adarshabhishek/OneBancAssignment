package com.example.onebancrestaurantapp.utils

import com.example.onebancrestaurantapp.data.model.CartItem

object CartManager {
    private val cartItems = mutableListOf<CartItem>()

    fun addItem(item:CartItem) {
        val existing=cartItems.find { it.itemId == item.itemId }
        if (existing!=null) {
            existing.quantity +=item.quantity
        } else {
            cartItems.add(item)
        }
    }
    fun updateItem(updatedItem: CartItem) {
        val existingIndex=cartItems.indexOfFirst { it.itemId == updatedItem.itemId }
        if (existingIndex!=-1) {
            cartItems[existingIndex]=updatedItem
        }
    }

    fun removeItem(itemId:String) {
        cartItems.removeAll{it.itemId==itemId }
    }

    fun updateQuantity(itemId:String,newQty:Int) {
        cartItems.find {it.itemId==itemId }?.quantity=newQty
    }

    fun getItems():List<CartItem> = cartItems

    fun getTotal(): Int = cartItems.sumOf { it.price * it.quantity }

    fun clear() {
        cartItems.clear()
    }
}
