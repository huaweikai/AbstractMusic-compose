package com.hua.abstractmusic.ui.popItem

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.google.common.util.concurrent.MoreExecutors
import com.hua.abstractmusic.bean.ParcelizeMediaItem
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.Constant.LOCAL_SHEET_ID
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.services.MediaConnect
import com.hua.abstractmusic.services.MediaItemTree
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.use_case.events.MusicInsertError
import com.hua.abstractmusic.utils.isLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/03/26
 * @Desc   :
 */
@SuppressLint("UnsafeOptInUsageError")
@HiltViewModel
class PopItemViewModel @Inject constructor(
    private val useCase: UseCase,
    private val netRepository: NetRepository,
    private val itemTree: MediaItemTree,
    private val mediaConnect: MediaConnect,
    private val userInfo: UserInfoData
) : ViewModel() {
    val user get() = userInfo.userInfo.value

    val sheetList = mutableStateOf(emptyList<MediaItem>())

    suspend fun refresh(isLocal: Boolean) {
        if (isLocal) {
            val browser = mediaConnect.browser ?: return
            val browserFuture = browser.getChildren(
                LOCAL_SHEET_ID, 0, Int.MAX_VALUE, null
            )
            browserFuture.addListener({
                sheetList.value = browserFuture.get().value ?: emptyList()
            }, MoreExecutors.directExecutor())
        } else {
            val result = netRepository.selectUserSheet()
            if (result.isSuccess) {
                sheetList.value = result.getOrNull() ?: emptyList()
            }
        }
    }

    val moreArtistList = mutableStateOf(emptyList<MediaItem>())


    fun selectArtistByMusicId(item: MediaItem) {
        if (item.mediaId.isLocal()) {
            val artistId: Long = item.mediaMetadata.extras?.getLong("artistId") ?: 0L
            val parentId = "${Constant.LOCAL_ARTIST_ID}/$artistId"
            moreArtistList.value =
                listOf(itemTree.getItem(parentId) ?: NULL_MEDIA_ITEM)
        } else {
            viewModelScope.launch {
                moreArtistList.value =
                    netRepository.selectArtistByMusicId(Uri.parse(item.mediaId)).data ?: emptyList()
            }
        }
    }

    fun clearArtist() {
        moreArtistList.value = emptyList()
    }


    fun addQueue(item: MediaItem, nextPlay: Boolean = false): Int {
        val browser = mediaConnect.browser ?: return 0
        val index = if (nextPlay) browser.currentMediaItemIndex + 1 else browser.mediaItemCount
        browser.addMediaItem(index,item)
        // 没有相关回调，直接手动更新
        mediaConnect.updatePlayList()
        return index
    }

    suspend fun insertMusicToSheet(
        mediaItem: MediaItem,
        sheetItem: MediaItem
    ): Pair<Boolean, String> {
        val sheetId = Uri.parse(sheetItem.mediaId).lastPathSegment
        return try {
            if (sheetItem.mediaId.isLocal()) {
                useCase.insertSheetCase(mediaItem, sheetId!!.toInt())
            } else {
                val mediaId = Uri.parse(mediaItem.mediaId).lastPathSegment
                netRepository.insertMusicToSheet(sheetId!!, mediaId!!)
            }
            Pair(true, "${mediaItem.mediaMetadata.title}已加入歌单${sheetItem.mediaMetadata.title}")
        } catch (e: MusicInsertError) {
            Pair(false, e.message ?: "")
        }
    }


    val moreAlbum = mutableStateOf(NULL_MEDIA_ITEM)

    fun selectAlbumByMusicId(item: MediaItem) {
        val albumId: Long = item.mediaMetadata.extras?.getLong("albumId") ?: 0L
        if (item.mediaId.isLocal()) {
            val parentId = "${Constant.LOCAL_ALBUM_ID}/$albumId"
            moreAlbum.value = itemTree.getItem(parentId) ?: NULL_MEDIA_ITEM
        } else {
            viewModelScope.launch {
                moreAlbum.value = netRepository.selectAlbumById(albumId.toString()).data
                    ?: Constant.NULL_MEDIA_ITEM
            }
        }
    }

    fun clearAlbum() {
        moreAlbum.value = Constant.NULL_MEDIA_ITEM
    }

}