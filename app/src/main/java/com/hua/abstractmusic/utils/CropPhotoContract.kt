package com.hua.abstractmusic.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.core.content.contentValuesOf
import com.hua.abstractmusic.bean.CropParams
import java.io.File

/**
 * @author : huaweikai
 * @Date   : 2022/01/30
 * @Desc   :
 */
class CropPhotoContract : ActivityResultContract<CropParams, Uri?>() {

    private var outputUri: Uri? = null

    @CallSuper
    override fun createIntent(context: Context, input: CropParams): Intent {
//        // 获取输入图片uri的媒体类型
        val mimeType = context.contentResolver.getType(input.uri)
        // 创建新的图片名称
        val imageName = "${System.currentTimeMillis()}.${
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }"
        // 使用指定的uri地址
        outputUri = input.extraOutputUri ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 及以上获取图片uri
            val values = contentValuesOf(
                Pair(MediaStore.MediaColumns.DISPLAY_NAME, imageName),
                Pair(MediaStore.MediaColumns.MIME_TYPE, mimeType),
                Pair(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            )
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            Uri.fromFile(File(context.externalCacheDir!!.absolutePath, imageName))
        }

        return Intent("com.android.camera.action.CROP")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .setDataAndType(input.uri, mimeType)
            .putExtra("outputX", input.outputX)
            .putExtra("outputY", input.outputY)
            .putExtra("aspectX", input.aspectX)
            .putExtra("aspectY", input.aspectY)
            .putExtra("scale", input.scale)
            .putExtra("crop", input.crop)
            .putExtra("return-data", input.returnData)
            .putExtra("noFaceDetection", input.noFaceDetection)
            .putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
            .putExtra("outputFormat", input.outputFormat)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data
    }
}