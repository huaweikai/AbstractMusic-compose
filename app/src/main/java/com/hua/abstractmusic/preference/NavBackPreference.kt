package com.hua.abstractmusic.preference

import androidx.navigation.NavBackStackEntry
import com.hua.abstractmusic.bean.ParcelizeMediaItem


/**
 * @author : huaweikai
 * @Date   : 2022/03/16
 * @Desc   :
 */
@Suppress("UNCHECKED_CAST")
fun <T> NavBackStackEntry.getValue(
    key:String,
    defaultValue:T
):T{
    val arguments = this.arguments ?: return defaultValue
    val value =  when(defaultValue){
        is String -> arguments.getString(key, defaultValue) as T
        is Long -> arguments.getLong(key, defaultValue) as T
        is Boolean -> arguments.getBoolean(key, defaultValue) as T
        is Float -> arguments.getFloat(key, defaultValue) as T
        is Byte -> arguments.getByte(key, defaultValue) as T
        is Int -> arguments.getInt(key, defaultValue) as T
        is ParcelizeMediaItem -> arguments.getParcelable<ParcelizeMediaItem>(key) as T
        else -> throw IllegalArgumentException("Type Error, cannot get value!")
    }
    return value
}