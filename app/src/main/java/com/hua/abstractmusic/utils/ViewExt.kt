package com.hua.abstractmusic.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.color.MaterialColors

/**
 * @author Xiaoc
 * @since 2021/5/13
 *
 * 简洁获取颜色值的操作
 */
@RequiresApi(Build.VERSION_CODES.M)
fun View.getColor(@ColorRes id: Int): Int = resources.getColor(id, context.theme)

/**
 * 获取Attr属性的颜色值
 */
@ColorInt
fun View.getAttrColor(@AttrRes id: Int): Int = MaterialColors.getColor(this, id)


/**
 * 获取Attr属性的颜色值
 */
@ColorInt
fun Context.getAttrColor(@AttrRes id: Int): Int = MaterialColors.getColor(this, id, "")

/**
 * 获取尺寸值（根据屏幕适配）
 */
fun Context.getDimension(@DimenRes id: Int): Float = resources.getDimension(id)

/**
 * 获取尺寸值（根据屏幕适配）
 */
fun View.getDimension(@DimenRes id: Int): Float = resources.getDimension(id)

/**
 * 得到Drawable资源
 */
fun View.getDrawable(@DrawableRes id: Int): Drawable? = ResourcesCompat.getDrawable(resources,id,context.theme)

/**
 * 将dp转为对应px的像素
 */
//val Int.dp: Int
//    get() {
//        return TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            this.toFloat(),
//            Resources.getSystem().displayMetrics
//        ).toInt()
//    }

/**
 * 设置点击事件防抖
 * 在600ms以内连续点击的事件只会执行一次
 * @param action 点击事件操作
 */
fun View.setOnDebounceClickListener(action: (View) -> Unit){
    val actionDebouncer = ActionDebouncer(action)

    setOnClickListener {
        actionDebouncer.notifyAction(it)
    }
}

private class ActionDebouncer(private val action: (View) -> Unit){

    companion object{
        const val DEBOUNCE_INTERVAL_MILLISECONDS = 600L
    }

    private var lastActionTime = 0L

    fun notifyAction(view: View){
        val now = SystemClock.elapsedRealtime()
        val millisecondsPassed = now - lastActionTime
        val actionAllowed = millisecondsPassed > DEBOUNCE_INTERVAL_MILLISECONDS
        lastActionTime = now

        if(actionAllowed){
            action(view)
        }
    }
}