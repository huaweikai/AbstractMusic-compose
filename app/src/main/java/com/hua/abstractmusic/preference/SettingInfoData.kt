package com.hua.abstractmusic.preference

import android.os.CountDownTimer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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
    val timeOpen: StateFlow<Boolean> get() = _timeOpen

    private val _time = MutableStateFlow<Long>(0L)
    val time: StateFlow<Long> get() = _time

    private val _sliderValue = mutableStateOf(0F)
    val slideValue: State<Float> get() = _sliderValue

    private var countDownTimer: CountDownTimer? = null

    fun setTime(value: Float,finished:()->Unit) {
        timerClose()
        _timeOpen.value = true
        _sliderValue.value = value
        val time = (value + 1) * 5 * 60 * 1000L
        countDownTimer = object : CountDownTimer(time.toLong(),1000L){
            override fun onTick(millisUntilFinished: Long) {
                _time.value = millisUntilFinished
            }

            override fun onFinish() {
                finished()
                closeTime()
            }
        }
        countDownTimer?.start()
    }

    fun closeTime() {
        _timeOpen.value = false
        timerClose()
    }

    fun timerClose() {
        countDownTimer?.cancel()
    }

}