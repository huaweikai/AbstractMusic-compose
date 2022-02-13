package com.hua.abstractmusic.repository

import android.net.Uri
import androidx.media2.common.MediaItem
import com.hua.abstractmusic.bean.MediaData
import com.hua.abstractmusic.net.MusicService
import com.hua.abstractmusic.other.Constant
import com.hua.abstractmusic.utils.toMediaItem

/**
 * @author : huaweikai
 * @Date   : 2022/01/06
 * @Desc   : 在线仓库
 */
class NetRepository(
    private val service: MusicService
) {

    suspend fun selectAlbumList():List<MediaItem>{
        return try {
            val list = mutableListOf<MediaItem>()
            service.getAlbumList().data?.forEach {
                list.add(it.toMediaItem(Constant.NETWORK_ALBUM_ID))
            }
            list
        }catch (e:Exception){
            emptyList()
        }
    }

    suspend fun selectArtistList():List<MediaItem>{
        return try {
            val list = mutableListOf<MediaItem>()
            service.getArtistList().data?.forEach {
                list.add(it.toMediaItem())
            }
            list
        }catch (e:Exception){
            emptyList()
        }
    }

    suspend fun selectMusicByAlbum(parentId:Uri):List<MediaItem>{
        val id = parentId.lastPathSegment
        return try {
            val list = mutableListOf<MediaItem>()
            id?.let {
                service.getMusicByAlbum(it).data?.forEach {
                    list.add(it.toMediaItem(parentId))
                }
            }
            list
        }catch (e:Exception){
            emptyList()
        }
    }

    suspend fun selectMusicByArtist(parentId:Uri):List<MediaItem>{
        val id = parentId.lastPathSegment
        return try {
            val list = mutableListOf<MediaItem>()
            id?.let {
                service.getMusicByArtist(it).data?.forEach {
                    list.add(it.toMediaItem(parentId))
                }
            }
            list
        }catch (e:Exception){
            emptyList()
        }
    }

    suspend fun getBanner():List<MediaItem>{
        return try{
            val list = mutableListOf<MediaItem>()
            service.getBanner().data?.forEach {
                list.add(it.toMediaItem(Constant.NETWORK_BANNER_ID))
            }
            list
        }catch (e:Exception){
            emptyList()
        }
    }

}