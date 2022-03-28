package com.hua.abstractmusic.repository

import android.annotation.SuppressLint
import android.net.Uri
import androidx.media3.common.MediaItem
import com.hua.abstractmusic.bean.net.HomeBean
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.net.NetSheet
import com.hua.abstractmusic.http.DataState
import com.hua.abstractmusic.http.RequestStatus
import com.hua.abstractmusic.net.MusicService
import com.hua.abstractmusic.net.SearchService
import com.hua.abstractmusic.other.Constant.NETWORK_ALBUM_ID
import com.hua.abstractmusic.other.Constant.NETWORK_MUSIC_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ARTIST_ID
import com.hua.abstractmusic.other.Constant.NETWORK_BANNER_ID
import com.hua.abstractmusic.other.Constant.NET_SHEET_ID
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.other.Constant.ROOT_SCHEME
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_ALBUM
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_MUSIC
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_ARTIST
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_BANNER
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_RECOMMEND
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_SHEET
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.other.NetWork.SERVER_ERROR
import com.hua.abstractmusic.other.NetWork.SUCCESS
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.ui.home.net.detail.SearchObject
import com.hua.abstractmusic.use_case.events.MusicInsertError
import com.hua.abstractmusic.utils.toMediaItem
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author : huaweikai
 * @Date   : 2022/01/06
 * @Desc   : 在线仓库
 */
