package com.hua.abstractmusic.ui.splash

import androidx.lifecycle.ViewModel
import com.hua.service.MediaConnect
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/03/25
 * @Desc   :
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val mediaConnect: MediaConnect
):ViewModel() {
    val isConnected get() =  mediaConnect.isConnected
}