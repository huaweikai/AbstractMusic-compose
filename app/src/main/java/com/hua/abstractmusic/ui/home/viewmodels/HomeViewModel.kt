package com.hua.abstractmusic.ui.home.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import androidx.media2.session.MediaBrowser
import androidx.media2.session.MediaController
import androidx.media2.session.SessionCommandGroup
import com.google.android.exoplayer2.Player
import com.google.common.util.concurrent.MoreExecutors
import com.hua.abstractmusic.base.BaseBrowserViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.other.Constant.NETWORK_ALBUM_ID
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.utils.title
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/01/07
 * @Desc   : viewmodel
 */
@HiltViewModel
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

        }

        override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
            updateItem(item)
        }

        override fun onPlayerStateChanged(controller: MediaController, state: Int) {
            updatePlayState(state)
        }
    }



    override fun initializeController() {
        connectBrowserService(browserCallback)
    }

    val nullMediaData = MediaItem.Builder().setMetadata(
            MediaMetadata.Builder()
                .apply {
                    title = "你好生活"
                }
                .build()).build()


    private val _netAlbum =  mutableStateOf<List<MediaData>>(emptyList())
    val netAlbum : State<List<MediaData>> get() = _netAlbum

    private val _currentItem = mutableStateOf(nullMediaData)
    val currentItem :State<MediaItem> get() = _currentItem

    //播放状态
    private val _playerState = mutableStateOf(SessionPlayer.PLAYER_STATE_IDLE)
    val playerState get() = _playerState

    private fun updatePlayState(@SessionPlayer.PlayerState playState: Int) {
        _playerState.value = playState
    }

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
                Log.d("TAG", "init: ${netAlbum.value}")
            }
        },MoreExecutors.directExecutor())
    }

    private fun updateItem(item:MediaItem?){
        item?:return
        browser?:return
        _netAlbum.value = _netAlbum.value.toMutableList().map {
            val isPlaying =it.mediaId == browser!!.currentMediaItem?.metadata?.mediaId
            it.copy(isPlaying = isPlaying)
        }
        _currentItem.value = item?:nullMediaData
    }

    fun skipIem(){
        val browser = browser?:return
        browser.skipToNextPlaylistItem()
    }

    fun playOrPause(){
        val browser = browser?:return
        if(browser.playerState == SessionPlayer.PLAYER_STATE_PLAYING){
            browser.pause()
        }else{
            browser.play()
        }
    }
}