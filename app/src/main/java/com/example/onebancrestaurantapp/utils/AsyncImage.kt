package com.example.onebancrestaurantapp.utils

import android.graphics.Bitmap
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
fun rememberImagePainter(url: String):Painter? {
    var painter by remember{ mutableStateOf<Painter?>(null) }

    LaunchedEffect(url) {
        painter=withContext(Dispatchers.IO) {
            try {
                val connection=URL(url).openConnection()
                connection.connect()
                val inputStream=connection.getInputStream()

                val boundsOptions = BitmapFactory.Options().apply {
                    inJustDecodeBounds=true
                }
                BitmapFactory.decodeStream(inputStream,null, boundsOptions)
                inputStream.close()

                val targetWidth=300
                val targetHeight=300

                val sampleSize =calculateInSampleSize(boundsOptions, targetWidth, targetHeight)

                val scaledOptions=BitmapFactory.Options().apply {
                    inSampleSize=sampleSize
                    inPreferredConfig=Bitmap.Config.RGB_565
                }

                val scaledInput=URL(url).openStream()
                val bitmap=BitmapFactory.decodeStream(scaledInput, null, scaledOptions)
                scaledInput.close()

                bitmap?.let {BitmapPainter(it.asImageBitmap()) }
            } catch(e:Exception) {
                Log.e("ImageLoader","Failed to load image: ${e.message}")
                null
            }
        }
    }

    return painter
}
fun calculateInSampleSize(
    options:BitmapFactory.Options,
    reqWidth:Int,
    reqHeight:Int
): Int {
    val (width, height) = options.outWidth to options.outHeight
    var inSampleSize =1

    if (height > reqHeight ||width>reqWidth) {
        var halfHeight=height/2
        var halfWidth=width/2

        while ((halfHeight/inSampleSize) >= reqHeight && (halfWidth/inSampleSize)>=reqWidth) {
            inSampleSize*=2
        }
    }
    return inSampleSize
}
