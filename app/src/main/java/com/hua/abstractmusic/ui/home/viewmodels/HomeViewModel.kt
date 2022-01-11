package com.hua.abstractmusic.ui.home.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewModelScope
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import androidx.media2.session.MediaBrowser
import androidx.media2.session.MediaController
import androidx.media2.session.SessionCommandGroup
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.android.exoplayer2.Player
import com.google.common.util.concurrent.MoreExecutors
import com.hua.abstractmusic.base.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.NETWORK_ALBUM_ID
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.utils.artist
import com.hua.abstractmusic.utils.title
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : viewmodel
 */
@HiltViewModel
@OptIn(ExperimentalPagerApi::class,ExperimentalMaterialApi::class)
class HomeViewModel @Inject constructor(
    application: Application,
    useCase: UseCase
):BaseBrowserViewModel(application, useCase) {

    private val browserCallback = object :MediaBrowser.BrowserCallback(){
        override fun onConnected(
            controller: MediaController,
            allowedCommands: SessionCommandGroup
        ) {
            init(NETWORK_ALBUM_ID)
            updateItem(browser?.currentMediaItem)
        }

        override fun onPlaylistChanged(
            controller: MediaController,
            list: MutableList<MediaItem>?,
            metadata: MediaMetadata?
        ) {
//            updatePlayList(list)
        }

        override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
            updateItem(item)
        }

        override fun onPlayerStateChanged(controller: MediaController, state: Int) {
            updatePlayState(state)
        }
    }

    //初始化，连接service
    override fun initializeController() {
        connectBrowserService(browserCallback)
    }

    //当前播放列表
    private val _currentPlayList = mutableStateOf<List<MediaData>>(emptyList())
    val currentPlayList :State<List<MediaData>> get() = _currentPlayList

    //用于在启动时，state需要一个起始的value
    private val nullMediaData = MediaItem.Builder()
        .setMetadata(
            MediaMetadata.Builder()
                .apply {
                    title = "欢迎来到音乐的世界"
                    artist = ""
                }
                .build()
        )
        .build()

    //网络请求的数据，到时候会清理掉，仅仅只是测试使用
    private val _netAlbum =  mutableStateOf<List<MediaData>>(emptyList())
    val netAlbum : State<List<MediaData>> get() = _netAlbum

    //当前播放的item，用户更新控制栏
    private val _currentItem = mutableStateOf(nullMediaData)
    val currentItem :State<MediaItem> get() = _currentItem



    //播放状态
    private val _playerState = mutableStateOf(SessionPlayer.PLAYER_STATE_IDLE)
    val playerState get() = _playerState

    private fun updatePlayState(@SessionPlayer.PlayerState playState: Int) {
        _playerState.value = playState
    }

    //加载音乐列表，根据父ID来进行加载
    fun init(parentId:String){
        val browser = browser?:return
        val childrenFeature = browser.getChildren(
            parentId,0,Int.MAX_VALUE,null
        )
        childrenFeature.addListener({
            childrenFeature.get().mediaItems?.map {
                MediaData(
                    it,
                    it.metadata?.mediaId == browser.currentMediaItem?.metadata?.mediaId
                )
            }.apply {
                _netAlbum.value = this?: emptyList()
            }
        },MoreExecutors.directExecutor())
    }

    //更新item的方法，当回调到item改变就调用这个方法
    private fun updateItem(item:MediaItem?){
        item?:return
        browser?:return
        _netAlbum.value = _netAlbum.value.toMutableList().map {
            val isPlaying =it.mediaId == browser!!.currentMediaItem?.metadata?.mediaId
            it.copy(isPlaying = isPlaying)
        }
        _currentItem.value = item

        _currentPlayList.value = browser!!.playlist?.map {
            val isPlaying =it.metadata?.mediaId == browser!!.currentMediaItem?.metadata?.mediaId
            MediaData(it,isPlaying)
        }?: emptyList()
    }

    //下一首
    fun skipIem(){
        val browser = browser?:return
        browser.skipToNextPlaylistItem()
    }

    //播放还是暂停？
    fun playOrPause(){
        val browser = browser?:return
        if(browser.playerState == SessionPlayer.PLAYER_STATE_PLAYING){
            browser.pause()
        }else{
            browser.play()
        }
    }

/*    //更新播放列表的方法
    fun updatePlayList(mediaItems:List<MediaItem>?){
        mediaItems?:return
        val browser = browser?:return
        _currentPlayList.value = mediaItems.map {
            val isPlaying = it.metadata?.mediaId == browser.currentMediaItem?.metadata?.mediaId
            MediaData(it,isPlaying)
        }
    }*/
    fun removePlayItem(position:Int){
        val browser = browser?:return
        browser.removePlaylistItem(position)
    }

    fun skipTo(position: Int){
        val browser = browser?:return
        browser.skipToPlaylistItem(position)
    }

    fun clearPlayList(){
        browser?.removePlaylistItem(0)
    }


    //记录本地音乐的viewpager停在哪
    var horViewPagerState = mutableStateOf(PagerState(0))

    //记录主页播放列表的打开和关闭
    val playListState = mutableStateOf(ModalBottomSheetState(ModalBottomSheetValue.Hidden))

    //记录主页控制中心，的navigationview是否要隐藏
    val navigationState = mutableStateOf(
        Animatable(130.dp,Dp.VectorConverter)
    )
}