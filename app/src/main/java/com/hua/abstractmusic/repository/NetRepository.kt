package com.hua.abstractmusic.repository

import android.annotation.SuppressLint
import android.net.Uri
import androidx.media3.common.MediaItem
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.net.NetSheet
import com.hua.abstractmusic.db.user.UserDao
import com.hua.abstractmusic.net.MusicService
import com.hua.abstractmusic.net.SearchService
import com.hua.abstractmusic.other.Constant.NETWORK_ALBUM_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ALL_MUSIC_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ARTIST_ID
import com.hua.abstractmusic.other.Constant.NET_SHEET_ID
import com.hua.abstractmusic.other.Constant.NULL_MEDIA_ITEM
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_ALBUM
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_ALL_MUSIC
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_ARTIST
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_BANNER
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_RECOMMEND
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_SHEET
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.other.NetWork.SERVER_ERROR
import com.hua.abstractmusic.other.NetWork.SUCCESS
import com.hua.abstractmusic.ui.home.net.detail.SearchObject
import com.hua.abstractmusic.use_case.events.MusicInsertError
import com.hua.abstractmusic.utils.toMediaItem

/**
 * @author : huaweikai
 * @Date   : 2022/01/06
 * @Desc   : 在线仓库
 */
@SuppressLint("UnsafeOptInUsageError")
class NetRepository(
    private val service: MusicService,
    private val userDao: UserDao,
    private val searchService: SearchService
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
                TYPE_NETWORK_ALL_MUSIC -> {
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
                    val getResult = service.getUserSheet(userDao.getUserInfo()!!.token)
                    if (userDao.userInRoom() == 0) {
                        emptyList<MediaItem>()
                    } else {
                        getResult.data?.forEach {
                            list.add(it.toMediaItem(parentId))
                        }
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
            val result: NetData<List<MediaItem>>
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
        val token = userDao.getToken()
        val result = service.createNewSheet(title, token)
        if (result.code != SUCCESS) throw MusicInsertError(result.msg)
    }

    suspend fun insertMusicToSheet(sheetId: String, musicId: String) {
        val token = userDao.getToken()
        val result = service.insertMusicToSheet(
            sheetId, musicId, token
        )
        if (result.code != SUCCESS) throw MusicInsertError(result.msg)
    }

    suspend fun updateSheet(sheet: NetSheet): NetData<Unit> {
        return try {
            val token = userDao.getToken()
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
            NetSheet(0, 0, "网络异常",num = 0,author = "")
        }
    }

    suspend fun removeSheetItem(sheetId: String, musicId: String): NetData<Unit> {
        return try {
            val token = userDao.getToken()
            service.deleteMusicFromSheet(sheetId, musicId, token)
        } catch (e: Exception) {
            NetData(ERROR, null, "")
        }
    }

    suspend fun removeSheet(sheetId: String) {
        try {
            val token = userDao.getToken()
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
                    val parentId = Uri.parse(NETWORK_ALL_MUSIC_ID)
                    NetData(result.code, result.data?.map {
                        it.toMediaItem(parentId)
                    },result.msg)
                }
                is SearchObject.Album -> {
                    val result = searchService.searchAlbum(searchObject.name)
                    val parentId = Uri.parse(NETWORK_ALBUM_ID)
                    NetData(result.code, result.data?.map {
                        it.toMediaItem(parentId)
                    },result.msg)
                }
                is SearchObject.Artist -> {
                    val result = searchService.searchArtist(searchObject.name)
                    val parentId = Uri.parse(NETWORK_ARTIST_ID)
                    NetData(result.code, result.data?.map {
                        it.toMediaItem(parentId)
                    },result.msg)
                }
                is SearchObject.Sheet -> {
                    val result = searchService.searchSheet(searchObject.name)
                    val parentId = Uri.parse(NET_SHEET_ID)
                    NetData(result.code, result.data?.map {
                        it.toMediaItem(parentId)
                    },result.msg)
                }
            }
        } catch (e: Exception) {
            NetData(ERROR, emptyList(), e.message ?: "")
        }
    }

    suspend fun selectItem(parentId: String):NetData<MediaItem>{
        val parentIdUri = Uri.parse(parentId)
        val id = parentIdUri.lastPathSegment ?: return NetData(ERROR,null,"")
        return when (parentIdUri.authority) {
            TYPE_NETWORK_BANNER -> {
                val result = service.selectAlbumById(id)
                NetData(result.code,result.data?.toMediaItem(parentIdUri) ?: NULL_MEDIA_ITEM,result.msg)
            }
            TYPE_NETWORK_ALBUM -> {
                val result = service.selectAlbumById(id)
                NetData(result.code,result.data?.toMediaItem(parentIdUri) ?: NULL_MEDIA_ITEM,result.msg)
            }
            TYPE_NETWORK_ARTIST -> {
                val result = service.selectArtistById(id)
                NetData(result.code,result.data?.toMediaItem(parentIdUri) ?: NULL_MEDIA_ITEM,result.msg)
            }
            TYPE_NETWORK_SHEET ->{
                val result = service.selectSheetById(id)
                NetData(result.code,result.data?.toMediaItem(parentIdUri) ?: NULL_MEDIA_ITEM,result.msg)
            }
            else-> NetData(SERVER_ERROR,null,"")
        }
    }
}