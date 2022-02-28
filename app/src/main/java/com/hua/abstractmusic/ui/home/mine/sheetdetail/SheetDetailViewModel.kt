package com.hua.abstractmusic.ui.home.mine.sheetdetail

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import com.hua.abstractmusic.base.viewmodel.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/02/28
 * @Desc   :
 */
@HiltViewModel
class SheetDetailViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    itemTree: MediaItemTree
) :BaseBrowserViewModel(application,useCase,itemTree){
    var sheetId:String? = null
    val sheetDetailList = mutableStateOf<List<MediaData>>(emptyList())

    override fun onMediaConnected() {
        sheetId?.let {
            localListMap[it] = sheetDetailList
            playListMap[it] = sheetDetailList
        }
        refresh()
    }

}