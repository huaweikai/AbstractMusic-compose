package com.hua.abstractmusic.repository

import android.annotation.SuppressLint
import android.net.Uri
import androidx.media3.common.MediaItem
import com.hua.abstractmusic.bean.net.NetData
import com.hua.abstractmusic.bean.net.NetSheet
import com.hua.abstractmusic.db.user.UserDao
import com.hua.abstractmusic.net.MusicService
import com.hua.abstractmusic.other.Constant.NETWORK_ALBUM_ID
import com.hua.abstractmusic.other.Constant.NETWORK_ARTIST_ID
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_ALBUM
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_ALL_MUSIC
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_ARTIST
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_BANNER
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_RECOMMEND
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_SHEET
import com.hua.abstractmusic.other.NetWork.ERROR
import com.hua.abstractmusic.other.NetWork.SUCCESS
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
    private val userDao: UserDao
) {

    suspend fun selectList(parentId: Uri): NetData<List<MediaItem>>? {
        return try {
            val result: NetData<List<MediaItem>>
            when (parentId.authority) {
                TYPE_NETWORK_ALBUM -> {
                    val list = mutableListOf<MediaItem>()
                    val getResult = service.getAlbumList()
                    getResult.data?.forEach {
                        list.add(it.toMediaItem(parentId))
                    }
                    result = NetData(getResult.code, list, getResult.msg)
                }
                TYPE_NETWORK_ALL_MUSIC -> {
                    val list = mutableListOf<MediaItem>()
                    val getResult = service.getAlbumList()
                    service.getAllMusic().data?.forEach {
                        list.add(it.toMediaItem(parentId))
                    }
                    result = NetData(getResult.code, list, getResult.msg)
                }
                TYPE_NETWORK_ARTIST -> {
                    val list = mutableListOf<MediaItem>()
                    val getResult = service.getAlbumList()
                    service.getArtistList().data?.forEach {
                        list.add(it.toMediaItem(parentId))
                    }
                    result = NetData(getResult.code, list, getResult.msg)
                }
                TYPE_NETWORK_BANNER -> {
                    val list = mutableListOf<MediaItem>()
                    val getResult = service.getAlbumList()
                    service.getBanner().data?.forEach {
                        list.add(it.toMediaItem(parentId))
                    }
                    result = NetData(getResult.code, list, getResult.msg)
                }
                TYPE_NETWORK_RECOMMEND -> {
                    val list = mutableListOf<MediaItem>()
                    val getResult = service.getAlbumList()
                    service.getRecommend().data?.forEach {
                        list.add(it.toMediaItem(parentId))
                    }
                    result = NetData(getResult.code, list, getResult.msg)
                }
                TYPE_NETWORK_SHEET -> {
                    val list = mutableListOf<MediaItem>()
                    val getResult = service.getAlbumList()
                    if (userDao.userInRoom() == 0) {
                        emptyList<MediaItem>()
                    } else {
                        service.getUserSheet(userDao.getUserInfo()!!.token).data?.forEach {
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
                NetSheet(0, 0, "网络异常")
            }
        } catch (e: Exception) {
            NetSheet(0, 0, "网络异常")
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
}