package com.hua.abstractmusic.use_case

import com.hua.abstractmusic.use_case.currentlist.ClearCurrentListCase
import com.hua.abstractmusic.use_case.currentlist.GetCurrentListCase
import com.hua.abstractmusic.use_case.currentlist.InsertMusicToCurrentItemCase
import com.hua.abstractmusic.use_case.net.SelectNetAlbumCase
import com.hua.abstractmusic.use_case.net.SelectNetArtistCase
import com.hua.abstractmusic.use_case.sheet.GetSheetMusicListCase
import com.hua.abstractmusic.use_case.sheet.GetSheetNameCase
import com.hua.abstractmusic.use_case.sheet.InsertSheetCase
import com.hua.abstractmusic.use_case.user.UserLoginCase
import com.hua.abstractmusic.use_case.user.UserRegisterCase
import com.hua.abstractmusic.use_case.user.UserTokenOut

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : case集合类
 */
data class UseCase(
    val insertMusicToCurrentItemCase: InsertMusicToCurrentItemCase,
    val clearCurrentListCase: ClearCurrentListCase,
    val getCurrentListCase: GetCurrentListCase,
    val getSheetNameCase: GetSheetNameCase,
    val getSheetMusicListCase: GetSheetMusicListCase,
    val insertSheetCase: InsertSheetCase,
    val selectNetAlbumCase: SelectNetAlbumCase,
    val selectNetArtistCase: SelectNetArtistCase,
    val userRegisterCase: UserRegisterCase,
    val userTokenOut: UserTokenOut,
    val userLoginCase: UserLoginCase
)
