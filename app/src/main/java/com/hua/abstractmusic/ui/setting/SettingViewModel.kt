package com.hua.abstractmusic.ui.setting

import androidx.lifecycle.viewModelScope
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.preference.SettingInfoData
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.repository.UserRepository
import com.hua.service.MediaConnect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/04/01
 * @Desc   :
 */
@HiltViewModel
class SettingViewModel @Inject constructor(
    mediaConnect: MediaConnect,
    private val settingInfoData: SettingInfoData,
    private val userRepository: UserRepository,
    private val userInfoData: UserInfoData
) :BaseViewModel(mediaConnect){
    val timeOpen get() = settingInfoData.timeOpen
    val userInfo get() = userInfoData.userInfo

    val timeSlider get() = settingInfoData.slideValue

    val mediaTime get() = settingInfoData.time

    fun startTimer(value:Float,isOpen:Boolean = true){
        if(isOpen){
            settingInfoData.setTime(value){
                mediaConnect.browser?.pause()
            }
        }else{
            settingInfoData.closeTime()
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            userRepository.logoutUser()
        }
    }

}