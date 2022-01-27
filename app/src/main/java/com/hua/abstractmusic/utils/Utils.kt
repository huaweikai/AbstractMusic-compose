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
