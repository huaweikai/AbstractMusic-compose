package com.hua.service

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.google.common.collect.ImmutableList
import com.hua.model.other.Constants.LOCAL_ALBUM_ID
import com.hua.model.other.Constants.LOCAL_ALL_ID
import com.hua.model.other.Constants.LOCAL_ARTIST_ID
import com.hua.model.other.Constants.LOCAL_SHEET_ID
import com.hua.model.other.Constants.NETWORK_ALBUM_ID
import com.hua.model.other.Constants.NETWORK_ARTIST_ID
import com.hua.model.other.Constants.NETWORK_BANNER_ID
import com.hua.model.other.Constants.NETWORK_MUSIC_ID
import com.hua.model.other.Constants.NETWORK_RECOMMEND_ID
import com.hua.model.other.Constants.NET_SHEET_ID
import com.hua.model.other.Constants.ROOT_SCHEME
import javax.inject.Inject
import javax.inject.Singleton


/**
 * @author : huaweikai
 * @Date   : 2021/11/26
 * @Desc   : mediaItem树
 */
@SuppressLint("UnsafeOptInUsageError")
@Singleton
class MediaItemTree @Inject constructor(
    private val context: Context,
    private val scanner: MediaStoreScanner
) {

    private var treeNodes: MutableMap<String, MediaItemNode?> = mutableMapOf()

    init {
        initialize()
    }

    private fun initialize() {
        val list = listOf(
            ROOT_SCHEME, LOCAL_ALL_ID, LOCAL_ALBUM_ID,
            LOCAL_ARTIST_ID, LOCAL_SHEET_ID, NETWORK_ALBUM_ID,
            NETWORK_ARTIST_ID, NETWORK_BANNER_ID,
            NETWORK_RECOMMEND_ID, NETWORK_MUSIC_ID, NET_SHEET_ID
        )
        list.forEach {
            treeNodes[it] = MediaItemNode(
                MediaItem.Builder()
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setIsPlayable(false)
                            .setFolderType(MediaMetadata.FOLDER_TYPE_MIXED)
                            .build()
                    )
                    .setMediaId(it)
                    .build()
            )
            if (it != ROOT_SCHEME) {
                treeNodes[ROOT_SCHEME]?.addChild(listOf(treeNodes[it]!!.item))
            }
        }
    }

    fun getRootItem(): MediaItem {
        return treeNodes[ROOT_SCHEME]!!.item
    }

    fun getChildren(parentId: String): List<MediaItem>? {
        return scanner.selectLocalList(context, parentId).apply {
            if (treeNodes[parentId] == null) {
                treeNodes[parentId] = MediaItemNode(
                    MediaItem.Builder().build()
                )
            }
            this?.let {
                treeNodes[parentId]!!.setChild(it)
            }
        }
    }

//    suspend fun networkGetChildren(parentId: String): NetData<List<MediaItem>>? {
//        val parentIdUri = Uri.parse(parentId)
//        return if (parentIdUri.lastPathSegment.isNullOrEmpty()) {
//            scanner.selectList(parentIdUri)
//        } else {
//            if (parentId.contains(Constant.ARTIST_TO_ALBUM)) {
//                scanner.selectAlbumByArtist(parentIdUri)
//            } else {
//                scanner.selectMusicById(parentIdUri)
//            }
//
//        }.apply {
//            if (treeNodes[parentId] == null) {
//                treeNodes[parentId] = MediaItemNode(
//                    MediaItem.Builder().build()
//                )
//            }
//            this?.let {
//                val list = if (it.code == NetWork.SUCCESS) {
//                    it.data!!
//                } else {
//                    emptyList()
//                }
//                treeNodes[parentId]!!.setChild(list)
//            }
//        }
//    }

//    fun addOnLineToTree(bean: HomeBean){
//        treeNodes[NETWORK_BANNER_ID]!!.setChild(bean.banners ?: emptyList())
//        treeNodes[NETWORK_MUSIC_ID]!!.setChild(bean.songs ?: emptyList())
//        treeNodes[NET_SHEET_ID]!!.setChild(bean.sheets ?: emptyList())
//        treeNodes[NETWORK_ALBUM_ID]!!.setChild(bean.albums ?: emptyList())
//    }

    fun addMusicToTree(parentId: String,items:List<MediaItem>?){
        if (treeNodes[parentId] == null) {
            treeNodes[parentId] = MediaItemNode(
                MediaItem.Builder().build()
            )
        }
        treeNodes[parentId]!!.setChild(items ?: emptyList())
    }

    fun getCacheItems(
        parentId: String
    ): List<MediaItem> {
        //想的是既然是回调回来拿数据的，那么数据肯定是存在的，直接返回。
        return treeNodes[parentId]?.getChildren() ?: emptyList()
    }

    fun getItem(mediaId: String): MediaItem? {
        val parentUri = Uri.parse(mediaId)
        val realMediaId = parentUri.lastPathSegment
        val parentIndex = mediaId.lastIndexOf(realMediaId ?: "")
        val parentId = mediaId.substring(0, parentIndex - 1)
        val children = treeNodes[parentId]?.getChildren() ?: return null
        return children.find {
            it.mediaId == mediaId
        }
    }


    class MediaItemNode(val item: MediaItem) {
        private val children = mutableListOf<MediaItem>()

        fun addChild(mediaItems: List<MediaItem>) {
            this.children.addAll(mediaItems)
        }

        fun setChild(mediaItems: List<MediaItem>) {
            children.clear()
            children.addAll(mediaItems)
        }

        fun getChildren(): List<MediaItem> {
            return ImmutableList.copyOf(children)
        }
    }
}