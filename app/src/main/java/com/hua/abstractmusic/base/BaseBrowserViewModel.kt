package com.hua.abstractmusic.base

import android.app.Application
import android.content.ComponentName
import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media2.session.MediaBrowser
import androidx.media2.session.SessionToken
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.common.util.concurrent.MoreExecutors
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.LASTMEDIA
import com.hua.abstractmusic.other.Constant.LASTMEDIAINDEX
import com.hua.abstractmusic.services.PlayerService
import com.hua.abstractmusic.use_case.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author : huaweikai
 * @Date   : 2021/11/26
 * @Desc   : 支持browser的viewmodel基类
 */

abstract class BaseBrowserViewModel (
    application: Application,
    val useCase: UseCase
):AndroidViewModel(application){

    var browser:MediaBrowser?=null
     private set


    abstract fun initializeController()

    protected val _state = mutableStateOf(false)
    val state: State<Boolean> get() = _state

    fun connectBrowserService(browserCallback: MediaBrowser.BrowserCallback){
        val context = getApplication<Application>().applicationContext
        browser = MediaBrowser.Builder(context)
            .setSessionToken(SessionToken(context, ComponentName(context,PlayerService::class.java)))
            .setControllerCallback(Dispatchers.Default.asExecutor(),browserCallback)
            .build()
    }

    fun releaseBrowser(){
        browser?.close()
    }

    fun setPlaylist(startIndex:Int,items:List<MediaData>,autoPlay:Boolean = true){
        val browser = this.browser?:return
            val mediaIds = items.map {
                it.mediaItem.metadata?.mediaId ?: ""
            }
            browser.setPlaylist(mediaIds,null).addListener({
                browser.skipToPlaylistItem(startIndex)
                if(autoPlay){
                    browser.play()
                }else{
                    browser.prepare()
                }
            },MoreExecutors.directExecutor())
        viewModelScope.launch (Dispatchers.IO){
            useCase.clearCurrentListCase()
            useCase.insertMusicToCurrentItemCase(items)
        }
    }

    open fun detailInit(parentId: String){
        viewModelScope.launch {
            _state.value = false
            delay(500L)
            init(parentId)
        }
    }

    //加载音乐列表，根据父ID来进行加载
    open fun init(parentId: String) {
        val browser = browser ?: return
        //订阅
        browser.subscribe(parentId, null)
        //去获取数据
        browser.getChildren(parentId, 0, Int.MAX_VALUE, null)
    }

}