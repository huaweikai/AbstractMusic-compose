package com.hua.service.usecase

import com.hua.service.usecase.currentlist.ClearCurrentListCase
import com.hua.service.usecase.currentlist.InsertMusicToCurrentItemCase
import com.hua.service.usecase.currentlist.SelectCurrentListCase
import com.hua.service.usecase.sheet.InsertSheetCase
import com.hua.service.usecase.sheet.SelectInfoBySheet

/**
 * @author : huaweikai
 * @Date   : 2022/04/10
 * @Desc   :
 */
data class UseCase(
    val insertSheetCase: InsertSheetCase,
    val selectInfoBySheet: SelectInfoBySheet,
    val clearCurrentListCase: ClearCurrentListCase,
    val selectCurrentListCase: SelectCurrentListCase,
    val insertMusicToCurrentItemCase: InsertMusicToCurrentItemCase
)