package com.hua.abstractmusic.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes

@TargetApi(19)
fun transparentBar(activity: Activity) {
    val window = activity.window
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = Color.TRANSPARENT
}

fun setStatusBarColor(activity: Activity, @ColorRes colorId: Int) {
    val window = activity.window
    window.statusBarColor = activity.resources.getColor(colorId)
}

fun statusBarLightMode(activity: Activity) = when {
    muiSetStatusBarLightMode(activity, true) -> 1
    flmSetStatusBarLightMode(activity, true) -> 2
    else -> {
        activity.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        3
    }
}


@SuppressLint("PrivateApi")
private fun getStatusBarByReflex(context: Context): Int {
    var statusBarHeight = 0
    try {
        val clazz = Class.forName("com.android.internal.R\$dimen")
        val obj = clazz.newInstance()
        val height = clazz.getField("status_bar_height")[obj]?.toString()?.toInt()
        if (height != null) {
            statusBarHeight = context.resources.getDimensionPixelSize(height)
        } else {
            statusBarHeight = 40
        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
    return statusBarHeight
}

private fun getStatusBarByResId(context: Context): Int {
    var height = 0
    //获取状态栏资源id
    //获取状态栏资源id
    val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        try {
            height = context.resources.getDimensionPixelSize(resourceId)
        } catch (ignored: Exception) {
        }
    }
    return height
}

fun getStatusBarHeight(context: Context): Int {
    var statusBarHeight: Int = getStatusBarByResId(context)
    if (statusBarHeight <= 0) {
        statusBarHeight = getStatusBarByReflex(context)
    }
    return statusBarHeight
}

/**
 * 需要MIUIV6以上
 *
 * @param activity
 * @param dark     是否把状态栏文字及图标颜色设置为深色
 * @return boolean 成功执行返回true
 */
@SuppressLint("PrivateApi")
fun muiSetStatusBarLightMode(activity: Activity, dark: Boolean): Boolean {
    var result = false
    val window = activity.window
    if (window != null) {
        val clazz = window.javaClass
        try {
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            val darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.java, Int::class.java)
            if (dark) {
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag)
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag)
            }
            result = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                if (dark) {
                    activity.window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
        } catch (e: Exception) {

        }
    }
    return result
}

fun flmSetStatusBarLightMode(activity: Activity, dark: Boolean): Boolean {
    var result = false
    val window = activity.window
    if (window != null) {
        try {
            val lp: WindowManager.LayoutParams = window.getAttributes()
            val darkFlag = WindowManager.LayoutParams::class.java
                .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            val mzFlags = WindowManager.LayoutParams::class.java
                .getDeclaredField("meizuFlags")
            darkFlag.isAccessible = true
            mzFlags.isAccessible = true
            val bit = darkFlag.getInt(null)
            var value = mzFlags.getInt(lp)
            value = if (dark) {
                value or bit
            } else {
                value and bit.inv()
            }
            mzFlags.setInt(lp, value)
            window.attributes = lp
            result = true
        } catch (ignored: Exception) {
        }
    }
    return result
}