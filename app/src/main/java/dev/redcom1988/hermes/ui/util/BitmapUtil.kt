package dev.redcom1988.hermes.ui.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dev.redcom1988.hermes.core.network.GET
import dev.redcom1988.hermes.core.network.NetworkHelper
import dev.redcom1988.hermes.core.util.extension.await
import dev.redcom1988.hermes.core.util.extension.inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

suspend fun fetchImageAsBitmap(url: String): Bitmap? = withContext(Dispatchers.IO) {
    try {
        val networkHelper = inject<NetworkHelper>()
        val request = GET(url)
        val response = networkHelper.client.newCall(request).await()
        if (!response.isSuccessful) return@withContext null

        val inputStream = response.body.byteStream()
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}