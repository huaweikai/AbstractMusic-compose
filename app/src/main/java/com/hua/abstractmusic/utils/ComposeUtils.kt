package com.hua.abstractmusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import coil.ImageLoader
import coil.request.ImageRequest
import com.hua.abstractmusic.R
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/02/17
 * @Desc   :
 */
class ComposeUtils(
    private val imageLoader:ImageLoader,
    private val context: Context
) {
    suspend fun coilToBitmap(
        uri: Uri?
    ): Bitmap {
        val request = ImageRequest.Builder(context)
            .data(uri)
            .error(R.drawable.ic_music_launcher)
            .allowHardware(false)
            .build()
        val result = imageLoader.execute(request)
        return try {
            (result.drawable as BitmapDrawable).bitmap
        } catch (e: Exception) {
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.music
            )
        }
    }
}