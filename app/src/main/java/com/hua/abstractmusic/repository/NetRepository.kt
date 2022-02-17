package com.hua.abstractmusic.repository

import android.net.Uri
import androidx.media2.common.MediaItem
import com.hua.abstractmusic.net.MusicService
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_ALBUM
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_ALL_MUSIC
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_ARTIST
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_BANNER
import com.hua.abstractmusic.other.Constant.TYPE_NETWORK_RECOMMEND
import com.hua.abstractmusic.utils.toMediaItem

/**
 * @author : huaweikai
 * @Date   : 2022/01/06
 * @Desc   : 在线仓库
 */
class NetRepository(
    private val service: MusicService
) {

    suspend fun selectList(parentId: Uri): List<MediaItem>? {
        return try {
            val result = mutableListOf<MediaItem>()
            when (parentId.authority) {
                TYPE_NETWORK_ALBUM -> {
                    service.getAlbumList().data?.forEach {
                        result.add(it.toMediaItem(parentId))
                    }
                }
                TYPE_NETWORK_ALL_MUSIC -> {
                    service.getAllMusic().data?.forEach {
                        result.add(it.toMediaItem(parentId))
                    }
                }
                TYPE_NETWORK_ARTIST -> {
                    service.getArtistList().data?.forEach {
                        result.add(it.toMediaItem(parentId))
                    }
                }
                TYPE_NETWORK_BANNER -> {
                    service.getBanner().data?.forEach {
                        result.add(it.toMediaItem(parentId))
                    }
                }
                TYPE_NETWORK_RECOMMEND -> {
                    service.getRecommend().data?.forEach {
                        result.add(it.toMediaItem(parentId))
                    }
                }
                else -> emptyList<MediaItem>()
            }
            result
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun selectMusicById(parentId: Uri): List<MediaItem>? {
        return try {
            val result = mutableListOf<MediaItem>()
            val id = parentId.lastPathSegment
            val list = if (id == null) {
                emptyList()
            } else {
                when (parentId.authority) {
                    TYPE_NETWORK_ALBUM -> {
                        service.getMusicByAlbum(id).data
                    }
                    TYPE_NETWORK_ARTIST -> {
                        service.getMusicByArtist(id).data
                    }
                    TYPE_NETWORK_BANNER -> {
                        service.getBannerById(id).data
                    }
                    TYPE_NETWORK_RECOMMEND -> {
                        service.getRecommendList(id).data
                    }
                    else -> emptyList()
                }
            }
            list?.forEach {
                result.add(it.toMediaItem(parentId))
            }
            result
        } catch (e: Exception) {
            emptyList()
        }
    }
}