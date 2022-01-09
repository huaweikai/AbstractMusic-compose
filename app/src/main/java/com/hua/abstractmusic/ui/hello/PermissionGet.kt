package com.hua.abstractmusic.ui.hello

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : 判断是否获取到读取权限
 */
object PermissionGet {
    fun checkReadPermission(context: Context):Boolean{
        val read = ContextCompat.checkSelfPermission(
            context,Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val write = ContextCompat.checkSelfPermission(
            context,Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return read&&write
    }
}