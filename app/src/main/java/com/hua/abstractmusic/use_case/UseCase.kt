package com.hua.abstractmusic.use_case

import com.hua.abstractmusic.use_case.currentlist.ClearCurrentListCase
import com.hua.abstractmusic.use_case.currentlist.GetCurrentListCase
import com.hua.abstractmusic.use_case.currentlist.InsertMusicToCurrentItemCase
import com.hua.abstractmusic.use_case.sheet.GetSheetList
import com.hua.abstractmusic.use_case.sheet.GetSheetMusicBySheetId
import com.hua.abstractmusic.use_case.sheet.InsertSheetCase

/**
 * @author : huaweikai
 * @Date   : 2021/11/27
 * @Desc   : case集合类
 */
data class UseCase(
    val insertMusicToCurrentItemCase: InsertMusicToCurrentItemCase,
    val clearCurrentListCase: ClearCurrentListCase,
    val getCurrentListCase: GetCurrentListCase,
    val getSheetList: GetSheetList,
    val getSheetMusicBySheetId: GetSheetMusicBySheetId,
    val insertSheetCase: InsertSheetCase
)
