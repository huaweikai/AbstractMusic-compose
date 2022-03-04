package com.hua.abstractmusic.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.content.contentValuesOf
import com.hua.abstractmusic.other.Constant.LOCAL
import java.io.File

/**
 * @author : huaweikai
 * @Date   : 2022/01/15
 * @Desc   : utils
 */
fun getVersion(
    context: Context
):String{
    runCatching {
        val info = context.packageManager.getPackageInfo(context.packageName,0)
        return info.versionName
    }
    return ""
}

fun String.isEmail()=
    Regex(pattern = "^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}\$")
        .containsMatchIn(this)


fun String.isCode()=
    Regex(pattern = "^\\d{6}\$")
        .containsMatchIn(this)

fun String.isPassWord() =
    Regex(pattern = "^(?!^(\\d+|[a-zA-Z]+|[~!@#\$%^&*?/]+)\$)^[\\w~!@#\$%^&*?/]{8,16}\$")
        .containsMatchIn(this)

fun String.isUser() =
    Regex(pattern = "^[\\u4e00-\\u9fa5_a-zA-Z0-9-]{2,16}\$")
        .containsMatchIn(this)

fun String.isLocal():Boolean {
    val type = Uri.parse(this).authority
    return type?.startsWith(LOCAL) ?: true
}
val Int.textDp: TextUnit
    @Composable get() =  this.textDp(density = LocalDensity.current)

private fun Int.textDp(density: Density): TextUnit = with(density) {
    this@textDp.dp.toSp()
}

fun getCacheDir(context: Context,uri: Uri):Uri?{
    val mimeType = context.contentResolver.getType(uri)
    // 创建新的图片名称
    val imageName = "${System.currentTimeMillis()}.${
        MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }"
    // 使用指定的uri地址
    val outputUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
    return outputUri
}
