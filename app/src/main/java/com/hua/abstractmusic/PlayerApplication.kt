package com.hua.abstractmusic

import android.app.Application
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : Application
 */
@HiltAndroidApp
class PlayerApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}