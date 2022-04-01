package com.hua.abstractmusic.ui.setting

import androidx.lifecycle.viewModelScope
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.other.NetWork
import com.hua.abstractmusic.preference.SettingInfoData
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.repository.UserRepository
import com.hua.abstractmusic.services.MediaConnect
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

    fun startTimer(isOpen:Boolean){
        if(isOpen){
            settingInfoData.setTime(0)
        }else{
            settingInfoData.closeTime()
        }

    }

    fun logoutUser() {
        viewModelScope.launch {
            val result = userRepository.logoutUser().code
            val isSuccess = result == NetWork.SUCCESS
        }
    }

}