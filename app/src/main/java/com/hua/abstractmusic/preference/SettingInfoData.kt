package com.hua.abstractmusic.preference

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : huaweikai
 * @Date   : 2022/04/01
 * @Desc   :
 */
@Singleton
class SettingInfoData @Inject constructor(

) {
    private val _timeOpen = MutableStateFlow(false)
    val timeOpen :StateFlow<Boolean> get() = _timeOpen

    private val _time = MutableStateFlow<Long>(0L)
    val time :StateFlow<Long> get() = _time


    fun setTime(value:Int){
        _timeOpen.value = true
    }

    fun closeTime(){
        _timeOpen.value = false
    }

}