@SuppressLint("UnsafeOptInUsageError")
class NetRepository(
    private val service: MusicService,
    private val searchService: SearchService,
    private val userInfoData: UserInfoData
) {

    suspend fun selectList(parentId: Uri): NetData<List<MediaItem>>? {
        return try {
            val result: NetData<List<MediaItem>>
            when (parentId.authority) {
                TYPE_NETWORK_ALBUM -> {
                    val list = mutableListOf<MediaItem>()
                    val getResult = service.getRecommendAlbumList()
                    getResult.data?.forEach {
                        list.add(it.toMediaItem(parentId))
                    }
                    result = NetData(getResult.code, list, getResult.msg)
                }
                TYPE_NETWORK_MUSIC -> {
                    val list = mutableListOf<MediaItem>()
                    val getResult = service.getAllMusic()
                    getResult.data?.forEach {
                        list.add(it.toMediaItem(parentId))
                    }
                    result = NetData(getResult.code, list, getResult.msg)
                }
                TYPE_NETWORK_ARTIST -> {
                    val list = mutableListOf<MediaItem>()
                    val getResult = service.getArtistList()
                    getResult.data?.forEach {
                        list.add(it.toMediaItem(parentId))
                    }
                    result = NetData(getResult.code, list, getResult.msg)
                }
                TYPE_NETWORK_BANNER -> {
                    val list = mutableListOf<MediaItem>()
                    val getResult = service.getBanner()
                    getResult.data?.forEach {
                        list.add(it.toMediaItem(parentId))
                    }
                    result = NetData(getResult.code, list, getResult.msg)
                }
                TYPE_NETWORK_RECOMMEND -> {
                    val list = mutableListOf<MediaItem>()
                    val getResult = service.getRecommend()
                    getResult.data?.forEach {
                        list.add(it.toMediaItem(parentId))
                    }
                    result = NetData(getResult.code, list, getResult.msg)
                }
                TYPE_NETWORK_SHEET -> {
                    val list = mutableListOf<MediaItem>()
                    val getResult = service.getUserSheet(userInfoData.userInfo.value.userToken)
                    if (userInfoData.userInfo.value.isLogin) {
                        getResult.data?.forEach {
                            list.add(it.toMediaItem(parentId))
                        }
                    } else {
                        emptyList<MediaItem>()
                    }
                    result = NetData(getResult.code, list, getResult.msg)
                }
                else -> result = NetData(ERROR, emptyList(), "")
            }
            result
        } catch (e: Exception) {
            NetData(ERROR, emptyList(), "")
        }
    }

    suspend fun selectMusicById(parentId: Uri): NetData<List<MediaItem>>? {
        return try {
            val id = parentId.lastPathSegment
            val mediaItems = mutableListOf<MediaItem>()
            val getResult = if (id == null) {
                NetData(ERROR, null, "id为空")
            } else {
                when (parentId.authority) {
                    TYPE_NETWORK_ALBUM -> {
                        service.getMusicByAlbum(id)
                    }
                    TYPE_NETWORK_ARTIST -> {
                        service.getMusicByArtist(id)
                    }
                    TYPE_NETWORK_BANNER -> {
                        service.getMusicByAlbum(id)
                    }
                    TYPE_NETWORK_RECOMMEND -> {
                        service.getMusicBySheetId(id)
                    }
                    TYPE_NETWORK_SHEET -> {
                        service.getMusicBySheetId(id)
                    }
                    userInfoData.userInfo.value.userToken ->{
                        service.getMusicBySheetId(id)
                    }
                    else -> NetData(ERROR, null, "未知错误")
                }
            }
            if (getResult.code == SUCCESS) {
                getResult.data?.forEach {
                    mediaItems.add(it.toMediaItem(parentId))
                }
                NetData(getResult.code, mediaItems, "")
            } else {
                NetData(ERROR, null, getResult.msg)
            }
        } catch (e: Exception) {
            NetData(ERROR, null, "网络问题")
        }
    }

    suspend fun selectLyrics(id: String): String {
        return try {
            val result = service.getMusicLyrics(id)
            if (result.code != SUCCESS) {
                ""
            } else {
                result.data ?: ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun createNewSheet(title: String) {
        val token = userInfoData.userInfo.value.userToken
        val result = service.createNewSheet(title, token)
        if (result.code != SUCCESS) throw MusicInsertError(result.msg)
    }

    suspend fun insertMusicToSheet(sheetId: String, musicId: String) {
        val token = userInfoData.userInfo.value.userToken
        val result = service.insertMusicToSheet(
            sheetId, musicId, token
        )
        if (result.code != SUCCESS) throw MusicInsertError(result.msg)
    }

    suspend fun updateSheet(sheet: NetSheet): NetData<Unit> {
        return try {
            val token = userInfoData.userInfo.value.userToken
            service.updateSheet(
                token, sheet
            )
        } catch (e: Exception) {
            NetData(ERROR, null, "")
        }
    }

    suspend fun selectSheetById(parentId: Uri): NetSheet {
        return try {
            val sheetId = parentId.lastPathSegment
            val result = service.selectSheetById(sheetId!!)
            if (result.code == SUCCESS) {
                result.data!!
            } else {
                NetSheet(0, 0, "网络异常", num = 0, author = "")
            }
        } catch (e: Exception) {
            NetSheet(0, 0, "网络异常", num = 0, author = "")
        }
    }

    suspend fun removeSheetItem(sheetId: String, musicId: String): NetData<Unit> {
        return try {
            val token = userInfoData.userInfo.value.userToken
            service.deleteMusicFromSheet(sheetId, musicId, token)
        } catch (e: Exception) {
            NetData(ERROR, null, "")
        }
    }

    suspend fun removeSheet(sheetId: String) {
        try {
            val token = userInfoData.userInfo.value.userToken
            service.deleteSheet(sheetId, token)
        } catch (e: Exception) {

        }
    }

    suspend fun selectArtistByMusicId(parentId: Uri): NetData<List<MediaItem>> {
        return try {
            val musicId = parentId.lastPathSegment ?: throw Exception("musicId为空")
            val result = service.getArtistByMusicId(musicId)
            val uri = Uri.parse(NETWORK_ARTIST_ID)
            val list = result.data?.map {
                it.toMediaItem(uri)
            }
            NetData(result.code, list, result.msg)
        } catch (e: Exception) {
            NetData(ERROR, emptyList(), e.message ?: "")
        }
    }

    suspend fun selectAlbumById(albumId: String): NetData<MediaItem> {
        return try {
            val result = service.selectAlbumById(albumId)
            val uri = Uri.parse(NETWORK_ALBUM_ID)
            NetData(result.code, result.data?.toMediaItem(uri), result.msg)
        } catch (e: Exception) {
            NetData(ERROR, NULL_MEDIA_ITEM, e.message ?: "")
        }
    }

    suspend fun selectAlbumByArtist(parentId: Uri): NetData<List<MediaItem>>? {
        return try {
            val artistId = parentId.lastPathSegment ?: throw Exception("artistId为空")
            val result = service.getAlbumByArtist(artistId)
            val uri = Uri.parse(NETWORK_ALBUM_ID)
            val list = result.data?.map {
                it.toMediaItem(uri)
            }
            NetData(result.code, list, result.msg)
        } catch (e: Exception) {
            NetData(ERROR, emptyList(), e.message ?: "")
        }
    }

    suspend fun search(searchObject: SearchObject): NetData<List<MediaItem>> {
        return try {
            when (searchObject) {
                is SearchObject.Music -> {
                    val result = searchService.searchMusic(searchObject.name)
                    val parentId = Uri.parse(NETWORK_MUSIC_ID)
                    NetData(result.code, result.data?.map {
                        it.toMediaItem(parentId)
                    }, result.msg)
                }
                is SearchObject.Album -> {
                    val result = searchService.searchAlbum(searchObject.name)
                    val parentId = Uri.parse(NETWORK_ALBUM_ID)
                    NetData(result.code, result.data?.map {
                        it.toMediaItem(parentId)
                    }, result.msg)
                }
                is SearchObject.Artist -> {
                    val result = searchService.searchArtist(searchObject.name)
                    val parentId = Uri.parse(NETWORK_ARTIST_ID)
                    NetData(result.code, result.data?.map {
                        it.toMediaItem(parentId)
                    }, result.msg)
                }
                is SearchObject.Sheet -> {
                    val result = searchService.searchSheet(searchObject.name)
                    val parentId = Uri.parse(NET_SHEET_ID)
                    NetData(result.code, result.data?.map {
                        it.toMediaItem(parentId)
                    }, result.msg)
                }
            }
        } catch (e: Exception) {
            NetData(ERROR, emptyList(), e.message ?: "")
        }
    }

    suspend fun selectItem(parentId: String): NetData<MediaItem> {
        val parentIdUri = Uri.parse(parentId)
        val id = parentIdUri.lastPathSegment ?: return NetData(ERROR, null, "")
        return when (parentIdUri.authority) {
            TYPE_NETWORK_BANNER -> {
                val result = service.selectAlbumById(id)
                NetData(
                    result.code,
                    result.data?.toMediaItem(parentIdUri) ?: NULL_MEDIA_ITEM,
                    result.msg
                )
            }
            TYPE_NETWORK_ALBUM -> {
                val result = service.selectAlbumById(id)
                NetData(
                    result.code,
                    result.data?.toMediaItem(parentIdUri) ?: NULL_MEDIA_ITEM,
                    result.msg
                )
            }
            TYPE_NETWORK_ARTIST -> {
                val result = service.selectArtistById(id)
                NetData(
                    result.code,
                    result.data?.toMediaItem(parentIdUri) ?: NULL_MEDIA_ITEM,
                    result.msg
                )
            }
            TYPE_NETWORK_SHEET -> {
                val result = service.selectSheetById(id)
                NetData(
                    result.code,
                    result.data?.toMediaItem(parentIdUri) ?: NULL_MEDIA_ITEM,
                    result.msg
                )
            }
            else -> NetData(SERVER_ERROR, null, "")
        }
    }

    suspend fun loadHomeData(): Result<HomeBean> {
        return runCatching {
            val banners = service.getBanner().data ?: emptyList()
            val songs = service.getAllMusic().data ?: emptyList()
            val sheets = service.getRecommend().data ?: emptyList()
            val albums = service.getRecommendAlbumList().data ?: emptyList()

            val bannerId = Uri.parse(NETWORK_BANNER_ID)
            val songId = Uri.parse(NETWORK_MUSIC_ID)
            val sheetId = Uri.parse(NET_SHEET_ID)
            val albumId = Uri.parse(NETWORK_ALBUM_ID)

            HomeBean(
                banners = banners.map { it.toMediaItem(bannerId) },
                songs = songs.map { it.toMediaItem(songId) },
                sheets = sheets.map { it.toMediaItem(sheetId) },
                albums = albums.map { it.toMediaItem(albumId) }
            )
        }
    }

    suspend fun selectUserSheet():Result<List<MediaItem>>{
         return runCatching {
            val user = userInfoData.userInfo.value
            if(user.isLogin){
                val result = service.getUserSheet(user.userToken)
                result.data?.map { it.toMediaItem(Uri.parse("$ROOT_SCHEME${user.userToken}")) } ?: emptyList()
            }else{
                emptyList()
            }
        }
    }

    suspend fun <T :NetData<*>> httpRequest(
        stateFlow: MutableStateFlow<RequestStatus<T>> ?= null,
        block:suspend () -> T?
    ):T?{
        return try {
            stateFlow?.emit(RequestStatus(status = DataState.STATE_LOADING))
            val data = block()
            stateFlow?.emit(
                if(data != null){
                    RequestStatus(
                        code = data.code,
                        status = when(data.code){
                            in 200..299 -> DataState.STATE_SUCCESS
                            in 300..599 -> DataState.STATE_FAILED
                            else -> DataState.STATE_UNKNOWN
                        },
                        msg = data.msg,
                        json = data
                    )
                }else{
                    RequestStatus(status = DataState.STATE_EMPTY)
                }
            )
            data
        }catch (e:Exception){
            stateFlow?.emit(RequestStatus(status = DataState.STATE_ERROR, error = e))
            null
        }
    }
}