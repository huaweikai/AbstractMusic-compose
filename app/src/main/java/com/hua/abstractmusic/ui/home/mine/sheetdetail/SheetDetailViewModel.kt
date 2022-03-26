package com.hua.abstractmusic.ui.home.mine.sheetdetail

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.bean.ParcelizeMediaItem
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.net.NetSheet
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.other.NetWork
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.repository.NetRepository
import com.hua.abstractmusic.repository.Repository
import com.hua.abstractmusic.repository.UserRepository
import com.hua.abstractmusic.services.MediaConnect
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.abstractmusic.use_case.UseCase
import com.hua.abstractmusic.use_case.events.MusicInsertError
import com.hua.abstractmusic.utils.isLocal
import com.hua.abstractmusic.utils.toDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : huaweikai
 * @Date   : 2022/02/28
 * @Desc   :
 */

@HiltViewModel
@SuppressLint("UnsafeOptInUsageError")
class SheetDetailViewModel @Inject constructor(
    mediaConnect: MediaConnect,
    private val userRepository: UserRepository,
    private val netRepository: NetRepository,
    private val repository: Repository,
    private val userInfoData: UserInfoData,
    private val useCase: UseCase,
) : BaseViewModel(mediaConnect) {

    var parcelItem: ParcelizeMediaItem? = null

    val sheetDetailList = mutableStateOf<List<MediaData>>(emptyList())
    var mediaData: MediaData? = null

    val sheetDetail = MutableStateFlow(NetSheet(0, 0, "", num = 0, author = ""))

    private val listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateItem(mediaItem)
        }
    }

    fun loadData() {
        parcelItem ?: return
        if (parcelItem!!.mediaId.isLocal()) {
            refresh()
        } else {
            netRefresh()
            updateSheet()
        }
    }

    init {
        addListener(listener)
    }

    override fun refresh() {
        parcelItem ?: return
        localListMap[parcelItem!!.mediaId] = sheetDetailList
        super.refresh()
        playListMap[parcelItem!!.mediaId] = sheetDetailList
    }

    fun netRefresh() {
        parcelItem ?: return
        viewModelScope.launch {
            _screenState.value = LCE.Loading
            val sheets = netRepository.selectMusicById(Uri.parse(parcelItem!!.mediaId))
            if (sheets?.code == NetWork.SUCCESS) {
                sheetDetailList.value = sheets.data?.map { MediaData(it) } ?: emptyList()
                _screenState.value = LCE.Success
                playListMap[parcelItem!!.mediaId] = sheetDetailList
            } else {
                _screenState.value = LCE.Error
            }

        }
    }

    fun updateSheet() {
        parcelItem ?: return
        viewModelScope.launch {
            sheetDetail.value = netRepository.selectSheetById(Uri.parse(parcelItem!!.mediaId))
        }
    }

    fun putSheetArt(
        url: String,
        contentResolver: ContentResolver,
    ) {
        parcelItem ?: return
        val uri = Uri.parse(url)
        val byte = contentResolver.openInputStream(uri)
        val file = DocumentFile.fromSingleUri(mediaConnect.context, uri)
        viewModelScope.launch(Dispatchers.IO) {
            val fileName =
                "${Constant.BUCKET_SHEET_IMG}/${Uri.parse(parcelItem!!.mediaId).lastPathSegment}-sheet-${
                    System.currentTimeMillis().toDate()
                }.png"
            userRepository.upLoadFile.putFile(
                fileName,
                byte,
                file,
                onSuccess = {
                    netRepository.updateSheet(
                        sheetDetail.value.copy(artUri = it)
                    ).also {
                        if (it.code == NetWork.SUCCESS) {
                            updateSheet()
                        }
                    }
                },
                onError = {

                },
                onCompletion = {
                    contentResolver.delete(uri, null, null)
                }
            )
        }
    }

    fun hasPermission(): Boolean {
        parcelItem ?: return false
        if(parcelItem!!.userId == null) return true
        return parcelItem!!.userId == userInfoData.userInfo.value.userBean?.id
    }

    suspend fun removeNetSheetItem(
        id: String
    ): NetData<Unit> {
        val musicId = Uri.parse(id).lastPathSegment!!
        val sheetId = Uri.parse(parcelItem?.mediaId).lastPathSegment!!
        return if (parcelItem?.mediaId?.isLocal() == false) {
            val result = netRepository.removeSheetItem(sheetId, musicId)
            val list = sheetDetailList.value.toMutableList()
            list.remove(list.find { it.mediaId == id })
            sheetDetailList.value = list
            result
        } else {
            if (repository.removeSheetItem(sheetId, musicId) == 1) {
                refresh()
                NetData(NetWork.SUCCESS, null, "")
            } else {
                NetData(NetWork.ERROR, null, "移出失败，稍后尝试")
            }
        }
    }

    val moreArtistList = mutableStateOf(emptyList<MediaItem>())


    fun selectArtistByMusicId(item: MediaItem) {
        if (item.mediaId.isLocal()) {
            val artistId: Long = item.mediaMetadata.extras?.getLong("artistId") ?: 0L
            val parentId = "${Constant.LOCAL_ARTIST_ID}/$artistId"
            moreArtistList.value =
                listOf(mediaConnect.itemTree.getItem(parentId) ?: Constant.NULL_MEDIA_ITEM)
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

    val moreAlbum = mutableStateOf(Constant.NULL_MEDIA_ITEM)

    fun selectAlbumByMusicId(item: MediaItem) {
        val albumId: Long = item.mediaMetadata.extras?.getLong("albumId") ?: 0L
        if (item.mediaId.isLocal()) {
            val parentId = "${Constant.LOCAL_ALBUM_ID}/$albumId"
            moreAlbum.value = mediaConnect.itemTree.getItem(parentId) ?: Constant.NULL_MEDIA_ITEM
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

    val user get() = userInfoData.userInfo.value

    val sheetList = mutableStateOf(emptyList<MediaItem>())

    fun refresh(isLocal:Boolean){
        sheetList.value = mediaConnect.itemTree.getCacheItems(
            if(isLocal) Constant.LOCAL_SHEET_ID else "${Constant.ROOT_SCHEME}${user.userToken}"
        )
    }
}