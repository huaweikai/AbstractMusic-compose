package com.hua.service.preference

/**
 * @author : huaweikai
 * @Date   : 2022/03/11
 * @Desc   :
 */
class PreferenceManager(
    lastMediaIndex: MediaIndexPreference,
    themeColorPreference: ThemeColorPreference,
    userTokenPreference: UserTokenPreference,
    repeatModePreference: RepeatModePreference,
    shuffleModePreference: ShuffleModePreference,
    mediaPositionPreference: MediaPositionPreference
) {
    var lastMediaIndex by lastMediaIndex
    var themeColor by themeColorPreference
    var userToken by userTokenPreference
    var repeatMode by repeatModePreference
    var shuffleMode by shuffleModePreference
    var mediaPosition by mediaPositionPreference
}