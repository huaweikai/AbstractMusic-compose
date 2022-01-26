package com.hua.abstractmusic.utils

import android.content.Context

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

fun String.isEmail():Boolean{
    val regex = Regex(pattern = "^[A-Za-z\\d]+([-_.][A-Za-z\\d]+)*@([A-Za-z\\d]+[-.])+[A-Za-z\\d]{2,4}\$")
    return regex.containsMatchIn(this)
}

fun String.isCode():Boolean{
    val regex = Regex(pattern = "^\\d{6}\$")
    return regex.containsMatchIn(this)
}