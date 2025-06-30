package com.example.onebancrestaurantapp.utils

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun rememberImagePainter(url: String): Painter? {
    var painter by remember { mutableStateOf<Painter?>(null) }

    LaunchedEffect(url) {
        painter = withContext(Dispatchers.IO) {
            try {
                val input = URL(url).openStream()
                val bitmap = BitmapFactory.decodeStream(input)
                BitmapPainter(bitmap.asImageBitmap())
            } catch (e: Exception) {
                Log.e("ImageLoader", "Failed to load image: ${e.message}")
                null
            }
        }
    }

    return painter
}
