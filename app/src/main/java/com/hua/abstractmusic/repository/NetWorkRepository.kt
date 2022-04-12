package com.hua.abstractmusic.repository

import android.annotation.SuppressLint
import android.net.Uri
import androidx.media3.common.MediaItem
import com.hua.abstractmusic.base.repository.BaseRepository
import com.hua.abstractmusic.preference.UserInfoData
import com.hua.abstractmusic.ui.home.net.detail.SearchObject
import com.hua.abstractmusic.utils.toMediaItem
import com.hua.model.sheet.SheetVO
import com.hua.network.api.MusicAPI
import com.hua.network.api.SearchApi
import com.hua.model.other.Constants.NETWORK_ALBUM_ID
import com.hua.model.other.Constants.NETWORK_ARTIST_ID
import com.hua.model.other.Constants.NETWORK_MUSIC_ID
import com.hua.model.other.Constants.NET_SHEET_ID
import com.hua.model.other.Constants.ROOT_SCHEME
import com.hua.model.other.Constants.TYPE_NETWORK_ALBUM
import com.hua.model.other.Constants.TYPE_NETWORK_ARTIST
import com.hua.model.other.Constants.TYPE_NETWORK_BANNER
import com.hua.model.other.Constants.TYPE_NETWORK_MUSIC
import com.hua.model.other.Constants.TYPE_NETWORK_RECOMMEND
import com.hua.model.other.Constants.TYPE_NETWORK_SHEET
import com.hua.network.*
import com.hua.service.MediaItemTree

/**
 * @author : huaweikai
 * @Date   : 2022/04/11
 * @Desc   :
 */
