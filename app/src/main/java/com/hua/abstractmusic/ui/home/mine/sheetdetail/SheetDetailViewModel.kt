package com.hua.abstractmusic.ui.home.mine.sheetdetail

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.google.gson.Gson
import com.hua.abstractmusic.base.viewmodel.BaseViewModel
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.repository.*
import com.hua.abstractmusic.ui.utils.LCE
import com.hua.service.usecase.events.MusicInsertError
import com.hua.abstractmusic.utils.isLocal
import com.hua.abstractmusic.utils.toDate
import com.hua.model.music.MediaData
import com.hua.model.other.Constants
import com.hua.model.parcel.ParcelizeMediaItem
import com.hua.model.sheet.SheetVO
import com.hua.network.ApiResult
import com.hua.network.get
import com.hua.network.onFailure
import com.hua.network.onSuccess
import com.hua.service.MediaConnect
import com.hua.service.usecase.UseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
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
    private val netRepository: NetWorkRepository,
    private val repository: LocalRepository,
    private val userInfoData: UserInfoData,
) : BaseViewModel(mediaConnect) {

    var isLocal: Boolean = true

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

    val sheetDetail = MutableStateFlow(SheetVO(0, 0, "", num = 0, author = ""))

    private val listener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateItem(mediaItem)
        }
    }

    val snackBarTitle = MutableSharedFlow<String>()

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
        parcelItem ?: return
        viewModelScope.launch {
            val result = if (parcelItem!!.mediaId.isLocal()) {
                repository.selectSheetById(Uri.parse(sheetDetail.value.id.toString()))
            } else {
                netRepository.selectSheetById(Uri.parse(parcelItem!!.mediaId))
            }
            result.onSuccess {
                sheetDetail.value = it.toSheetVO()
            }
        }
    }

    init {
        addListener(listener)
    }

    private fun refreshSheetLocalList() {
        parcelItem ?: return
        localListMap[parcelItem!!.mediaId] = sheetDetailList
        super.refresh()
        sheetChangeList.value = mediaConnect.itemTree.getCacheItems(parcelItem!!.mediaId).map {
            MediaData(it)
        }
        playListMap[parcelItem!!.mediaId] = sheetDetailList
    }

   private fun netfreshSheetList() {
        parcelItem ?: return
        viewModelScope.launch {
            _screenState.value = LCE.Loading
            val sheets = netRepository.selectMusicByType(Uri.parse(parcelItem!!.mediaId))
            if (sheets is ApiResult.Success) {
                mediaConnect.itemTree.addMusicToTree(parcelItem!!.mediaId, sheets.data)
                sheetDetailList.value = sheets.data.map {
                    MediaData(
                        it,
                        isPlaying = it.mediaId == mediaConnect.browser?.currentMediaItem?.mediaId
                    )
                }
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
        onBack: () -> Unit
    ) {
        parcelItem ?: return
        viewModelScope.launch {
            val sheetDesc = sheetDetail.value
            if (parcelItem!!.mediaId.isLocal()) {
                repository.updateSheet(sheetDesc)
                onBack()
            } else {
                putImgUpdateNetSheet(sheetDesc, onBack)
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

    fun uploadSheetDesc(sheet: SheetVO) {
        sheetDetail.value = sheet
    }

    private fun putImgUpdateNetSheet(
        sheet: SheetVO,
        onBack: () -> Unit
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
                        val path =
                            "https://abstractmusic.obs.cn-north-4.myhuaweicloud.com/$fileName"
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
    ): ApiResult<Unit> {
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
            val result = repository.removeSheetItem(sheetId, musicId)
            if (result is ApiResult.Success) {
                refreshSheetLocalList()
            }
            result
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
        viewModelScope.launch {
            val result = if(item.mediaId.isLocal()){
                repository.selectArtistByMusicId(item)
            }else{
                netRepository.selectArtistByMusicId(item)
            }
            moreArtistList.value = result.get { emptyList() }
        }
    }

    fun clearArtist() {
        moreArtistList.value = emptyList()
    }

    val moreAlbum = mutableStateOf(Constant.NULL_MEDIA_ITEM)

    fun selectAlbumByMusicId(item: MediaItem) {
        viewModelScope.launch {
            val result = if(item.mediaId.isLocal()){
                repository.selectAlbumByMusicId(item)
            }else{
                netRepository.selectAlbumByMusicId(item)
            }
            moreAlbum.value = result.get { Constant.NULL_MEDIA_ITEM }
        }
    }

    fun clearAlbum() {
        moreAlbum.value = Constant.NULL_MEDIA_ITEM
    }

    fun insertMusicToSheet(
        mediaItem: MediaItem,
        sheetItem: MediaItem
    ) {
        viewModelScope.launch {
            val sheetId = Uri.parse(sheetItem.mediaId).lastPathSegment
            val result = if(sheetItem.mediaId.isLocal()){
                repository.insertMusicToSheet(sheetId!!,mediaItem)
            }else{
                netRepository.insertMusicToSheet(sheetId!!,mediaItem)
            }
            result.onSuccess {
                showSnackBar("加入歌单成功")
            }
            result.onFailure {
                showSnackBar(it.errorMsg ?:"")
            }
        }

    }

    val user get() = userInfoData.userInfo.value

    val sheetList = mutableStateOf(emptyList<MediaItem>())

    fun refreshSheetLocalList(isLocal: Boolean) {
        sheetList.value = mediaConnect.itemTree.getCacheItems(
            if (isLocal) Constants.LOCAL_SHEET_ID else "${Constants.ROOT_SCHEME}${user.userToken}"
        )
    }

    fun deleteSheet() {
        viewModelScope.launch {
            val id = Uri.parse(parcelItem?.mediaId).lastPathSegment
            if (parcelItem?.mediaId?.isLocal() == true) {
                repository.deleteSheet(id!!)
            }else{
                netRepository.deleteSheet(id!!)
            }
            refresh()
        }
    }

    fun getSheetDesc(): String {
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

    private fun showSnackBar(message:String){
        viewModelScope.launch {
            snackBarTitle.emit(message)
        }
    }
}

@SuppressLint("UnsafeOptInUsageError")
private fun MediaItem.toSheetVO() = SheetVO(
    id = Uri.parse(this.mediaId).lastPathSegment?.toInt() ?: 0,
    userId = this.mediaMetadata.extras?.getInt("userId") ?: -1,
    title = this.mediaMetadata.title.toString(),
    artUri = this.mediaMetadata.artworkUri.toString(),
    sheetDesc = "${this.mediaMetadata.subtitle?:""}",
    num = 0,
    author = this.mediaMetadata.artist.toString()
)