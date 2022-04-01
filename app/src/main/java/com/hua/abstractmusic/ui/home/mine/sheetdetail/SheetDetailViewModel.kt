package com.hua.abstractmusic.ui.home.mine.sheetdetail

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.google.gson.Gson
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.bean.ParcelizeMediaItem
import com.hua.abstractmusic.bean.Sheet
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

    var isLocal:Boolean = true

    var parcelItem: ParcelizeMediaItem? = null
        set(value) {
            field = value
            isLocal = field!!.mediaId.isLocal()
            sheetDetail.value = sheetDetail.value.copy(
                id = Uri.parse(field!!.mediaId).lastPathSegment?.toInt() ?: 0,
                userId = field?.userId ?: -1,
                title = field?.title ?: "",
                artUri = field?.artUri,
                sheetDesc = field?.desc,
                num = field?.trackNumber ?: 0,
                author = field?.artist ?: ""
            )
        }

    val sheetDetailList = mutableStateOf<List<MediaData>>(emptyList())
    val sheetChangeList = mutableStateOf<List<MediaData>>(emptyList())
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
            refreshSheetLocalList()
        } else {
            netfreshSheetList()
            refreshSheetDesc()
        }
    }

    fun refreshSheetDesc() {
        viewModelScope.launch {
            if (parcelItem?.mediaId?.isLocal() == true) {
                val localSheet = repository.selectSheetBySheetId(sheetDetail.value.id)
                sheetDetail.value = sheetDetail.value.copy(
                    id = localSheet.sheetId,
                    userId = -1,
                    title = localSheet.title,
                    artUri = localSheet.artUri,
                    sheetDesc = localSheet.desc
                )
            } else {
                sheetDetail.value = netRepository.selectSheetById(Uri.parse(parcelItem!!.mediaId))
            }
        }
    }

    init {
        addListener(listener)
    }

    fun refreshSheetLocalList() {
        parcelItem ?: return
        localListMap[parcelItem!!.mediaId] = sheetDetailList
        super.refresh()
        sheetChangeList.value = mediaConnect.itemTree.getCacheItems(parcelItem!!.mediaId).map {
            MediaData(it)
        }
        playListMap[parcelItem!!.mediaId] = sheetDetailList
    }

    fun netfreshSheetList() {
        parcelItem ?: return
        viewModelScope.launch {
            _screenState.value = LCE.Loading
            val sheets = netRepository.selectMusicById(Uri.parse(parcelItem!!.mediaId))
            if (sheets?.code == NetWork.SUCCESS) {
                mediaConnect.itemTree.addMusicToTree(parcelItem!!.mediaId, sheets.data)
                sheetDetailList.value = sheets.data?.map {
                    MediaData(
                        it,
                        isPlaying = it.mediaId == mediaConnect.browser?.currentMediaItem?.mediaId
                    )
                } ?: emptyList()
                sheetChangeList.value =
                    mediaConnect.itemTree.getCacheItems(parcelItem!!.mediaId).map {
                        MediaData(it)
                    }
                _screenState.value = LCE.Success
                playListMap[parcelItem!!.mediaId] = sheetDetailList
            } else {
                _screenState.value = LCE.Error
            }
        }
    }

    fun uploadSheetDesc(
        onBack:()->Unit
    ) {
        parcelItem ?: return
        viewModelScope.launch {
            val sheetDesc = sheetDetail.value
            if (parcelItem!!.mediaId.isLocal()) {
                repository.updateSheetDesc(
                    Sheet(
                        sheetId = sheetDesc.id,
                        title = sheetDesc.title,
                        artUri = sheetDesc.artUri,
                        desc = sheetDesc.sheetDesc
                    )
                )
                onBack()
            } else {
                putImgUpdateNetSheet(sheetDesc,onBack)
            }
        }
    }

    fun updateSelect(index: Int) {
        val list = sheetChangeList.value.toMutableList()
        var temp = list[index]
        val isPlaying = !temp.isPlaying
        temp = temp.copy(
            isPlaying = isPlaying
        )
        list[index] = temp
        sheetChangeList.value = list
    }

    fun uploadSheetDesc(sheet: NetSheet) {
        sheetDetail.value = sheet
    }

    fun putImgUpdateNetSheet(
        sheet: NetSheet,
        onBack:()->Unit
    ) {
        if (sheet.artUri == parcelItem?.artUri) {
            viewModelScope.launch {
                netRepository.updateSheet(sheet)
                onBack()
            }
        } else {
            val uri = Uri.parse(sheet.artUri)
            val byte = mediaConnect.context.contentResolver.openInputStream(uri)
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
                        val path = "https://abstractmusic.obs.cn-north-4.myhuaweicloud.com/$fileName"
                        netRepository.updateSheet(sheet.copy(artUri = path))
                    },
                    onError = {
                        netRepository.updateSheet(sheet.copy(artUri = parcelItem?.artUri))
                    },
                    onCompletion = {
                        mediaConnect.context.contentResolver.delete(uri, null, null)
                        onBack()
                    }
                )
            }
        }
    }

    fun hasPermission(): Boolean {
        parcelItem ?: return false
        if (parcelItem!!.userId == null) return true
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
            sheetChangeList.value = list
            result
        } else {
            if (repository.removeSheetItem(sheetId, musicId) == 1) {
                refreshSheetLocalList()
                NetData(NetWork.SUCCESS, null, "")
            } else {
                NetData(NetWork.ERROR, null, "移出失败，稍后尝试")
            }
        }
    }

    fun removeNetSheetList() {
        viewModelScope.launch {
            sheetChangeList.value.forEach {
                if (it.isPlaying) {
                    removeNetSheetItem(it.mediaId)
                }
            }
            if (parcelItem?.mediaId?.isLocal() == true) {
                refresh()
            } else {
                netfreshSheetList()
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

    fun refreshSheetLocalList(isLocal: Boolean) {
        sheetList.value = mediaConnect.itemTree.getCacheItems(
            if (isLocal) Constant.LOCAL_SHEET_ID else "${Constant.ROOT_SCHEME}${user.userToken}"
        )
    }

    fun deleteSheet() {
        viewModelScope.launch {
            val id = Uri.parse(parcelItem?.mediaId).lastPathSegment
            try {
                if (parcelItem?.mediaId?.isLocal() == true) {
                    userRepository.removeSheet(id!!)
                } else {
                    netRepository.removeSheet(id!!)
                }
                refresh()
            } catch (e: Exception) {
            }
        }
    }
    fun getSheetDesc():String{
        val sheet = sheetDetail.value
        return Gson().toJson(
            ParcelizeMediaItem(
                mediaId = sheet.id.toString(),
                title = sheet.title,
                artist = sheet.author,
                artUri = sheet.artUri ?: "",
                desc = sheet.sheetDesc
            )
        )
    }
}