@SuppressLint("UnsafeOptInUsageError")
class NetWorkRepository(
    private val api:MusicAPI,
    private val searchApi:SearchApi,
    private val itemTree: MediaItemTree,
    private val userInfoData: UserInfoData
) :BaseRepository{
    override suspend fun selectTypeList(parentId: Uri): ApiResult<List<MediaItem>> {
        val result = when(parentId.authority) {
            TYPE_NETWORK_ALBUM -> {
                api.getRecommendAlbumList().map { list ->
                    list.map { it.toMediaItem(parentId) }
                }
            }
            TYPE_NETWORK_MUSIC -> {
                api.getAllMusic().map { list->
                    list.map { it.toMediaItem(parentId) }
                }
            }
            TYPE_NETWORK_ARTIST -> {
                api.getArtistList().map { list->
                    list.map { it.toMediaItem(parentId) }
                }
            }
            TYPE_NETWORK_BANNER -> {
                api.getBanner().map { list->
                    list.map { it.toMediaItem(parentId) }
                }
            }
            TYPE_NETWORK_RECOMMEND -> {
                api.getRecommend().map { list->
                    list.map { it.toMediaItem(parentId) }
                }
            }
            TYPE_NETWORK_SHEET -> {
                val getResult = api.getUserSheet(userInfoData.userInfo.value.userToken)
                if (userInfoData.userInfo.value.isLogin) {
                    getResult.map { list->
                        list.map { it.toMediaItem(parentId) }
                    }
                } else {
                    ApiResult.Failure(Error())
                }
            }
            else -> ApiResult.Failure(Error())
        }
        return result.apply {
            onSuccess {
                itemTree.addMusicToTree(parentId.toString(),this.get { emptyList() })
            }
        }
    }

    override suspend fun selectMusicByType(parentId: Uri): ApiResult<List<MediaItem>> {
        val id = parentId.lastPathSegment
        val getResult = if (id == null) {
            ApiResult.Failure(Error())
        } else {
            when (parentId.authority) {
                TYPE_NETWORK_ALBUM -> {
                    api.getMusicByAlbum(id)
                }
                TYPE_NETWORK_ARTIST -> {
                    api.getMusicByArtist(id)
                }
                TYPE_NETWORK_BANNER -> {
                    api.getMusicByAlbum(id)
                }
                TYPE_NETWORK_RECOMMEND -> {
                    api.getMusicBySheetId(id)
                }
                TYPE_NETWORK_SHEET -> {
                    api.getMusicBySheetId(id)
                }
                userInfoData.userInfo.value.userToken -> {
                    api.getMusicBySheetId(id)
                }
                else -> ApiResult.Failure(Error())
            }
        }
        return getResult.map { list->
            list.map { it.toMediaItem(parentId) }
        }.apply {
            onSuccess {
                itemTree.addMusicToTree(parentId.toString(),this.get { emptyList() })
            }
        }
    }

    override suspend fun selectLyrics(id: Uri?): String {
        id?:return ""
        val musicId = id.lastPathSegment ?: return ""
        val result = api.getMusicLyrics(musicId)
        return if(result is ApiResult.Success){
            result.data
        }else{
            ""
        }
    }


    override suspend fun selectArtistByMusicId(item: MediaItem): ApiResult<List<MediaItem>> {
        val parentId = Uri.parse(NETWORK_ARTIST_ID)
        val musicId = Uri.parse(item.mediaId).lastPathSegment ?: return ApiResult.Failure(ApiError.requestError)
        return api.getArtistByMusicId(musicId).map { list->
            list.map { it.toMediaItem(parentId) }
        }
    }

    override suspend fun selectAlbumByMusicId(item: MediaItem): ApiResult<MediaItem> {
        val albumId = item.mediaMetadata.extras?.getLong("albumId") ?: return ApiResult.Failure(ApiError.requestError)
        val result = api.selectAlbumById(albumId.toString())
        val parentId = Uri.parse(NETWORK_ALBUM_ID)
        return result.map {
            it.toMediaItem(parentId)
        }
    }

    override suspend fun selectAlbumByArtist(parentId: Uri): ApiResult<List<MediaItem>> {
        val artistId = parentId.lastPathSegment ?: return ApiResult.Failure(ApiError.requestError)
        val result = api.getAlbumByArtist(artistId)
        val parentUri = Uri.parse(NETWORK_ALBUM_ID)
        return result.map { list ->
            list.map{it.toMediaItem(parentUri)}
        }.apply {
            onSuccess {
                itemTree.addMusicToTree(parentId.toString(),this.get { emptyList() })
            }
        }
    }

    override suspend fun createSheet(title: String): ApiResult<Unit> {
        val token = userInfoData.userInfo.value.userToken
        return api.createNewSheet(title, token)
    }

    override suspend fun insertMusicToSheet(sheetId: String, mediaItem: MediaItem):ApiResult<Unit> {
        val musicId = Uri.parse(mediaItem.mediaId).lastPathSegment ?: return ApiResult.Failure(ApiError.requestError)
        val token = userInfoData.userInfo.value.userToken
        return api.insertMusicToSheet(
            sheetId, musicId, token
        )
    }

    override suspend fun updateSheet(sheet: SheetVO):ApiResult<Unit>{
        val token = userInfoData.userInfo.value.userToken
        return api.updateSheet(token,sheet)
    }

    override suspend fun selectSheetById(parentId: Uri): ApiResult<MediaItem> {
        val sheetId = parentId.lastPathSegment ?: return ApiResult.Failure(ApiError.requestError)
        return api.selectSheetById(sheetId).map { list->
            list.toMediaItem(parentId)
        }
    }

    override suspend fun removeSheetItem(sheetId: String, musicId: String):ApiResult<Unit> {
        val token = userInfoData.userInfo.value.userToken
        return api.deleteMusicFromSheet(sheetId,musicId,token)
    }

    override suspend fun deleteSheet(sheetId: String):ApiResult<Unit> {
        val token = userInfoData.userInfo.value.userToken
        return api.deleteSheet(sheetId,token)
    }

    override suspend fun selectUserSheet(): ApiResult<List<MediaItem>> {
        val user = userInfoData.userInfo.value
        return if(user.isLogin){
            val parentId = Uri.parse("$ROOT_SCHEME${user.userToken}")
            val result = api.getUserSheet(user.userToken)
            result.map { list ->
                list.map { it.toMediaItem(parentId) }
            }
        }else{
            ApiResult.Failure(Error())
        }
    }
    suspend fun search(searchObject: SearchObject):ApiResult<List<MediaItem>>{
        val parentId = Uri.parse(searchObject.parentId)
        return when (searchObject) {
            is SearchObject.Music -> {
                searchApi.searchMusic(searchObject.name).map { list ->
                    list.map { it.toMediaItem(parentId) }
                }
            }
            is SearchObject.Album -> {
                searchApi.searchAlbum(searchObject.name).map { list ->
                    list.map { it.toMediaItem(parentId) }
                }
            }
            is SearchObject.Artist -> {
                searchApi.searchArtist(searchObject.name).map { list ->
                    list.map { it.toMediaItem(parentId) }
                }
            }
            is SearchObject.Sheet -> {
                searchApi.searchSheet(searchObject.name).map { list ->
                    list.map { it.toMediaItem(parentId) }
                }
            }
        }
    }
}