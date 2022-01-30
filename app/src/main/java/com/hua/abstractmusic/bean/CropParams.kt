package com.hua.abstractmusic.bean

import android.graphics.Bitmap
import android.net.Uri

/**
 * @author : huaweikai
 * @Date   : 2022/01/30
 * @Desc   :
 */
class CropParams(
    val uri: Uri,
    val aspectX: Int = 1,
    val aspectY: Int = 1,
    @androidx.annotation.IntRange(from = 0, to = 1080)
    val outputX: Int = 250,
    @androidx.annotation.IntRange(from = 0, to = 1080)
    val outputY: Int = 250,
    val scale: Boolean = true,
    val crop: Boolean = true,
    val noFaceDetection: Boolean = true,
    val returnData: Boolean = false,
    val outputFormat: String = Bitmap.CompressFormat.JPEG.toString(),
    val extraOutputUri: Uri? = null
)