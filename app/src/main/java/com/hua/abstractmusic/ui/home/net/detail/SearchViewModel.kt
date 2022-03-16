package com.hua.abstractmusic.ui.home.net.detail

import android.app.Application
import com.hua.abstractmusic.base.viewmodel.BaseBrowserViewModel
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/03/16
 * @Desc   :
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    application: Application,
    useCase: UseCase,
    itemTree: MediaItemTree
) :BaseBrowserViewModel(application, useCase, itemTree){


}