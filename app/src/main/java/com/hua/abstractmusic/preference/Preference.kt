package com.hua.abstractmusic.preference

import androidx.media3.common.Player
import com.hua.abstractmusic.other.Constant.LAST_MEDIA_INDEX
import com.tencent.mmkv.MMKV
import dagger.Module
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KProperty

/**
 *@author: Chen
 *@createTime: 2021/8/13 17:54
 *@description:
 **/
class MediaIndexPreference(mmkv: MMKV):MMKVPreference<Int>(mmkv,LAST_MEDIA_INDEX,0)
class ThemeColorPreference(mmkv: MMKV) : MMKVPreference<Int>(mmkv, "theme_color", Int.MIN_VALUE)
class UserTokenPreference(mmkv: MMKV) : MMKVPreference<String>(mmkv, "user_token", "")
class RepeatModePreference(mmkv: MMKV):MMKVPreference<Int>(mmkv,"repeat", Player.REPEAT_MODE_OFF)
class ShuffleModePreference(mmkv: MMKV):MMKVPreference<Boolean>(mmkv,"shuffle", false)
class MediaPositionPreference(mmkv: MMKV):MMKVPreference<Long>(mmkv,"position",0)