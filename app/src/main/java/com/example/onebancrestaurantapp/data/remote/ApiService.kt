package com.example.onebancrestaurantapp.data.remote

import android.util.Log
import com.example.onebancrestaurantapp.data.model.CartItem
import com.example.onebancrestaurantapp.data.model.Cuisine
import com.example.onebancrestaurantapp.data.model.Dish
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class ApiService {
    private val baseUrl = "https://uat.onebanc.ai"
    private fun makePostRequest(endpoint: String, jsonBody: JSONObject, proxyAction: String): String {
        val url = URL(baseUrl+endpoint)
        val conn = url.openConnection() as HttpURLConnection

        return try {
            conn.requestMethod = "POST"
            conn.setRequestProperty("X-Partner-API-Key","uonebancservceemultrS3cg8RaL30")
            conn.setRequestProperty("X-Forward-Proxy-Action",proxyAction)
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            BufferedWriter(OutputStreamWriter(conn.outputStream,"UTF-8")).use {
                it.write(jsonBody.toString())
            }

            val inputStream=if(conn.responseCode in 200..299) conn.inputStream else conn.errorStream
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            Log.e("ApiService", "makePostRequest error: ${e.message}")
            ""
        } finally {
            conn.disconnect()
        }
    }

    fun getItemList(page:Int,count:Int): List<Cuisine> {
        val endpoint="/emulator/interview/get_item_list"
        val body=JSONObject().apply{
            put("page",page)
            put("count",count)
        }

        val response=makePostRequest(endpoint,body,"get_item_list")
        return parseCuisineResponse(response)
    }

    private fun parseCuisineResponse(response: String): List<Cuisine> {
        val cuisines=mutableListOf<Cuisine>()
        if (response.isEmpty())return cuisines
        val root=JSONObject(response)
        val cuisineArray=root.getJSONArray("cuisines")

        for (i in 0 until cuisineArray.length()) {
            val cuisineObj=cuisineArray.getJSONObject(i)
            val items=mutableListOf<Dish>()
            val itemsArray = cuisineObj.getJSONArray("items")
            for (j in 0 until itemsArray.length()) {
                val item=itemsArray.getJSONObject(j)
                items.add(
                    Dish(
                        id=item.getString("id"),
                        name=item.getString("name"),
                        imageUrl=item.getString("image_url"),
                        price=item.getString("price").toInt(),
                        rating=item.getString("rating").toFloat()
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
        totalItems: Int,
        cartItems: List<CartItem>
    ): String?{
        return "SIMULATED_TXN_REF"
    }
}