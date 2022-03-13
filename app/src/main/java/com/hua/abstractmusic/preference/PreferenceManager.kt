package com.hua.abstractmusic.preference

/**
 * @author : huaweikai
 * @Date   : 2022/03/11
 * @Desc   :
 */
class PreferenceManager(
    lastMediaIndex: MediaIndexPreference,
    themeColorPreference: ThemeColorPreference
) {
    var lastMediaIndex by lastMediaIndex
    var themeColor by themeColorPreference
}