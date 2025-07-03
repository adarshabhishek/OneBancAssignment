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
                val connection = URL(url).openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()

                // Step 1: Decode bounds only (no bitmap loaded in memory yet)
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream.close()

                val targetWidth = 500
                val targetHeight = 500

                // Step 2: Calculate sample size
                val sampleSize = calculateInSampleSize(options, targetWidth, targetHeight)

                // Step 3: Decode with downscaling
                val scaledOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                }
                val scaledInput = URL(url).openStream()
                val scaledBitmap = BitmapFactory.decodeStream(scaledInput, null, scaledOptions)
                scaledInput.close()

                scaledBitmap?.let { BitmapPainter(it.asImageBitmap()) }
            } catch (e: Exception) {
                Log.e("ImageLoader", "❌ Failed to load image: ${e.message}")
                null
            }
        }
    }

    return painter
}

fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val (width: Int, height: Int) = options.outWidth to options.outHeight
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}
