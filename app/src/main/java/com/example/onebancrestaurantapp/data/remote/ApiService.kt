package com.example.onebancrestaurantapp.data.remote

import android.util.Log
import com.example.onebancrestaurantapp.data.model.Cuisine
import com.example.onebancrestaurantapp.data.model.Dish
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import com.example.onebancrestaurantapp.data.model.CartItem

class ApiService {

    private val baseUrl = "https://uat.onebanc.ai"
    private val headers = mapOf(
        "X-Partner-API-Key" to "uonebancservceemultrS3cg8RaL30",
        "Content-Type" to "application/json"
    )

    fun getItemList(page: Int, count: Int): List<Cuisine> {
        val endpoint = "/emulator/interview/get_item_list"
        val body = JSONObject().apply {
            put("page", page)
            put("count", count)
        }
        val customHeader = "get_item_list"
        val response = makePostRequest(endpoint, body, customHeader)

        return parseCuisineResponse(response)
    }

    private fun makePostRequest(
        endpoint: String,
        jsonBody: JSONObject,
        proxyAction: String
    ): String {
        val url = URL(baseUrl + endpoint)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("X-Forward-Proxy-Action", proxyAction)
            headers.forEach { connection.setRequestProperty(it.key, it.value) }

            connection.doOutput = true
            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.writeBytes(jsonBody.toString())
            outputStream.flush()
            outputStream.close()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                throw IOException("Error response: $responseCode")
            }
        } catch (e: Exception) {
            Log.e("ApiService", "Network Error: ${e.message}")
            return ""
        } finally {
            connection.disconnect()
        }
    }

    private fun parseCuisineResponse(response: String): List<Cuisine> {
        val cuisines = mutableListOf<Cuisine>()
        if (response.isEmpty()) return cuisines

        val root = JSONObject(response)
        val cuisineArray = root.getJSONArray("cuisines")

        for (i in 0 until cuisineArray.length()) {
            val cuisineObj = cuisineArray.getJSONObject(i)
            val items = mutableListOf<Dish>()

            val itemsArray = cuisineObj.getJSONArray("items")
            for (j in 0 until itemsArray.length()) {
                val item = itemsArray.getJSONObject(j)
                items.add(
                    Dish(
                        id = item.getString("id"),
                        name = item.getString("name"),
                        imageUrl = item.getString("image_url"),
                        price = item.getString("price").toInt(),
                        rating = item.getString("rating").toFloat()
                    )
                )
            }

            cuisines.add(
                Cuisine(
                    cuisineId = cuisineObj.getString("cuisine_id"),
                    cuisineName = cuisineObj.getString("cuisine_name"),
                    cuisineImageUrl = cuisineObj.getString("cuisine_image_url"),
                    items = items
                )
            )
        }

        return cuisines
    }
    fun makePayment(
        totalAmount: Int,
        totalItems: Int,
        cartItems: List<CartItem>
    ): String? {
        val endpoint = "/emulator/interview/make_payment"
        val body = JSONObject().apply {
            put("total_amount", totalAmount.toString())
            put("total_items", totalItems)
            put("data", JSONArray().apply {
                cartItems.forEach {
                    put(JSONObject().apply {
                        put("cuisine_id", it.cuisineId)
                        put("item_id", it.itemId)
                        put("item_price", it.price)
                        put("item_quantity", it.quantity)
                    })
                }
            })
        }

        val response = makePostRequest(endpoint, body, "make_payment")

        return if (response.isNotEmpty()) {
            val root = JSONObject(response)
            if (root.optInt("response_code") == 200) {
                root.optString("txn_ref_no")
            } else null
        } else null
    }

}
