package com.hua.abstractmusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.hua.abstractmusic.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author : huaweikai
 * @Date   : 2022/02/17
 * @Desc   :
 */
@Singleton
class ComposeUtils @Inject constructor(
    private val imageLoader: ImageLoader,
    @ApplicationContext private val context: Context
) {
    @Inject
    @Named("ErrorBitmap")
    lateinit var errorBitmap: Bitmap

    suspend fun coilToBitmap(
        uri: Any?
    ): Bitmap {
        val request = ImageRequest.Builder(context)
            .data(uri)
            .error(R.drawable.music)
            .allowHardware(false)
            .build()
        val result = imageLoader.execute(request)
        return try {
            (result.drawable as BitmapDrawable).bitmap
        } catch (e: Exception) {
            errorBitmap
        }
    }

    fun coilToBitmap(
        uri: Any?,
        onSuccess: (Bitmap) -> Unit,
        onError: (Bitmap?) -> Unit
    ) {
        val request = ImageRequest.Builder(context)
            .data(uri)
            .error(R.drawable.music)
            .target(
                onSuccess = {
                    onSuccess(it.toBitmap())
                },
                onError = {
                    onError(it?.toBitmap())
                }
            )
            .build()
        imageLoader.enqueue(request)
    }
}